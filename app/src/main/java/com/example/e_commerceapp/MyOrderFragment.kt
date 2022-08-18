package com.example.e_commerceapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_my_cart.*
import kotlinx.android.synthetic.main.fragment_my_order.*

class MyOrderFragment : Fragment() , productItemClicked {
    private val myOrderRef = Firebase.database.getReference().child("User").child(FirebaseAuth.getInstance().currentUser?.uid!!)
        .child("MyOrder")
    private val myOrderAdapter = ProductAdapter(this)
    val  myOrderProducts : ArrayList<Product> = ArrayList()
    val userDetailArrayList = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_my_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycleviewMyOrder.adapter = myOrderAdapter
        recycleviewMyOrder.layoutManager = LinearLayoutManager(context)
        myOrderRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var product = snapshot.child("ProductDetail").getValue<Product>()
                userDetailArrayList.add( snapshot.key!!)
                if(product != null ) {
                    myOrderProducts.add(product)
                    myOrderAdapter.onUpdateProduct(product)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { TODO("Not yet implemented") }
            override fun onChildRemoved(snapshot: DataSnapshot) { TODO("Not yet implemented") }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {TODO("Not yet implemented") }
            override fun onCancelled(error: DatabaseError) {TODO("Not yet implemented") }
        })

    }

    override fun onItemClicked(position:Int) {
        val intent = Intent( context , ProductActivity::class.java)
        val product = myOrderProducts[position]
        intent.putExtra("parceable" , product)
        intent.putExtra("message" , "MyOrder")
        intent.putExtra("userdetail" , userDetailArrayList[position])
        startActivity(intent)
    }


}