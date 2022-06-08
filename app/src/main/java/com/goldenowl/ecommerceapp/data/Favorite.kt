package com.goldenowl.ecommerceapp.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["id", "size"])
data class Favorite(
    @ColumnInfo @NonNull
    val id: String,
    @ColumnInfo @NonNull
    val size: String,
    var quantity: Long,
    val price: Long,
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
    var isBag: Boolean = false
) {
    constructor() : this(
        "",
        "", 0, 0,
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
    )
}