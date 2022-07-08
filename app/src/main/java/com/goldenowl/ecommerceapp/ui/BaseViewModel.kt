package com.goldenowl.ecommerceapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Source

open class BaseViewModel : ViewModel() {
    val source = Source.DEFAULT
    val toastMessage = MutableLiveData<String>()
    companion object{
        const val SIZE = "size"
        const val COLOR = "color"
        const val ID_PRODUCT = "idProduct"
        const val ID = "id"
        const val LIMIT = 4
        const val CATEGORY_NAME = "categoryName"
        const val CREATED_DATE  = "createdDate"
        const val SALE_PERCENT = "salePercent"
    }
}