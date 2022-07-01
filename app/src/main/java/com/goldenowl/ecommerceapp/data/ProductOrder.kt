package com.goldenowl.ecommerceapp.data

data class ProductOrder(
    val idProduct: String = "",
    val image: String = "",
    val title: String = "",
    val brandName: String = "",
    val size: String = "",
    val color: String = "",
    val units: Int = 0,
    val price: Float = 0F,
)