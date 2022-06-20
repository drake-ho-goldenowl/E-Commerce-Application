package com.goldenowl.ecommerceapp.data

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.round

@Entity
data class RatingProduct(
    @PrimaryKey
    val idProduct: String = "",
    @NonNull
    val rating: List<Long> = listOf(0, 0, 0, 0, 0)
) {
    fun getAverageRating(): Float {
        var result: Long = 0
        for ((index, value) in rating.withIndex()) {
            result += value * (index + 1)
        }
        result /= 5
        return round(result.toFloat() * 10) / 10
    }

    fun getTotalRating(): Float {
        var result: Long = 0
        for (rate in rating) {
            result += rate
        }
        return result.toFloat()
    }
}
