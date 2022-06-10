package com.goldenowl.ecommerceapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: Favorite)

    @Update
    suspend fun update(favorite: Favorite)

    @Delete
    suspend fun delete(favorite: Favorite)

    @Query("DELETE FROM favorite")
    suspend fun deleteAll()

    @Query("SELECT COUNT(idProduct) FROM favorite GROUP BY idProduct,size")
    suspend fun countFavorite(): Int = 0

    @Query("SELECT idProduct FROM favorite GROUP BY idProduct")
    suspend fun getAllIdProduct(): List<String>

    @Query("SELECT idProduct FROM favorite WHERE idProduct = :id GROUP BY idProduct")
    suspend fun getIdProduct(id: String): String

    @Query("SELECT * FROM favorite WHERE idProduct = :idProduct AND size = :size")
    fun getFavoriteFlow(idProduct: String, size: String): Flow<Favorite>

    @Query("SELECT * FROM favorite WHERE idProduct = :idProduct AND size = :size")
    fun getFavoriteWithIdProduct(idProduct: String, size: String): Favorite

    @Query("SELECT category_name FROM favorite INNER JOIN product ON product.id = favorite.idProduct GROUP BY category_name")
    fun getAllCategory(): Flow<List<String>>

    @Query("SELECT * FROM favorite")
    fun getAll(): Flow<List<Favorite>>

    @Query("SELECT * FROM FAVORITE")
    suspend fun getAllList(): List<Favorite>

    @Query("SELECT * FROM favorite")
    fun getAllFavoriteAndProduct(): Flow<List<FavoriteAndProduct>>

    //filter
    @Query("SELECT * FROM favorite INNER JOIN product ON product.id = favorite.idProduct WHERE category_name = :category")
    fun filterByCategory(category: String): Flow<List<FavoriteAndProduct>>

    @Query("SELECT * FROM favorite INNER JOIN product ON product.id = favorite.idProduct WHERE title LIKE '%' || :search || '%'")
    fun filterBySearch(search: String): Flow<List<FavoriteAndProduct>>

    @Query("SELECT * FROM favorite INNER JOIN product ON product.id = favorite.idProduct WHERE title LIKE '%' || :search || '%' AND category_name = :category")
    fun filterByCategoryAndSearch(
        search: String,
        category: String,
    ): Flow<List<FavoriteAndProduct>>

}