package com.example.e_commerceapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_new_product.*


class AddNewProduct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_product)

        // set up of autoCompleteTextView
        var status = getString(R.string.available)
        val availability = resources.getStringArray(R.array.ProductAvailability)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, availability)
        autoCompleteTextView.setAdapter(arrayAdapter)
        autoCompleteTextView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p0 != null) {
                    status = p0.getItemAtPosition(p2) as String
                }
            }
        })


        // set title
        val message = intent.getStringExtra("message")
        val key = intent.getStringExtra("key")
        if( message == "update") {
            Toast.makeText(this , key , Toast.LENGTH_LONG).show()
            title = "Update Product"
            val product = intent.getParcelableExtra<Product>("parceable")
            if( product != null  ) {
                EditAddName.setText(product.productName)
                EditAddRealPrice.setText(product.productPriceReal)
                val temp = product.discountProductPrice
                if( temp != null) {
                EditAddDiscountPrice.setText(temp.substring( 0 , temp.lastIndex ))
                }
                EditAddDescription.setText(product.productDescription)
                imageUrl.setText(product.imageUrl)
                status = product.productStatus!!
            }
        }
        else {
            title = "Add New Product"
        }



        submitProduct.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (checkAllField()) {
                    var discount:Int = ((EditAddRealPrice.text.toString().toInt())-(EditAddDiscountPrice.text.toString().toInt()))*100/(
                            EditAddRealPrice.text.toString().toInt()
                            )
                    val p = Product(
                        imageUrl.text.toString(),
                        EditAddName.text.toString(),
                        EditAddRealPrice.text.toString(),
                        EditAddDiscountPrice.text.toString() + getString(R.string.rupee),
                        discount.toString() ,
                        status ,
                        EditAddDescription.text.toString(),
                        FirebaseAuth.getInstance().currentUser?.uid
                    )
                    if( message == "update") {
                        val firebaseDatabase = Firebase.database.getReference().child("Product").child(key!!)
                            .setValue(p)

                        val userOrderRef = Firebase.database.reference.child("User").child(FirebaseAuth.getInstance().currentUser?.uid!!).child("MyProduct")
                            .child(key)
                        userOrderRef.child("ProductDetail").setValue(p)
                       finish()

                    }
                    else {
                        val firebaseDatabase = Firebase.database.getReference().child("Product")
                        val prodRef = firebaseDatabase.push().key
                        firebaseDatabase.child(prodRef!!).setValue(p)
                        val userOrderRef = Firebase.database.reference.child("User")
                            .child(FirebaseAuth.getInstance().currentUser?.uid!!).child("MyProduct")
                            .child(prodRef)
                        userOrderRef.child("ProductDetail").setValue(p)
                        userOrderRef.child("KeyDetail").setValue(prodRef)
                        EditAddName.setText("")
                        EditAddRealPrice.setText("")
                        EditAddDiscountPrice.setText("")
                        EditAddDescription.setText("")
                        imageUrl.setText("")
                        autoCompleteTextView.setText(R.string.available)
                        Toast.makeText(
                            this@AddNewProduct,
                            "Product Added Successfuly" + discount.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
    private fun checkAllField() : Boolean {
        var isAllFieldRight = true
        if(EditAddName.length() == 0) {
            EditAddName.setError("Please Enter Product Name ")
            isAllFieldRight = false
        }
        if(EditAddRealPrice.length() == 0) {
            EditAddRealPrice.setError("Please Enter Product Price")
            isAllFieldRight = false
        }
        if( EditAddDiscountPrice.length() == 0 ) {
            EditAddDiscountPrice.setError("Please Enter Discount Price")
            isAllFieldRight = false
        }
        if(EditAddDescription.length() == 0) {
            EditAddDescription.setError("Please Enter Product Description")
            isAllFieldRight = false
        }
        if(imageUrl.length() == 0) {
            imageUrl.setError("Please Enter Product Image Url")
            isAllFieldRight = false
        }
        return isAllFieldRight
    }
    }
