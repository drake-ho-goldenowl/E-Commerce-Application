package com.goldenowl.ecommerceapp.data

import java.util.*

data class Favorites(
    var lastEdit : Date? = null,
    var data : List<Favorite>? = null
)