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
    val numberReviews: Int = 0,
    @NonNull @ColumnInfo(name = "review_stars")
    val reviewStars: Int = 0,
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
}