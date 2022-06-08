package com.goldenowl.ecommerceapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.utilities.LAST_EDIT_TIME_FAVORITES
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.set

class FavoriteViewModel(
    private val productDao: ProductDao,
    private val favoriteDao: FavoriteDao,
    val userManager: UserManager
) :
    BaseViewModel() {
    private val db = Firebase.firestore
    var favorites = favoriteDao.getAll().asLiveData()
    val allCategory = favoriteDao.getAllCategory().asLiveData()


    private fun updateFavoriteFirebase(favorites: List<Favorite>){
        val docData: MutableMap<String, Any> = HashMap()
        LAST_EDIT_TIME_FAVORITES = Date()
        docData[LAST_EDIT] = LAST_EDIT_TIME_FAVORITES!!
        docData[DATA] = favorites
        db.collection("favorites").document(userManager.getAccessToken())
            .set(docData)
            .addOnSuccessListener {
                Log.d(UserManager.TAG, "DocumentSnapshot added")
            }
            .addOnFailureListener { e ->
                Log.w(UserManager.TAG, "Error adding document", e)
            }
    }


    fun removeFavorite(favorite: Favorite){
        viewModelScope.launch {
            favoriteDao.delete(favorite)
            checkFavorites(favorite)
            updateFavoriteFirebase(favoriteDao.getAllList())
        }
    }

    private suspend fun checkFavorites(favorite: Favorite){
        val id = favoriteDao.getId(favorite.id)
        if(id.isNullOrBlank()){
            val product = productDao.getProduct(favorite.id)
            product.isFavorite = false
            productDao.update(product)
        }
    }



    private fun createFavorite(product: Product, color: String, size: String): Favorite {
        val sizeSelect = getSizeOfColor(product.colors[0].sizes, size) ?: Size()
        return Favorite(
            product.id,
            sizeSelect.size,
            sizeSelect.quantity,
            sizeSelect.price,
            product.title,
            product.brandName,
            product.images[0],
            product.createdDate,
            product.salePercent,
            product.isPopular,
            product.numberReviews,
            product.reviewStars,
            product.categoryName,
            color,
        )
    }


    private fun getSizeOfColor(sizes: List<Size>, select: String): Size? {
        for (size in sizes) {
            if (select == size.size) {
                return size
            }
        }
        return null
    }

    fun insertFavorite(product: Product, color: String, size: String) {
        val favorite = createFavorite(product, color, size)
        viewModelScope.launch {
            favoriteDao.insert(favorite)
            product.isFavorite = true
            fetchProduct(product)
            updateFavoriteFirebase(favoriteDao.getAllList())
        }
    }

    private fun fetchProduct(product: Product) {
        viewModelScope.launch {
            productDao.update(product)
        }
    }


    fun getAllSize(product: Product): List<String> {
        val sizes: MutableSet<String> = mutableSetOf()
        for (color in product.colors) {
            for (size in color.sizes) {
                if (size.quantity > 0) {
                    sizes.add(size.size)
                }
            }
        }
        return sizes.toList()
    }

    companion object{
        const val LAST_EDIT = "lastEdit"
        const val DATA = "data"
        const val TAG = "FAVORITE_VIEW_MODEL"
    }
}

class FavoriteViewModelFactory(
    private val productDao: ProductDao,
    private val favoriteDao: FavoriteDao,
    private val userManager: UserManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteViewModel(productDao, favoriteDao, userManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
