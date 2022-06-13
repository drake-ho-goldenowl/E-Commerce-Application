package com.goldenowl.ecommerceapp.data

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
){
    suspend fun insert(product: Product) = productDao.insert(product)

    suspend fun update(product: Product) = productDao.update(product)

    suspend fun delete(product: Product) = productDao.delete(product)

    fun getProductFlow(id : String) = productDao.getProductFlow(id)

    fun getAll() = productDao.getAll()

    fun getAllCategory() = productDao.getAllCategory()

    fun filterByCategory(category: String) = productDao.filterByCategory(category)

    fun filterBySearch(search: String) = productDao.filterBySearch(search)

    fun filterByCategoryAndSearch(
        search: String,
        category: String,
    ) = productDao.filterByCategoryAndSearch(search,category)

    suspend fun updateIsFavorite(id: String, isFavorite: Boolean)
            = productDao.updateIsFavorite(id, isFavorite)
}