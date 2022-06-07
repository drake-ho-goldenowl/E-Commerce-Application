package com.goldenowl.ecommerceapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.Favorite
import com.goldenowl.ecommerceapp.data.FavoriteDao
import com.goldenowl.ecommerceapp.data.ProductDao
import com.goldenowl.ecommerceapp.data.UserManager
import com.goldenowl.ecommerceapp.utilities.LAST_EDIT_TIME
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.*

class FavoriteViewModel(
    private val productDao: ProductDao,
    private val favoriteDao: FavoriteDao,
    val userManager: UserManager
) :
    ViewModel() {
    private val db = Firebase.firestore
    var favorites = favoriteDao.getAll().asLiveData()
    val allCategory = favoriteDao.getAllCategory().asLiveData()

    fun addFavorite(favorites: List<Favorite>){
        val docData: MutableMap<String, Any> = HashMap()
        LAST_EDIT_TIME = Date()
        docData[LAST_EDIT] = LAST_EDIT_TIME!!
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
