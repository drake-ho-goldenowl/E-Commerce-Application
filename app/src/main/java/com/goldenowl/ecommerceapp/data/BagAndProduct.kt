package com.goldenowl.ecommerceapp.data

import androidx.room.Embedded
import androidx.room.Relation

data class BagAndProduct(
    @Embedded
    val bag: Bag,
    @Relation(
        parentColumn = "idProduct",
        entityColumn = "id"
    )
    val product: Product
)