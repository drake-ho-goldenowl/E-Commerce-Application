package com.goldenowl.ecommerceapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(
    primaryKeys = ["idProduct", "size", "color"],
    foreignKeys = [ForeignKey(
        entity = Product::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idProduct"),
        onDelete = CASCADE
    )]
)
data class Favorite(
    var id :String = "",
    val size: String = "",
    val idProduct: String = "",
    val color: String = "",
    var isBag: Boolean = false
)