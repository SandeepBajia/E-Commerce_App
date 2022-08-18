package com.example.e_commerceapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.fragment_my_cart.*
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.fragment_store.*


class MyCartFragment : Fragment() , productItemClicked {

    private val myCartRef = Firebase.database.getReference().child("User").child(FirebaseAuth.getInstance().currentUser?.uid!!)
        .child("Cart")
    private val myCartAdapter = ProductAdapter(this)
    val  myCartProducts : ArrayList<Product> = ArrayList()
    var count : Long = 0
    var totalprice = 0
    var totalDiscounted = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_my_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myCartRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d( "removess" , snapshot.childrenCount.toString())
                if(snapshot.childrenCount == count   ) {
                    if( activity != null) {
                        cartProgressBar.visibility = View.INVISIBLE
                        emptyCartImage.visibility = View.VISIBLE
                        emptyCartText.visibility = View.VISIBLE
                    }
                }
                else {
                    if( activity != null ) {
                        loadData(snapshot.childrenCount)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        recycleviewMyCart.layoutManager = LinearLayoutManager(context)
        recycleviewMyCart.adapter = myCartAdapter

    }



    private fun setPriceDetail() {
        if( activity == null) {
            return
        }
        cartProgressBar.visibility= View.INVISIBLE
        PriceDetail.visibility= View.VISIBLE

        for( i in myCartProducts) {
            if( i.productPriceReal != null && i.discountProductPrice != null ) {
                totalprice += i.productPriceReal.toInt()
                val temp =  i.discountProductPrice
                totalDiscounted += (temp.substring(0 ,temp.length-1)).toInt()
            }
        }
        setPrice()
    }

    private fun setPrice() {
        if(activity == null ) {
            return
        }
        totalPrice.text = "Total Amount - " +  totalprice.toString() + getString(R.string.rupee)
        val totalDiscount = totalprice - totalDiscounted
        totalDiscounts.text = "Total Discount - " + totalDiscount.toString() + getString(R.string.rupee)
        if( totalDiscounted >= 500 ) {
            totalDelivery.text = "Total Delivery Charge -" +  '0' + getString(R.string.rupee)
        }
        else {
            totalDelivery.text = "Total Delivery Charge -" +  "50" + getString(R.string.rupee)
        }
        totalAmount.text = "Total Amount - " + totalDiscounted.toString() + getString(R.string.rupee)
    }

    private fun loadData(size :Long ) {
        myCartRef.addChildEventListener(object:ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var product = snapshot.getValue<Product>()
                myCartProducts.add(product!!)
                myCartAdapter.onUpdateProduct(product)
                count++;
                if( count == size ) {
                    setPriceDetail()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { TODO("Not yet implemented") }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                 val removeProduct = snapshot.getValue<Product>() as Product
                myCartAdapter.onRemoveProduct(removeProduct)
                myCartProducts.remove(removeProduct)
                if(myCartProducts.size == 0 && activity != null) {
                    PriceDetail.visibility= View.INVISIBLE
                    emptyCartImage.visibility = View.VISIBLE
                    emptyCartText.visibility = View.VISIBLE
                }
                if(removeProduct.productPriceReal != null && removeProduct.discountProductPrice != null && activity != null) {
                    totalprice -= removeProduct.productPriceReal.toInt()
                    val temp =  removeProduct.discountProductPrice
                    totalDiscounted -= (temp.substring(0 ,temp.length-1)).toInt()
                    setPrice()
                }
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {TODO("Not yet implemented") }
            override fun onCancelled(error: DatabaseError) {TODO("Not yet implemented") }
        })
    }

    override fun onItemClicked(position:Int) {

        val intent = Intent( context , ProductActivity::class.java)
        val product = myCartProducts[position]
        intent.putExtra("parceable" , product)
        intent.putExtra("message" , "cart")
        startActivity(intent)
    }

}