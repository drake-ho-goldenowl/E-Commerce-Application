package com.goldenowl.ecommerceapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    val toastMessage = MutableLiveData<String>()
}