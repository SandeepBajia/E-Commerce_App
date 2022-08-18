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
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_notification.*


class Notification : Fragment() , productItemClicked {

    val receiveOrderRef = Firebase.database.getReference().child("User").child(FirebaseAuth.getInstance().currentUser?.uid!!).child("Order")
    val receiveOrderProducts = ArrayList<Product>()
    val userDetailKey = ArrayList<String>()
    val productAdapter = ProductAdapter(  this)



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationRecycleView.layoutManager = LinearLayoutManager(view.context)
        notificationRecycleView.adapter = productAdapter
        receiveOrderRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var orderProduct = snapshot.child("productDetail").getValue<Product>()
                receiveOrderProducts.add(orderProduct!!)
                productAdapter.onUpdateProduct(orderProduct )
                userDetailKey.add(snapshot.key!!)

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        return view
    }

    override fun onItemClicked(position:Int) {
        val intent = Intent( context , SellerProductActivity::class.java)
        val product = receiveOrderProducts[position]
        intent.putExtra("Parceable" , product)
        intent.putExtra("key" , userDetailKey[position])
        startActivity(intent)
    }


}