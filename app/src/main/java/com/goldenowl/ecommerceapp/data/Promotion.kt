package com.goldenowl.ecommerceapp.data

import java.util.*

data class Promotion(
    val id: String = "",
    val name: String = "",
    val salePercent: Long = 0,
    val endDate: Date? = null,
    val backgroundImage: String = ""
)
