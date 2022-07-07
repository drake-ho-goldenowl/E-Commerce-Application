package com.goldenowl.ecommerceapp.ui.Setting

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.data.UserManager
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    val userManager: UserManager,
    private val db : FirebaseFirestore
    ) : BaseViewModel() {
    val validNameLiveData: MutableLiveData<String> = MutableLiveData()
    private var storageReference: StorageReference = FirebaseStorage.getInstance().reference
    fun updateName(nameText: String) {
        if (!validName(nameText)) return
        val user = userManager.getUser()
        user.name = nameText
        userManager.setName(nameText)
        userManager.writeProfile(db, user)
    }

    fun updateDOB(dobText: String) {
        val user = userManager.getUser()
        user.dob = dobText
        userManager.setDOB(dobText)
        userManager.writeProfile(db, user)
    }

    private fun updateAvatar(avatarURL: String) {
        val user = userManager.getUser()
        user.avatar = avatarURL
        userManager.setAvatar(avatarURL)
        userManager.writeProfile(db, user)
    }

    private fun isLettersOrDigit(string: String): Boolean {
        return string.filter { it.isLetterOrDigit() }.length == string.length
    }

    fun uploadImage(filePath: Uri?, token: String) {
        if (filePath != null) {
            val ref = storageReference.child("avatar/$token")
            val uploadTask = ref.putFile(filePath)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    updateAvatar(downloadUri.toString())
                } else {
                    // Handle failures
                    // ...
                }
            }
        } else {
            toastMessage.postValue("Please Upload an Image")
        }
    }

    fun checkLoginWithFbOrGoogle(): Boolean {
        if (userManager.getPassword() == "") return true
        return false
    }

    fun validName(nameText: String): Boolean {
        return if (nameText.isBlank()) {
            validNameLiveData.postValue("Mustn't empty")
            false
        } else if (!isLettersOrDigit(nameText)) {
            validNameLiveData.postValue("Mustn't Contain Special Character")
            false
        } else {
            validNameLiveData.postValue("")
            true
        }
    }
}