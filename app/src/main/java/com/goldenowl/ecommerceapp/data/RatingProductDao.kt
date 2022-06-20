package com.goldenowl.ecommerceapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RatingProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ratingProduct: RatingProduct)

    @Query("DELETE FROM RatingProduct")
    suspend fun deleteAll()

    @Update
    suspend fun update(ratingProduct: RatingProduct)

    @Query("SELECT * FROM RatingProduct WHERE idProduct = :idProduct")
    fun getRatingProduct(idProduct: String): Flow<RatingProduct>
}