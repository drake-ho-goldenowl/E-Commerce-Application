package com.goldenowl.ecommerceapp.data


import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Product(
    @PrimaryKey
    val id: String,
    @NonNull @ColumnInfo(name = "title")
    val title: String,
    @NonNull @ColumnInfo(name = "brand_name")
    val brandName: String,
    @NonNull @ColumnInfo(name = "images")
    val images: List<String>,
    @ColumnInfo(name = "created_date")
    val createdDate: Date?,
    @Nullable @ColumnInfo(name = "sale_percent")
    val salePercent: Int?,
    @field:JvmField @ColumnInfo(name = "is_popular")
    val isPopular: Boolean?,
    @NonNull @ColumnInfo(name = "number_reviews")
    val numberReviews: Int,
    @NonNull @ColumnInfo(name = "review_stars")
    val reviewStars: Int,
    @NonNull @ColumnInfo(name = "category_name")
    val categoryName: String,
    @NonNull @ColumnInfo(name = "colors")
    val colors: List<Color>,
    @ColumnInfo(name = "description")
    val description: String,
    @NonNull @ColumnInfo(name = "tags")
    val tags: List<Tag>
) {
    constructor() : this(
        "",
        "",
        "",
        emptyList(),
        null,
        null,
        null,
        0,
        0,
        "",
        emptyList(),
        "",
        emptyList())
}

