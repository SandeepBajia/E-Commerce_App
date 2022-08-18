package com.example.e_commerceapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_my_product.*


class MyProductFragment : Fragment() , productItemClicked {

    val myProdRef = Firebase.database.reference.child("User").child(FirebaseAuth.getInstance().currentUser?.uid!!)
        .child("MyProduct")
    val myProductAdapter = ProductAdapter(this)
    val myProducts = ArrayList<Product>()
    val myproductKey = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycleviewMyProduct.layoutManager = LinearLayoutManager(context)
        recycleviewMyProduct.adapter = myProductAdapter
        myProdRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(activity != null) {
                    progressBarMyProduct.visibility = View.GONE
                    val product = snapshot.child("ProductDetail").getValue<Product>()
                    myProductAdapter.onUpdateProduct(product!!)
                    myProducts.add(product)
                    myproductKey.add(snapshot.child("KeyDetail").getValue<String>()!!)
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val product = snapshot.child("ProductDetail").getValue<Product>()
                val position = myproductKey.indexOf(snapshot.key!!)
                myProductAdapter.onUpdateExistingProduct(position , product!!)
                myProducts.set( position , product)
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                val product = snapshot.child("ProductDetail").getValue<Product>()
               myProductAdapter.onRemoveProduct(product!!)
                myProducts.remove(product)
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {TODO("Not yet implemented") }
            override fun onCancelled(error: DatabaseError) {TODO("Not yet implemented") }
        })
    }

    override fun onItemClicked(position: Int) {
        super.onItemClicked(position)
        val intent = Intent(context , SellerProductActivity::class.java)
        intent.putExtra("Parceable" , myProducts[position])
        intent.putExtra("message" , "myproduct")
        intent.putExtra("key" , myproductKey[position])
        startActivity(intent)
    }


}