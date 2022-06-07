package com.goldenowl.ecommerceapp.data

data class Size(
    val size: String,
    val price: Long,
    var quantity: Long,
) {
    constructor(): this("",0, 0)
}