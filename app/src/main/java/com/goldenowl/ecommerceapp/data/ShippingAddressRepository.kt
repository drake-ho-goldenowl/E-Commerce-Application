package com.goldenowl.ecommerceapp.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShippingAddressRepository @Inject constructor(
    private val shippingAddressDao: ShippingAddressDao
) {
    suspend fun insert(shippingAddress: ShippingAddress) =
        shippingAddressDao.insert(shippingAddress)

    suspend fun update(shippingAddress: ShippingAddress) =
        shippingAddressDao.update(shippingAddress)

    suspend fun delete(shippingAddress: ShippingAddress) =
        shippingAddressDao.delete(shippingAddress)

    fun getSize() = shippingAddressDao.getSize()

    suspend fun deleteAll() = shippingAddressDao.deleteAll()

    fun getAll() = shippingAddressDao.getAll()

    fun getShippingAddress(id: String) = shippingAddressDao.getShippingAddress(id)

}