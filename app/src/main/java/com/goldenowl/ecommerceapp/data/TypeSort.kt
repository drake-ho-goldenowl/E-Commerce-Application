package com.goldenowl.ecommerceapp.data

import com.google.firebase.firestore.Query

enum class TypeSort(val value: Query.Direction) {
    ASCENDING(Query.Direction.ASCENDING),
    DESCENDING(Query.Direction.DESCENDING)
}