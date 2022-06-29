package com.goldenowl.ecommerceapp.viewmodels

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.*
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val bagRepository: BagRepository,
    private val userManager: UserManager,
    private val db: FirebaseFirestore
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

    val bags = bagRepository.getAll().asLiveData()
    val disMiss: MutableLiveData<Boolean> = MutableLiveData()

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

    private fun updateFavoriteFirebase() {
        viewModelScope.launch {
            favoriteRepository.updateFavoriteFirebase(db, userManager.getAccessToken())
        }
    }


    fun insertBag(idProduct: String, color: String, size: String) {
        viewModelScope.launch {
            bagRepository.insertBag(
                idProduct,
                color,
                size,
            )
            bagRepository.updateBagFirebase(db, userManager.getAccessToken())
        }
    }


    fun removeFavorite(favorite: Favorite) {
        viewModelScope.launch {
            favoriteRepository.delete(favorite)
            updateFavoriteFirebase()
        }
    }

    fun insertFavorite(product: Product, size: String, color: String) {
        viewModelScope.launch {
            favoriteRepository.insertFavorite(product, size, color)
            updateFavoriteFirebase()
            disMiss.postValue(true)
        }
    }


    fun setButtonBag(context: Context, buttonView: View, favorite: Favorite) {
        viewModelScope.launch {
            val isBag = bagRepository.checkBagHaveFavorite(
                favorite.idProduct,
                favorite.color,
                favorite.size
            )
            if (isBag) {
                buttonView.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.btn_bag_active
                )
            } else {
                buttonView.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.btn_bag_no_active
                )
            }
        }
    }

    fun isLogged(): Boolean {
        return userManager.isLogged()
    }

    companion object {
        const val LAST_EDIT = "lastEdit"
        const val DATA = "data"
        const val TAG = "FAVORITE_VIEW_MODEL"
    }
}
