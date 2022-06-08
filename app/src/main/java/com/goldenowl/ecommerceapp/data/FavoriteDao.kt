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

    @Query("SELECT id FROM favorite GROUP BY id")
    suspend fun getAllId(): List<Int>

    @Query("SELECT idProduct FROM favorite GROUP BY idProduct")
    suspend fun getAllIdProduct(): List<String>

    @Query("SELECT idProduct FROM favorite WHERE idProduct = :id GROUP BY idProduct")
    suspend fun getIdProduct(id: String): String

    @Query("SELECT * FROM favorite WHERE id = :id AND size = :size")
    fun getFavoriteFlow(id: String, size: String): Flow<Favorite>

    @Query("SELECT * FROM favorite WHERE id = :id AND size = :size")
    fun getFavorite(id: String, size: String): Favorite

    @Query("SELECT category_name FROM favorite INNER JOIN product ON product.id = favorite.id GROUP BY category_name")
    fun getAllCategory(): Flow<List<String>>

    @Query("SELECT * FROM favorite")
    fun getAll(): Flow<List<Favorite>>

    @Query("SELECT * FROM FAVORITE")
    suspend fun getAllList(): List<Favorite>

    @Query("SELECT * FROM favorite")
    fun getAllFavoriteAndProduct(): Flow<List<FavoriteAndProduct>>

    @Query("SELECT * FROM FAVORITE")
    suspend fun getAllListFavoriteAndProduct(): List<FavoriteAndProduct>

}