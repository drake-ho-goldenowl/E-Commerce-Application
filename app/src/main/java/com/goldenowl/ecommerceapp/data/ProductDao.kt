package com.goldenowl.ecommerceapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("SELECT * FROM product")
    fun getAll(): Flow<List<Product>>

    @Query("SELECT category_name FROM product GROUP BY category_name")
    fun getAllCategory(): Flow<List<String>>

    @Query("SELECT * FROM product WHERE category_name = :category ORDER BY :str DESC")
    fun filterByCategory(category: String, str: String): Flow<List<Product>>

    @Query("SELECT * FROM product ORDER BY :str DESC")
    fun filterBySort(str: String): Flow<List<Product>>

    @Query("SELECT * FROM product WHERE title LIKE '%' || :search || '%'  ORDER BY :str DESC")
    fun filterBySearch(search: String, str: String): Flow<List<Product>>

    @Query("SELECT * FROM product WHERE title LIKE '%' || :search || '%' AND category_name = :category ORDER BY :str DESC")
    fun filterByCategoryAndSearch(
        search: String,
        category: String,
        str: String
    ): Flow<List<Product>>
}