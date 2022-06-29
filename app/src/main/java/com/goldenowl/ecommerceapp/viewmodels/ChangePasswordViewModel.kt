package com.goldenowl.ecommerceapp.viewmodels

import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.data.UserManager
import com.goldenowl.ecommerceapp.utilities.Hash
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val userManager: UserManager,
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) :
    BaseViewModel() {
    val validOldPasswordLiveData: MutableLiveData<String> = MutableLiveData()
    val validNewPasswordLiveData: MutableLiveData<String> = MutableLiveData()
    val validRepeatPasswordLiveData: MutableLiveData<String> = MutableLiveData()
    val validChangePasswordLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun changePassword(
        newPasswordText: String,
        repeatPasswordText: String,
        passwordOldText: String
    ) {
        if (!checkOldPassword(passwordOldText)
            || !validPassword(newPasswordText, passwordOldText)
            || !checkRepeatPassword(repeatPasswordText, newPasswordText)
        ) {
            return
        }

        validChangePasswordLiveData.postValue(false)
        val account = userManager.getUser()
        val user = firebaseAuth.currentUser
        val password = Hash.hashSHA256(newPasswordText)
        account.password = password

        val credential: AuthCredential = EmailAuthProvider.getCredential(
            account.email, passwordOldText
        )

        user?.reauthenticate(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user.updatePassword(newPasswordText).addOnCompleteListener {
                    if (it.isSuccessful) {
                        userManager.setPassword(newPasswordText)
                        userManager.writeProfile(db, account)
                        toastMessage.postValue("User password updated")
                        validChangePasswordLiveData.postValue(true)
                    }
                }.addOnFailureListener {
                    toastMessage.postValue(it.toString())
                }
            }
        }
    }

    fun forgotPassword() {
        firebaseAuth.sendPasswordResetEmail(userManager.getEmail())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toastMessage.postValue("Email sent.")
                }
            }
    }


    fun checkOldPassword(passwordText: String): Boolean {
        return when {
            passwordText.isEmpty() -> {
                validOldPasswordLiveData.postValue("Mustn't empty")
                return false
            }
            Hash.hashSHA256(passwordText) != userManager.getPassword() -> {
                validOldPasswordLiveData.postValue("Not the same as the old password")
                false
            }
            else -> {
                validOldPasswordLiveData.postValue("")
                true
            }
        }
    }

    fun checkRepeatPassword(passwordRepeatText: String, passwordNewText: String): Boolean {
        return when {
            passwordRepeatText.isEmpty() -> {
                validRepeatPasswordLiveData.postValue("Mustn't empty")
                false
            }
            passwordRepeatText != passwordNewText -> {
                validRepeatPasswordLiveData.postValue("Not the same as the new password")
                false
            }
            else -> {
                validRepeatPasswordLiveData.postValue("")
                true
            }
        }
    }

    fun validPassword(passwordNewText: String, passwordOldText: String): Boolean {
        if (passwordNewText.length < 8) {
            validNewPasswordLiveData.postValue("Minimum 8 Character Password")
            return false
        } else if (!passwordNewText.matches(".*[A-Z].*".toRegex())) {
            validNewPasswordLiveData.postValue("Must Contain 1 Upper-case Character")
            return false
        } else if (!passwordNewText.matches(".*[a-z].*".toRegex())) {
            validNewPasswordLiveData.postValue("Must Contain 1 Lower-case Character")
            return false
        } else if (passwordNewText == passwordOldText) {
            validNewPasswordLiveData.postValue("Mustn't same as the old password")
            return false
        } else {
            validNewPasswordLiveData.postValue("")
            return true
        }
    }
}

