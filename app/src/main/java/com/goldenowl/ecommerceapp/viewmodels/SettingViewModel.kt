package com.goldenowl.ecommerceapp.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.model.User
import com.goldenowl.ecommerceapp.model.UserManager
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SettingViewModel(application: Application) : BaseViewModel(application) {
    val validNameLiveData: MutableLiveData<String> = MutableLiveData()
    private var userManager : UserManager = UserManager.getInstance(application)
    private var storageReference: StorageReference = FirebaseStorage.getInstance().reference

    fun updateName(nameText: String){
        val user = userManager.getUser()
        user.name = nameText
        userManager.setName(nameText)
        User.writeProfile(user)
    }

    fun updateDOB(dobText: String){
        val user = userManager.getUser()
        user.dob = dobText
        userManager.setDOB(dobText)
        User.writeProfile(user)
    }

    fun updateAvatar(avatarURL: String){
        val user = userManager.getUser()
        user.avatar = avatarURL
        userManager.setAvatar(avatarURL)
        User.writeProfile(user)
    }

    fun isLettersOrDigit(string: String): Boolean {
        return string.filter { it.isLetterOrDigit() }.length == string.length
    }

    fun uploadImage(filePath: Uri?,token: String){
        if(filePath != null){
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
        }else{
            toastMessage.postValue("Please Upload an Image")
        }
    }

    fun checkLoginWithFbOrGoogle(): Boolean{
        if(userManager.getPassword() == "") return true
        return false
    }

    fun validName(nameText: String) {
        if(!isLettersOrDigit(nameText)){
            validNameLiveData.postValue("Mustn't Contain Special Character")
        }
        else if (nameText.isBlank()) {
            validNameLiveData.postValue("Mustn't empty")
        } else {
            validNameLiveData.postValue("")
        }
    }
}