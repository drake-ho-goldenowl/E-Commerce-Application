package com.goldenowl.ecommerceapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(order: Order)

    @Query("DELETE FROM `order`")
    suspend fun deleteAll()

    @Query("SELECT COUNT(id) FROM `order`")
    fun getSize(): Flow<Int>

    @Query("SELECT * FROM `order`")
    fun getAll(): Flow<List<Order>>

    @Query("SELECT * FROM `order` WHERE id = :id")
    fun getOrder(id: String): Flow<Order>
}