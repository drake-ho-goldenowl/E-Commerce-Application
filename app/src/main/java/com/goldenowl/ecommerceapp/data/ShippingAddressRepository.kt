package com.goldenowl.ecommerceapp.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShippingAddressRepository @Inject constructor(
    private val shippingAddressDao: ShippingAddressDao
) {
    suspend fun insert(shippingAddress: ShippingAddress) =
        shippingAddressDao.insert(shippingAddress)

    suspend fun deleteAll() = shippingAddressDao.deleteAll()

    fun getAll() = shippingAddressDao.getAll()

    fun getShippingAddress(id: String) = shippingAddressDao.getShippingAddress(id)

}