package com.goldenowl.ecommerceapp.data

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class BagRepository @Inject constructor(
    private val bagDao: BagDao
) {
    suspend fun insert(bag: Bag) = bagDao.insert(bag)

    suspend fun update(bag: Bag) = bagDao.update(bag)

    suspend fun delete(bag: Bag) = bagDao.delete(bag)

    suspend fun deleteAll() = bagDao.deleteAll()

    fun getAll() = bagDao.getAll()

    suspend fun getAllList() = bagDao.getAllList()

    suspend fun getBag(idProduct: String, color: String, size: String) =
        bagDao.getBag(idProduct, color, size)
}