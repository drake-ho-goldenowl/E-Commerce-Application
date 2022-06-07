package com.goldenowl.ecommerceapp.data

import java.util.*

data class Favorites(
    var lastEdit : Date?,
    var data : List<Favorite>?
) {
    constructor():this (
        null,
        emptyList<Favorite>()
    )
}