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
class BagViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val bagRepository: BagRepository,
    private val favoriteRepository: FavoriteRepository,
    val userManager: UserManager
) :
    BaseViewModel() {
    private val statusIdFavorite = MutableStateFlow(Triple("", "",""))
    val favorite: LiveData<Favorite?> = statusIdFavorite.flatMapLatest {
        favoriteRepository.getFavoriteFlow(it.first, it.second,it.third)
    }.asLiveData()

    val bags: LiveData<List<BagAndProduct>> = bagRepository.getAllBagAndProduct().asLiveData()

    fun setFavorite(idProduct: String,size: String, color: String) {
        statusIdFavorite.value = Triple(idProduct, size,color)
    }


    private fun updateBagFirebase() {
        viewModelScope.launch {
            bagRepository.updateBagFirebase(userManager.getAccessToken())
        }
    }

    fun removeBag(bag: Bag) {
        viewModelScope.launch {
            bagRepository.delete(bag)
            checkBags(bag)
            updateBagFirebase()
        }
    }

    private suspend fun checkBags(bag: Bag) {
        val favorite = favoriteRepository.getFavoriteWithIdProduct(bag.idProduct, bag.color)
        favorite.isBag = false
        favoriteRepository.update(favorite)
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
        }
    }
}
