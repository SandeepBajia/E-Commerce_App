package com.example.e_commerceapp

import android.graphics.Paint
import android.location.Address
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.e_commerceapp.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_product.*

class ProductActivity : AppCompatActivity() {

    private  val userRef = Firebase.database.getReference().child("User").child(FirebaseAuth.getInstance().currentUser?.uid!!)
    private val mCurrentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        var product : Product? = getIntent().getParcelableExtra<Product>("parceable")
        var message = intent.getStringExtra("message")


            if( message == "MyOrder") {
                changePhoneNumber.visibility = View.GONE
                changeAddress.visibility = View.GONE
                buttonLayout.visibility = View.GONE
                val key = intent.getStringExtra("userdetail")

                userRef.child("MyOrder").child(key!!).child("UserDetail").addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user:User? = snapshot.getValue<User>()
                        phoneNumber.text = user?.userPhoneNumber
                        address.text = user?.userAddress
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })


            }

            if( message == "cart" || message == "User") {

                deliverystatus.visibility = View.GONE
                deliveryStatus.visibility = View.GONE
                if( message == "cart") {
                    addCart.text = "Remove"
                    addCart.setOnClickListener { removeProductFromCart(product!!) }
                }
                if( message == "User") {
                    addCart.setOnClickListener { addProductToCart(product!!) }
                }



                submitOrder.setOnClickListener { submitorder(product!!) }
                changePhoneNumber.setOnClickListener{ changePhone() }
                changeAddress.setOnClickListener { changeAddress()  }

                userRef.child("Userdetail").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue<User>() as User
                        phoneNumber.text = user.userPhoneNumber
                        address.text = user.userAddress
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")

                    }
                })

            }

        if( product != null ) {
            Glide.with(this).load(product.imageUrl).into(imageviewProduct)
            nameProduct.text = product.productName
            statusProduct.text = product.productStatus
            descriptionProduct.text = product.productDescription
            priceProductReal.text = product.productPriceReal
            priceProductReal.setPaintFlags(priceProductReal.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
            priceProductDiscount.text = product.discountProductPrice
            percentDiscount.text = product.percentageOff + "% Off"
            ratingBarProduct.rating = 4f
        }


    }

    private fun addProductToCart(product:Product) {
        userRef.child("Cart").child(product.productName.toString()).setValue(product).addOnCompleteListener {
            Toast.makeText(this, "Product added to cart" , Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeProductFromCart(product:Product) {
        userRef.child("Cart").child(product.productName.toString()).removeValue().addOnCompleteListener{
            Toast.makeText(this, "Product removed to cart" , Toast.LENGTH_SHORT).show()
            finish()
        }

    }



    private fun submitorder(product: Product) {

        val user = User(mCurrentUser?.uid , mCurrentUser?.displayName ,
                mCurrentUser?.email , phoneNumber.text.toString() , address.text.toString() )

        // add order in seller Order Section
        var databaseRef = Firebase.database.getReference().child("User").child(product.sellerId!! )
            .child("Order")
        val orderProduct:Product = Product(product.imageUrl , product.productName , product.productPriceReal ,
            product.discountProductPrice , product.percentageOff, product.productStatus
            , product.productDescription , product.sellerId , FirebaseAuth.getInstance().currentUser?.uid)
        val sellerRef = databaseRef.push().key
        databaseRef.child(sellerRef!!).child("CustomerDetail").setValue(user)
        databaseRef.child(sellerRef).child("productDetail").setValue(orderProduct)

        //add order in customer 's My Order Section
        var userRef = Firebase.database.getReference().child("User")
            .child(FirebaseAuth.getInstance().currentUser?.uid!!).child("MyOrder")
        val customerRef = userRef.push().key
        userRef.child(customerRef!!).child("ProductDetail").setValue(product)
        userRef.child(customerRef).child("UserDetail").setValue(User(mCurrentUser?.uid , mCurrentUser?.displayName ,
        mCurrentUser?.email , phoneNumber.text.toString() , address.text.toString() ))
        Toast.makeText(this , "Ordered Successfully" , Toast.LENGTH_SHORT).show()
    }

    private fun changePhone() {
        EditPhone.visibility = View.VISIBLE
        EditPhone.setText(phoneNumber.text)
        phoneNumber.visibility = View.GONE
        changePhoneNumber.setOnClickListener { setPhone() }
    }
    private fun setPhone( ) {
        userRef.child("Userdetail").child("userPhoneNumber").setValue(EditPhone.text.toString())
        phoneNumber.visibility = View.VISIBLE
        phoneNumber.setText(EditPhone.text)
        EditPhone.visibility = View.GONE
        changePhoneNumber.setOnClickListener { changePhone() }
    }

    private fun changeAddress() {
        EditAddress.visibility = View.VISIBLE
        EditAddress.setText(address.text)
        address.visibility = View.GONE
        changeAddress.setOnClickListener { setAddress() }
    }
    private fun setAddress() {
        userRef.child("Userdetail").child("userAddress").setValue(EditAddress.text.toString())
        address.visibility = View.VISIBLE
        address.text = EditAddress.text
        EditAddress.visibility = View.GONE
        changeAddress.setOnClickListener { changeAddress() }
    }

}


