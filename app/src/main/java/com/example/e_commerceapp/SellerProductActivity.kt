package com.example.e_commerceapp

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.e_commerceapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.activity_seller_product.*

class SellerProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_product)


        // set up product detail
        val product = intent.getParcelableExtra<Product>("Parceable")
        nameProductSeller.text = product?.productName
        descriptionProductSeller.text = product?.productDescription
        priceProductRealSeller.text = product?.productPriceReal
        priceProductRealSeller.setPaintFlags(priceProductRealSeller.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        priceProductDiscountSeller.text = product?.discountProductPrice
        percentDiscountSeller.text = product?.percentageOff + "% Off"
        statusProductSeller.text = product?.productStatus
        Glide.with(this).load(product?.imageUrl).into(imageviewProductSeller)

        val message = intent.getStringExtra("message")
        val key = intent.getStringExtra("key")
        if (message == "myproduct") {
            userLayout.visibility = View.GONE
            dropdownDelivery.visibility = View.GONE
            deliveryStatusSeller.visibility = View.GONE

            // set update button
            updateProduct.setOnClickListener {
                val intent = Intent( this , AddNewProduct::class.java)
                intent.putExtra("message" , "update")
                intent.putExtra("parceable" , product)
                intent.putExtra("key" , key)
                startActivity(intent)
                finish()
            }

            deleteProduct.setOnClickListener {
                val firebaseDatabase = Firebase.database.getReference().child("Product").child(key!!)
                    .removeValue()

                val userOrderRef = Firebase.database.reference.child("User").child(FirebaseAuth.getInstance().currentUser?.uid!!).child("MyProduct")
                    .child(key).removeValue()
                finish()
            }

        } else {
            updateProduct.visibility = View.GONE
            deleteProduct.visibility = View.GONE
            // set up user detail

            val userKey = intent.getStringExtra("key")
            val userRef = Firebase.database.reference.child("User")
                .child(FirebaseAuth.getInstance().currentUser?.uid!!)
                .child("Order").child(userKey!!).child("CustomerDetail")


            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue<User>()
                    userName.text = user?.username
                    userEmail.text = user?.userEmail
                    userAddress.text = user?.userAddress
                    userPhone.text = user?.userPhoneNumber
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            // set up delivery status

            val arrayAdapter = ArrayAdapter(
                this,
                R.layout.dropdown_item,
                resources.getStringArray(R.array.deliveryStatusOption)
            )
            autoCompleteTextViewDeliveryStatus.setAdapter(arrayAdapter)

        }
    }
}