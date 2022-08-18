package com.example.e_commerceapp.daos

import com.example.e_commerceapp.model.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserDao {
    var userReference = Firebase.database.reference.child("User")

    fun addUser(user : User?) {
        user?.let {

            userReference = userReference.child(it.userId!!).child("Userdetail")
            userReference.child("username").setValue(it.username)
            userReference.child("userId").setValue(it.userId)
            userReference.child("userEmail").setValue(it.userEmail)


        }
    }
}