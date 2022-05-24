package com.goldenowl.ecommerceapp.model

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class User(
    var name: String,
    val email: String,
    var password: String,
    val token: String,
    var dob: String = "",
    var avatar: String = ""
) {
    constructor() : this("", "", "", "", "","")

    companion object {
        private const val TAG = "USER"
        private val db = Firebase.firestore
        fun writeProfile(user: User) {
            db.collection("users").document(user.token)
                .set(user)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot added")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }
}