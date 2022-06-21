package com.goldenowl.ecommerceapp.data


import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import kotlin.math.roundToInt

@Entity
data class Product(
    @PrimaryKey
    val id: String = "",
    @NonNull @ColumnInfo(name = "title")
    val title: String = "",
    @NonNull @ColumnInfo(name = "brand_name")
    val brandName: String = "",
    @NonNull @ColumnInfo(name = "images")
    val images: List<String> = emptyList(),
    @ColumnInfo(name = "created_date")
    val createdDate: Date? = null,
    @Nullable @ColumnInfo(name = "sale_percent")
    val salePercent: Int? = null,
    @field:JvmField @ColumnInfo(name = "is_popular")
    val isPopular: Boolean = false,
    @NonNull @ColumnInfo(name = "number_reviews")
    var numberReviews: Int = 0,
    @NonNull @ColumnInfo(name = "review_stars")
    var reviewStars: Float = 0F,
    @NonNull @ColumnInfo(name = "category_name")
    val categoryName: String = "",
    @NonNull @ColumnInfo(name = "colors")
    val colors: List<Color> = emptyList(),
    @ColumnInfo(name = "description")
    val description: String = "",
    @NonNull @ColumnInfo(name = "tags")
    val tags: List<Tag> = emptyList(),
) {
    fun getAllSize(): List<String> {
        val sizes: MutableSet<String> = mutableSetOf()
        for (color in this.colors) {
            for (size in color.sizes) {
                if (size.quantity > 0) {
                    sizes.add(size.size)
                }
            }
        }
        return sizes.toList()
    }

    fun getSize(sizeStr: String): Size? {
        for (size in this.colors[0].sizes) {
            if (sizeStr == size.size) {
                return size
            }
        }
        return null
    }

    fun getColorAndSize(colorStr: String, sizeStr: String): Size? {
        for (color in this.colors) {
            if (color.color == colorStr) {
                for (size in color.sizes) {
                    if (sizeStr == size.size)
                        return size
                }
            }
        }
        return null
    }
    fun getAverageRating(rates: List<Int>): Float {
        var result = 0F
        var totalHaveRating = 0
        for ((index, value) in rates.withIndex()) {
            if(value > 0) totalHaveRating += value
            result += value * (index + 1)
        }
        if(totalHaveRating == 0) totalHaveRating = 1
        result /= totalHaveRating
        return ((result * 10).roundToInt() / 10).toFloat()
    }

    fun getTotalRating(rates: List<Int>): Int {
        var result = 0
        for (rate in rates) {
            result += rate
        }
        return result
    }
}