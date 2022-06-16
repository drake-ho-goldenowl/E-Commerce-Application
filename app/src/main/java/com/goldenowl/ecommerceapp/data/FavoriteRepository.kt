package com.goldenowl.ecommerceapp.data

import android.util.Log
import com.goldenowl.ecommerceapp.utilities.FAVORITE_FIREBASE
import com.goldenowl.ecommerceapp.utilities.LAST_EDIT_TIME_FAVORITES
import com.goldenowl.ecommerceapp.viewmodels.FavoriteViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao
) {
    suspend fun insert(favorite: Favorite) = favoriteDao.insert(favorite)

    suspend fun update(favorite: Favorite) = favoriteDao.update(favorite)

    suspend fun delete(favorite: Favorite) = favoriteDao.delete(favorite)

    suspend fun deleteAll() = favoriteDao.deleteAll()


    fun getFavorite(idProduct: String, size: String, color: String) =
        favoriteDao.getFavorite(idProduct, size, color)

    fun getAllCategory() = favoriteDao.getAllCategory()

    fun getAll() = favoriteDao.getAll()

    fun getAllFavoriteAndProduct() = favoriteDao.getAllFavoriteAndProduct()

    suspend fun checkProductHaveFavorite(idProduct: String): Boolean {
        return favoriteDao.checkProductHaveFavorite(idProduct).isNotEmpty()
    }

    fun filterByCategory(category: String) = favoriteDao.filterByCategory(category)

    fun filterBySearch(search: String) = favoriteDao.filterBySearch(search)

    fun filterByCategoryAndSearch(
        search: String,
        category: String,
    ) = favoriteDao.filterByCategoryAndSearch(search, category)

    private fun createFavorite(idProduct: String, size: String, color: String): Favorite {
        return Favorite(
            size = size,
            idProduct = idProduct,
            color = color
        )
    }

    suspend fun insertFavorite(product: Product, size: String, color: String) {
        val favorite = createFavorite(product.id, size, color)
        favoriteDao.insert(favorite)
    }

    suspend fun updateFavoriteFirebase(db: FirebaseFirestore, uid: String) {
        val favorites = favoriteDao.getAllList()
        val docData: MutableMap<String, Any> = HashMap()
        LAST_EDIT_TIME_FAVORITES = Date()
        docData[FavoriteViewModel.LAST_EDIT] = LAST_EDIT_TIME_FAVORITES ?: Date()
        docData[FavoriteViewModel.DATA] = favorites
        db.collection(FAVORITE_FIREBASE).document(uid)
            .set(docData)
            .addOnSuccessListener {
                Log.d(UserManager.TAG, "DocumentSnapshot added")
            }
            .addOnFailureListener { e ->
                Log.w(UserManager.TAG, "Error adding document", e)
            }
    }
}