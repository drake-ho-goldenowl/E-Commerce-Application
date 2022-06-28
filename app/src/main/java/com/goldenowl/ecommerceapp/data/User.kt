package com.goldenowl.ecommerceapp.data

class User(
    var name: String = "",
    val email: String = "",
    var password: String = "",
    val token: String = "",
    var dob: String = "",
    var avatar: String = "",
    var defaultAddress: String = "",
    var defaultPayment: String = "",
)
