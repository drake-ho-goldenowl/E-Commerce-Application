package com.goldenowl.ecommerceapp.data

import java.util.*

const val MIN = 100000
const val MAX = 999999
const val TRACKING_NUMBER = "IW3475453455"

data class Order(
    val id: String = (MIN..MAX).random().toString(),
    var products: List<ProductOrder> = emptyList(),
    var trackingNumber: String = TRACKING_NUMBER,
    var total: Float = 0F,
    var status: Int = 0,
    var timeCreated: Date = Date(),
    var shippingAddress: String = "",
    var payment: String = "",
    var isTypePayment: Int = 0,

    var promotion: String = ""
)