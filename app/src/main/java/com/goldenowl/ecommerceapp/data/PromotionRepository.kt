package com.goldenowl.ecommerceapp.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromotionRepository @Inject constructor(
    private val promotionDao: PromotionDao
){
    suspend fun insert(promotion: Promotion) = promotionDao.insert(promotion)

    fun getPromotion(id:String) = promotionDao.getPromotion(id)

    fun getAll() = promotionDao.getAll()
}