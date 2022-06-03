package com.goldenowl.ecommerceapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("SELECT * FROM product")
    fun getAll(): Flow<List<Product>>

    @Query("SELECT category_name FROM product GROUP BY category_name")
    fun getAllCategory(): Flow<List<String>>

    @Query("SELECT * FROM product WHERE category_name = :category")
    fun filterByCategory(category: String): Flow<List<Product>>

    @Query("SELECT * FROM product WHERE title LIKE '%' || :search || '%'")
    fun filterBySearch(search: String): Flow<List<Product>>

    @Query("SELECT * FROM product WHERE title LIKE '%' || :search || '%' AND category_name = :category")
    fun filterByCategoryAndSearch(
        search: String,
        category: String,
    ): Flow<List<Product>>

}