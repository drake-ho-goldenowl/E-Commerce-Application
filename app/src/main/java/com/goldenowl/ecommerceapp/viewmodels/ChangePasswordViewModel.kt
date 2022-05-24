package com.goldenowl.ecommerceapp.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.model.User
import com.goldenowl.ecommerceapp.model.UserManager
import com.goldenowl.ecommerceapp.utilities.Hash
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordViewModel(application: Application) : BaseViewModel(application) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userManager: UserManager = UserManager.getInstance(application)
    val validOldPasswordLiveData: MutableLiveData<String> = MutableLiveData()
    val validNewPasswordLiveData: MutableLiveData<String> = MutableLiveData()
    val validRepeatPasswordLiveData: MutableLiveData<String> = MutableLiveData()
    val validChangePasswordLiveData: MutableLiveData<Boolean> = MutableLiveData()


    fun changePassword(passwordText: String,passwordText2: String) {
        validChangePasswordLiveData.postValue(false)
        val account = userManager.getUser()
        val user = firebaseAuth.currentUser
        val password = Hash.hashSHA256(passwordText)
        account.password = password

        val credential : AuthCredential = EmailAuthProvider.getCredential(
            account.email,passwordText2
        )

        user!!.reauthenticate(credential).addOnCompleteListener {task->
            if(task.isSuccessful){
                user.updatePassword(passwordText).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        userManager.setPassword(passwordText)
                        User.writeProfile(account)
                        toastMessage.postValue("User password updated")
                        validChangePasswordLiveData.postValue(true)
                    }
                }.addOnFailureListener {
                    toastMessage.postValue(it.toString())
                }
            }
        }
    }

    fun forgotPassword(){
        firebaseAuth.sendPasswordResetEmail(userManager.getEmail())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toastMessage.postValue("Email sent.")
                }
            }
    }

    fun checkOldPassword(passwordText: String) {
        if (Hash.hashSHA256(passwordText) != userManager.getPassword()) {
            validOldPasswordLiveData.postValue("Not the same as the old password")
        } else {
            validOldPasswordLiveData.postValue("")
        }
    }

    fun checkRepeatPassword(passwordText1: String, passwordText2: String) {
        if (passwordText1 != passwordText2) {
            validRepeatPasswordLiveData.postValue("Not the same as the new password")
        } else {
            validRepeatPasswordLiveData.postValue("")
        }
    }

    fun validPassword(passwordText: String, passwordText2: String) {
        if (passwordText.length < 8) {
            validNewPasswordLiveData.postValue("Minimum 8 Character Password")
        } else if (!passwordText.matches(".*[A-Z].*".toRegex())) {
            validNewPasswordLiveData.postValue("Must Contain 1 Upper-case Character")
        } else if (!passwordText.matches(".*[a-z].*".toRegex())) {
            validNewPasswordLiveData.postValue("Must Contain 1 Lower-case Character")
        } else if (passwordText == passwordText2) {
            validNewPasswordLiveData.postValue("Mustn't same as the old password")
        } else validNewPasswordLiveData.postValue("")

    }

    companion object {
        const val TAG = "CHANGEPASSWORDVIEWMODEL"
    }

}

