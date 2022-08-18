   package com.example.e_commerceapp


import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.header_layout.*


   class MainActivity : AppCompatActivity(), productItemClicked {


       val database = Firebase.database
       val productRef = database.getReference().child("Product")
       lateinit var actionBarDrawerToggle : ActionBarDrawerToggle
       val auth = FirebaseAuth.getInstance()
       val  products : ArrayList<Product> = ArrayList()
       val productAdapter  = ProductAdapter( this)

       override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

          // set Action bar
           setSupportActionBar(toolbar)

           // set Title
          toolbar.setTitleTextColor(resources.getColor(R.color.white))
           title = "Store"
           addFragment(StoreFragment())

            // set up of actionBarToggle
           actionBarDrawerToggle = ActionBarDrawerToggle(this , drawer_layout , toolbar, R.string.nav_open , R.string.nav_close)
           drawer_layout.addDrawerListener(actionBarDrawerToggle)
           actionBarDrawerToggle.syncState()
           actionBarDrawerToggle.drawerArrowDrawable.color = resources.getColor(R.color.white)

           // set up of navigationview
           navigationview.getHeaderView(0).findViewById<TextView>(R.id.headerUserEmail).text =
               FirebaseAuth.getInstance().currentUser?.email

           navigationview.getHeaderView(0).setOnClickListener {
               addFragment(ProfileFragment())
               title = "Profile"
               drawer_layout.closeDrawer(GravityCompat.START)
           }

        navigationview.setNavigationItemSelectedListener( object : NavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                if( item.itemId == R.id.store) {
                    addFragment(StoreFragment())
                    title = "Store"
                }
               if( item.itemId == R.id.profile) {
                   addFragment(ProfileFragment())
                   title = "Profile"
               }
               else if(item.itemId == R.id.logout) {
                   auth.signOut()
                   finish()

               }
               else if(item.itemId == R.id.myCart) {
                   addFragment(MyCartFragment())
                   title = "MY Cart"
               }

               else if( item.itemId == R.id.myProduct) {
                   addFragment(MyProductFragment())
                   title = "My Product"
               }
               else if(item.itemId == R.id.notification) {
                   addFragment(Notification())
                   title = "Notification"
               }

               else if(item.itemId == R.id.addNewProduct) {
                   val intent = Intent( this@MainActivity , AddNewProduct::class.java)
                   startActivity(intent)
               }
               else if( item.itemId == R.id.myOrder) {
                   addFragment(MyOrderFragment())
                   title = "My Order"
               }
                drawer_layout.closeDrawer(GravityCompat.START)
                return true
            }
        })

    }

      
       override fun onOptionsItemSelected(item: MenuItem): Boolean {
           if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
               return true
           }

           return super.onOptionsItemSelected(item)
       }

        fun  addFragment(fragment: Fragment) {
           val ft = supportFragmentManager.beginTransaction()
           ft.replace(R.id.customerFrame , fragment)
           ft.commit()

       }

       override fun onItemClicked(position:Int) {

           val intent = Intent( this , ProductActivity::class.java)
           val product = products[position]
           intent.putExtra("parceable" , product)
           intent.putExtra("message" , "User")
           startActivity(intent)


       }

       override fun onBackPressed() {

           if( drawer_layout.isDrawerOpen(GravityCompat.START)) {
               drawer_layout.closeDrawer(GravityCompat.START)
           }
           else {
               super.onBackPressed()
           }
       }



}


