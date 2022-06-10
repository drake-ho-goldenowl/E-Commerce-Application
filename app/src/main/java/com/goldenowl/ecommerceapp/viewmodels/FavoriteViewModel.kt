package com.goldenowl.ecommerceapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
    private val bagRepository: BagRepository,
    val userManager: UserManager
) :
    BaseViewModel() {
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

    fun updateFavoriteFirebase() {
        viewModelScope.launch {
            favoriteRepository.updateFavoriteFirebase(userManager.getAccessToken())
        }
    }


    fun insertBag(idProduct: String, color: String, size: String, favorite: Favorite?) {
        viewModelScope.launch {
            val favoriteNew = bagRepository.insertBag(
                idProduct,
                color,
                size,
                favorite,
                userManager.getAccessToken()
            )
            if (favoriteNew != null) {
                favoriteRepository.update(favoriteNew)
                favoriteRepository.updateFavoriteFirebase(userManager.getAccessToken())
            }
            bagRepository.updateBagFirebase(userManager.getAccessToken())
        }
    }


    fun removeFavorite(favorite: Favorite) {
        viewModelScope.launch {
            favoriteRepository.delete(favorite)
            checkFavorites(favorite)
            updateFavoriteFirebase()
        }
    }

    fun insertFavorite(product: Product,size: String,color: String){
        viewModelScope.launch {
            val productNew = favoriteRepository.insertFavorite(product, size,color)
            productRepository.update(productNew)
        }
    }

    private suspend fun checkFavorites(favorite: Favorite) {
        val id = favoriteRepository.getIdProduct(favorite.idProduct)
        if (id.isNullOrBlank()) {
            val product = productRepository.getProduct(favorite.idProduct)
            product.isFavorite = false
            productRepository.update(product)
        }
    }


    companion object {
        const val LAST_EDIT = "lastEdit"
        const val DATA = "data"
        const val TAG = "FAVORITE_VIEW_MODEL"
    }
}
