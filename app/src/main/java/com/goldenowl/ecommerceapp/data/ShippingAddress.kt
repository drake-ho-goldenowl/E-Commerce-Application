package com.goldenowl.ecommerceapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ShippingAddress(
    @PrimaryKey
    val id: Long = 0,
    var fullName: String = "",
    var address: String = "",
    var city: String = "",
    var state: String = "",
    var zipCode: String = "",
    var country: String = "",
)
