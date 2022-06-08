package com.goldenowl.ecommerceapp.data

import java.util.*

data class Bags(
    var lastEdit : Date?,
    var data : List<Bag>?
) {
    constructor():this (
        null,
        emptyList<Bag>()
    )
}