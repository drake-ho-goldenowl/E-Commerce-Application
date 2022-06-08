package com.goldenowl.ecommerceapp.data

import androidx.room.Embedded
import androidx.room.Relation

data class FavoriteAndProduct(
    @Embedded
    val favorite: Favorite,
    @Relation(
        parentColumn = "idProduct",
        entityColumn = "id"
    )
    val product: Product
)
