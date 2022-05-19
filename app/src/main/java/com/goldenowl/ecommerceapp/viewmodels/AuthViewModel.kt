package com.goldenowl.ecommerceapp.viewmodels

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class AuthViewModel : BaseViewModel() {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val userLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    init {
        if (firebaseAuth.currentUser != null) {
            userLiveData.postValue(firebaseAuth.currentUser)
        }
    }

    fun signUp(email: String, password: String){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    userLiveData.postValue(firebaseAuth.currentUser)
                    toastMessage.postValue("Registration Success")
                } else {
                    toastMessage.postValue("Registration Failure: " + task.exception)
                }
            }
    }

    fun logIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    userLiveData.postValue(firebaseAuth.currentUser)
                    toastMessage.postValue("Login Success")

                } else {
                    toastMessage.postValue("Login Failure: " + task.exception)
                }
            }
    }
    fun logOut() {
        firebaseAuth.signOut()
        userLiveData.postValue(null)
    }
}