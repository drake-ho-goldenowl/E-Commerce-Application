package com.goldenowl.ecommerceapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShippingAddressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shippingAddress: ShippingAddress)

    @Query("DELETE FROM ShippingAddress")
    suspend fun deleteAll()

    @Query("SELECT * FROM ShippingAddress")
    fun getAll(): Flow<List<ShippingAddress>>

    @Query("SELECT * FROM ShippingAddress WHERE id = :id")
    fun getShippingAddress(id: String): Flow<ShippingAddress>
}