package com.goldenowl.ecommerceapp.data


import java.util.*
import kotlin.math.roundToInt

data class Product(
    val id: String = "",
    val title: String = "",
    val brandName: String = "",
    val images: List<String> = emptyList(),
    val createdDate: Date? = null,
    val salePercent: Int? = null,
    @JvmField
    val isPopular: Boolean = false,
    var numberReviews: Int = 0,
    var reviewStars: Float = 0F,
    val categoryName: String = "",
    val colors: List<Color> = emptyList(),
    val description: String = "",
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
    fun getAllColor(): List<String>{
        val colors = mutableListOf<String>()
        for (color in this.colors){
            color.color?.let {
                colors.add(it)
            }
        }
        return colors
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
            if (value > 0) totalHaveRating += value
            result += value * (index + 1)
        }
        if (totalHaveRating == 0) totalHaveRating = 1
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