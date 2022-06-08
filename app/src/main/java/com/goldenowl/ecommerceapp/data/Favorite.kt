package com.goldenowl.ecommerceapp.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index

@Entity(primaryKeys = ["id", "size"],
    foreignKeys = [ForeignKey(entity = Product::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idProduct"),
        onDelete = CASCADE)],
    indices = [Index(value = ["id"], unique = true)]
)
data class Favorite(
    @ColumnInfo @NonNull
    val id: Int = 0,
    @ColumnInfo @NonNull
    val size: String = "",
    val idProduct: String = "",
    var isBag: Boolean = false
) {
}