package com.goldenowl.ecommerceapp.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RatingProductRepository @Inject constructor(
    private val ratingProductDao: RatingProductDao
) {
    suspend fun insert(ratingProduct: RatingProduct) = ratingProductDao.insert(ratingProduct)

    suspend fun update(ratingProduct: RatingProduct) = ratingProductDao.update(ratingProduct)

    suspend fun deleteAll() = ratingProductDao.deleteAll()

    fun getRatingProduct(idProduct: String) = ratingProductDao.getRatingProduct(idProduct)

}