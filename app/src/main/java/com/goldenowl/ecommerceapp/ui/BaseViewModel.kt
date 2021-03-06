package com.goldenowl.ecommerceapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    val toastMessage = MutableLiveData<String>()
    val isLoading = MutableLiveData(false)
    val dismiss = MutableLiveData(false)

    companion object {
        const val SIZE = "size"
        const val COLOR = "color"
        const val ID_PRODUCT = "idProduct"
        const val ID = "id"
        const val LIMIT = 4
        const val CATEGORY_NAME = "categoryName"
        const val CREATED_DATE = "createdDate"
        const val SALE_PERCENT = "salePercent"
        const val STATUS_ORDER = "status"
        const val ID_USER = "idUser"
        const val NUMBER_REVIEWS = "numberReviews"
        const val REVIEW_STARS = "reviewStars"

        val statuses = listOf("Delivered", "Processing", "Cancelled")
        const val DELIVERED = 0
        const val PROCESSING = 1
        const val CANCELLED = 2
    }
}