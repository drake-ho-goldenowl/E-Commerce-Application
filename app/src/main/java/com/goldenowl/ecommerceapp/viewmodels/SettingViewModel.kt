package com.goldenowl.ecommerceapp.viewmodels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.goldenowl.ecommerceapp.data.UserManager
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SettingViewModel(val userManager: UserManager) : BaseViewModel() {
    val validNameLiveData: MutableLiveData<String> = MutableLiveData()
    private var storageReference: StorageReference = FirebaseStorage.getInstance().reference

    fun updateName(nameText: String) {
        if(!validName(nameText)) return
        val user = userManager.getUser()
        user.name = nameText
        userManager.setName(nameText)
        userManager.writeProfile(user)
    }

    fun updateDOB(dobText: String) {
        val user = userManager.getUser()
        user.dob = dobText
        userManager.setDOB(dobText)
        userManager.writeProfile(user)
    }

    private fun updateAvatar(avatarURL: String) {
        val user = userManager.getUser()
        user.avatar = avatarURL
        userManager.setAvatar(avatarURL)
        userManager.writeProfile(user)
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


class SettingViewModelFactory(private val userManager: UserManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingViewModel(userManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}