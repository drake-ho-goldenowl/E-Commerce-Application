package com.goldenowl.ecommerceapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.utilities.FAVORITE_FIREBASE
import com.goldenowl.ecommerceapp.utilities.LAST_EDIT_TIME_FAVORITES
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.set

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
    val userManager: UserManager
) :
    BaseViewModel() {
    private val db = Firebase.firestore
    val statusFilter = MutableStateFlow(Triple("", "", 0))
    val allCategory = favoriteRepository.getAllCategory().asLiveData()
    val favoriteAndProducts: LiveData<List<FavoriteAndProduct>> = statusFilter.flatMapLatest {
        if (it.first.isNotBlank() && it.second.isNotBlank()) {
            favoriteRepository.filterByCategoryAndSearch(it.second, it.first)
        } else if (it.first.isNotBlank()) {
            favoriteRepository.filterByCategory(it.first)
        } else if (it.second.isNotBlank()) {
            favoriteRepository.filterBySearch(it.second)
        } else {
            favoriteRepository.getAllFavoriteAndProduct()
        }
    }.asLiveData()

    fun filterSort(favorites: List<FavoriteAndProduct>): List<FavoriteAndProduct> {
        return when (statusFilter.value.third) {
            0 -> favorites.sortedByDescending {
                it.product.isPopular
            }
            1 -> favorites.sortedByDescending {
                it.product.createdDate
            }
            2 -> favorites.sortedByDescending {
                it.product.numberReviews
            }
            3 -> favorites.sortedBy {
                val size = it.product.getSize(it.favorite.size)
                size?.price
            }
            else -> {
                favorites.sortedByDescending {
                    val size = it.product.getSize(it.favorite.size)
                    size?.price
                }
            }
        }
    }

    fun setCategory(category: String) {
        statusFilter.value = Triple(category, statusFilter.value.second, statusFilter.value.third)
    }

    fun setSearch(search: String) {
        statusFilter.value = Triple(statusFilter.value.first, search, statusFilter.value.third)
    }

    fun setSort(select: Int) {
        statusFilter.value = Triple(statusFilter.value.first, statusFilter.value.second, select)
    }

    private fun updateFavoriteFirebase(favorites: List<Favorite>) {
        val docData: MutableMap<String, Any> = HashMap()
        LAST_EDIT_TIME_FAVORITES = Date()
        docData[LAST_EDIT] = LAST_EDIT_TIME_FAVORITES!!
        docData[DATA] = favorites
        db.collection(FAVORITE_FIREBASE).document(userManager.getAccessToken())
            .set(docData)
            .addOnSuccessListener {
                Log.d(UserManager.TAG, "DocumentSnapshot added")
            }
            .addOnFailureListener { e ->
                Log.w(UserManager.TAG, "Error adding document", e)
            }
    }


    fun removeFavorite(favorite: Favorite) {
        viewModelScope.launch {
            favoriteRepository.delete(favorite)
            checkFavorites(favorite)
            updateFavoriteFirebase(favoriteRepository.getAllList())
        }
    }

    private suspend fun checkFavorites(favorite: Favorite) {
        val id = favoriteRepository.getIdProduct(favorite.idProduct)
        if (id.isBlank()) {
            val product = productRepository.getProduct(favorite.idProduct)
            product.isFavorite = false
            productRepository.update(product)
        }
    }


    private fun createFavorite(product: Product, size: String): Favorite {
        return Favorite(
            size = size,
            idProduct = product.id
        )
    }


    fun insertFavorite(product: Product, size: String) {
        val favorite = createFavorite(product, size)
        viewModelScope.launch {
            favoriteRepository.insert(favorite)
            product.isFavorite = true
            productRepository.update(product)
            updateFavoriteFirebase(favoriteRepository.getAllList())
        }
    }


    companion object {
        const val LAST_EDIT = "lastEdit"
        const val DATA = "data"
        const val TAG = "FAVORITE_VIEW_MODEL"
    }
}
