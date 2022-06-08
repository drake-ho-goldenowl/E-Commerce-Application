package com.goldenowl.ecommerceapp.data

import androidx.room.Entity

@Entity(primaryKeys = ["id", "size","color"])
data class Bag(
    val id: String,
    val size: String,
    val color: String,
    var quantity: Long,
    val price: Long,
    val title: String,
    val brandName: String,
    val images: String,
    val salePercent: Int?,
) {
    constructor() : this(
        "",
        "",
        "",
        0,
        0,
        "",
        "",
        "",
        null,
    )
}