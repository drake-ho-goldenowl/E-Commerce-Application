package com.goldenowl.ecommerceapp.data

data class Color(
    val color: String?,
    val sizes: List<Size>
){
    constructor(): this(null, emptyList())
}
