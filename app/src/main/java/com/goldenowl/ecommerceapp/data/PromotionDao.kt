package com.goldenowl.ecommerceapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PromotionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(promotion: Promotion)

    @Query("SELECT * FROM promotion WHERE id = :id")
    fun getPromotion(id:String): Flow<Promotion>

    @Query("SELECT * FROM promotion")
    fun getAll(): Flow<List<Promotion>>
}