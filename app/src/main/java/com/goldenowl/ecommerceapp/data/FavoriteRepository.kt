package com.goldenowl.ecommerceapp.data

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao
) {
    suspend fun insert(favorite: Favorite) {
        favoriteDao.insert(favorite)
    }

    suspend fun update(favorite: Favorite) = favoriteDao.update(favorite)

    suspend fun delete(favorite: Favorite) = favoriteDao.delete(favorite)

    suspend fun deleteAll() = favoriteDao.deleteAll()

    suspend fun countFavorite() = favoriteDao.countFavorite()

    suspend fun getAllIdProduct() = favoriteDao.getAllIdProduct()

    suspend fun getIdProduct(id: String) = favoriteDao.getIdProduct(id)

    fun getFavoriteFlow(idProduct: String, size: String) =
        favoriteDao.getFavoriteFlow(idProduct, size)

    fun getFavoriteWithIdProduct(idProduct: String, size: String) =
        favoriteDao.getFavoriteWithIdProduct(idProduct, size)

    fun getAllCategory() = favoriteDao.getAllCategory()

    fun getAll() = favoriteDao.getAll()

    suspend fun getAllList() = favoriteDao.getAllList()

    fun getAllFavoriteAndProduct() = favoriteDao.getAllFavoriteAndProduct()

    fun filterByCategory(category: String) = favoriteDao.filterByCategory(category)

    fun filterBySearch(search: String) = favoriteDao.filterBySearch(search)

    fun filterByCategoryAndSearch(
        search: String,
        category: String,
    ) = favoriteDao.filterByCategoryAndSearch(search, category)
}