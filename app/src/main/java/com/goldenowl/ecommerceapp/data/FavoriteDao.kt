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

    @Query("SELECT id FROM favorite GROUP BY id")
    suspend fun getAllId(): List<String>

    @Query("SELECT id FROM favorite WHERE id = :id GROUP BY id")
    suspend fun getId(id : String): String

    @Query("SELECT categoryName FROM favorite GROUP BY categoryName")
    fun getAllCategory(): Flow<List<String>>

    @Query("SELECT * FROM favorite")
    fun getAll(): Flow<List<Favorite>>

    @Query("SELECT * FROM FAVORITE")
    suspend fun getAllList(): List<Favorite>

}