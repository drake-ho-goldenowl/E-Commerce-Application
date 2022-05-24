package com.goldenowl.ecommerceapp.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AuthViewModelFactory(
    private val application: Application,
    private val listener: OnSignInStartedListener
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AuthViewModel::class.java)){
            return AuthViewModel(application,listener) as T
        }
        throw IllegalAccessException("Unknown ViewModel class")
    }
}