package com.goldenowl.ecommerceapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShippingAddressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shippingAddress: ShippingAddress)

    @Update
    suspend fun update(shippingAddress: ShippingAddress)

    @Delete
    suspend fun delete(shippingAddress: ShippingAddress)

    @Query("SELECT COUNT(id) FROM ShippingAddress")
    fun getSize(): Flow<Int>

    @Query("DELETE FROM ShippingAddress")
    suspend fun deleteAll()

    @Query("SELECT * FROM ShippingAddress")
    fun getAll(): Flow<List<ShippingAddress>>

    @Query("SELECT * FROM ShippingAddress WHERE id = :id")
    fun getShippingAddress(id: String): Flow<ShippingAddress>
}