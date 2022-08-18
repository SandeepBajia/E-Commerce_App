package com.example.e_commerceapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.example.e_commerceapp.daos.UserDao
import com.example.e_commerceapp.model.User
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity() {
    private lateinit var googleSignInClient:GoogleSignInClient
    private lateinit var mauth : FirebaseAuth
    private  var RC_Sign_In = 123



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mauth = Firebase.auth


       val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
           .requestIdToken("923122756418-foih0l89n4b16cfrb9hib4ruc708rgp7.apps.googleusercontent.com")
           .requestEmail()
           .build()

         googleSignInClient =GoogleSignIn.getClient(this , gso)
        signInButton.setOnClickListener { signIn() }
    }

    override fun onResume() {
        super.onResume()
        if(mauth.currentUser != null ) {
            updateUI(mauth.currentUser)
        }
    }

    private fun signIn() {

        val signInIntent =  googleSignInClient.signInIntent
        startActivityForResult(signInIntent , RC_Sign_In)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode , resultCode , data)
        if( requestCode == RC_Sign_In) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if(task.isSuccessful) {
                handleSignInResult(task)
            }
        }


    }


    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            if( account != null ) {
                firebaseAuthWithGoogle(account.idToken!!)
            }
        }
        catch( e:ApiException) {
            Log.w(TAG , "sign failed")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        signInButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        mauth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mauth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }

    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if( firebaseUser != null ) {
            val user = User(
                firebaseUser.uid,
                firebaseUser.displayName,
                firebaseUser.email,
                firebaseUser.phoneNumber
            )
            val userDao = UserDao()
            userDao.addUser(user)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        else {
            signInButton.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }


}