package com.goldenowl.ecommerceapp.data

import com.goldenowl.ecommerceapp.utilities.MAX
import com.goldenowl.ecommerceapp.utilities.MIN
import com.goldenowl.ecommerceapp.utilities.TRACKING_NUMBER
import java.util.*

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
    var delivery: Delivery? = null,
    var promotion: String = ""
) {
    fun getUnits(): Int {
        var result = 0
        for (product in products) {
            result += product.units
        }
        return result
    }
}

