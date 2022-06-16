package com.goldenowl.ecommerceapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Promotion(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val salePercent: Long = 0,
    val endDate: Date? = null,
    val backgroundImage: String =""
)
