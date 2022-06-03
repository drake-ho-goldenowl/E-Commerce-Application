package com.goldenowl.ecommerceapp.data

import androidx.room.Entity
import java.util.*
@Entity(primaryKeys = ["id","size"])
data class Favorite(
    val id: String,
    val title: String,
    val brandName: String,
    val images: String,
    val createdDate: Date?,
    val salePercent: Int?,
    @field:JvmField
    val isPopular: Boolean?,
    val numberReviews: Int,
    val reviewStars: Int,
    val categoryName: String,
    val color: String,
    val size: Size,
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        null,
        null,
        null,
        0,
        0,
        "",
        "",
        Size(),
    )
}