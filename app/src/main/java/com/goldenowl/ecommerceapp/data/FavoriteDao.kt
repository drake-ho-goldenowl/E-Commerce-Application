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


    @Query("SELECT idProduct FROM favorite WHERE idProduct = :id GROUP BY idProduct")
    fun getIdProduct(id: String): Flow<String>

    @Query("SELECT * FROM favorite WHERE idProduct = :idProduct AND size = :size AND color = :color")
    fun getFavorite(idProduct: String, size: String, color: String): Flow<Favorite>

    @Query("SELECT category_name FROM favorite INNER JOIN product ON product.id = favorite.idProduct GROUP BY category_name")
    fun getAllCategory(): Flow<List<String>>

    @Query("SELECT * FROM favorite")
    fun getAll(): Flow<List<Favorite>>

    @Query("SELECT * FROM FAVORITE")
    suspend fun getAllList(): List<Favorite>

    @Query("SELECT * FROM favorite")
    fun getAllFavoriteAndProduct(): Flow<List<FavoriteAndProduct>>

    @Query("SELECT * FROM favorite INNER JOIN product ON product.id = favorite.idProduct WHERE idProduct = :idProduct")
    suspend fun checkProductHaveFavorite(idProduct: String): List<FavoriteAndProduct>

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