package com.goldenowl.ecommerceapp.data

import androidx.room.Entity
import androidx.room.ForeignKey.CASCADE

@Entity(
    primaryKeys = ["idProduct", "size", "color"],
    foreignKeys = [androidx.room.ForeignKey(
        entity = Product::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idProduct"),
        onDelete = CASCADE
    )]
)
data class Bag(
    val size: String = "",
    val color: String = "",
    val idProduct: String = "",
    var quantity: Long = 0,
)