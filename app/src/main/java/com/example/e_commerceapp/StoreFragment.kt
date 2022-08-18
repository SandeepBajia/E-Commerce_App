package com.example.e_commerceapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_store.*


class StoreFragment : Fragment() , productItemClicked {

    val database = Firebase.database
    val productRef = database.getReference().child("Product")
    val auth = FirebaseAuth.getInstance()
    val  products : ArrayList<Product> = ArrayList()
    val productAdapter  = ProductAdapter( this)
    lateinit var mChildEventListener: ChildEventListener
    var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_store, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycleview.layoutManager = LinearLayoutManager(view.context)
        val productAdapter  = ProductAdapter( this)
        recycleview.adapter = productAdapter



        mChildEventListener = object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if( activity != null ) {
                    count++
                    if (count == 1) {
                        homeProgressBar.visibility = View.GONE
                    }
                    var newProduct = snapshot.getValue<Product>()
                    products.add(newProduct!!)
                    productAdapter.onUpdateProduct(newProduct)
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        productRef.addChildEventListener(mChildEventListener)

    }



    override fun onItemClicked(position:Int) {

        val intent = Intent( context , ProductActivity::class.java)
        val product = products[position]
        intent.putExtra("parceable" , product)
        intent.putExtra("message"  , "User")
        startActivity(intent)
    }

}