package com.goldenowl.ecommerceapp.data

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(primaryKeys = ["idProduct", "size"],
    foreignKeys = [ForeignKey(entity = Product::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idProduct"),
        onDelete = CASCADE)]
)
data class Favorite(
    @NonNull
    val size: String = "",
    @NonNull
    val idProduct: String = "",
    var isBag: Boolean = false
) {
}