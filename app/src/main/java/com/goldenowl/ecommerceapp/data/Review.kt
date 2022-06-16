package com.goldenowl.ecommerceapp.data

import com.google.firebase.Timestamp

data class Review(
    val idUser: String = "",
    val idProduct: String = "",
    val description: String = "",
    val star: Long = 0,
    val createdTimer: Timestamp? = Timestamp.now(),
    val helpful: List<String> = emptyList(),
    val listImage: List<String> = emptyList()
)