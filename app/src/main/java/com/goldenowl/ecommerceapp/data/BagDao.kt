package com.goldenowl.ecommerceapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bag: Bag)

    @Update
    suspend fun update(bag: Bag)

    @Delete
    suspend fun delete(bag: Bag)

    @Query("DELETE FROM bag")
    suspend fun deleteAll()

    @Query("SELECT * FROM bag")
    fun getAll(): Flow<List<Bag>>

    @Query("SELECT * FROM bag")
    suspend fun getAllList(): List<Bag>

    @Query("SELECT * FROM bag INNER JOIN product ON bag.idProduct = product.id")
    fun getAllBagAndProduct(): Flow<List<BagAndProduct>>

    @Query("SELECT * FROM bag WHERE idProduct = :idProduct AND color = :color AND size = :size")
    suspend fun getBag(idProduct: String, color: String, size: String): Bag

    @Query("UPDATE bag set quantity = :quantity WHERE idProduct = :idProduct AND color = :color AND size = :size")
    suspend fun updateQuantity(idProduct: String,color: String,size: String,quantity: Long)
}