package com.goldenowl.ecommerceapp.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderDao: OrderDao
) {
    suspend fun insert(order: Order) = orderDao.insert(order)

    suspend fun deleteAll() = orderDao.deleteAll()

    fun getSize() = orderDao.getSize()

    fun getAll() = orderDao.getAll()

    fun getOrder(id: String) = orderDao.getOrder(id)

}