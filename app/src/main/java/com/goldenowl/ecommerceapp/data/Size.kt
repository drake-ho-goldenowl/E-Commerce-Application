package com.goldenowl.ecommerceapp.data

data class Size(
    val size: String?,
    val price: Long?,
    val quantity: Long?,
) {
    constructor(): this(null,null, null)
}