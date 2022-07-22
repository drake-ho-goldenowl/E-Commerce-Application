package com.goldenowl.ecommerceapp.data

import androidx.lifecycle.MutableLiveData

open class BaseRepository {
    val isSuccess = MutableLiveData(true)
}