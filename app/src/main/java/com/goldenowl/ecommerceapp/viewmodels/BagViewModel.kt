package com.goldenowl.ecommerceapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private val statusIdFavorite = MutableStateFlow(Triple("", "", ""))
    val favorite: LiveData<Favorite?> = statusIdFavorite.flatMapLatest {
        favoriteRepository.getFavorite(it.first, it.second, it.third)
    }.asLiveData()

    val bags: LiveData<List<BagAndProduct>> = bagRepository.getAllBagAndProduct().asLiveData()
    val disMiss: MutableLiveData<Boolean> = MutableLiveData()

    fun setFavorite(idProduct: String, size: String, color: String) {
        statusIdFavorite.value = Triple(idProduct, size, color)
    }


    fun updateBagFirebase() {
        viewModelScope.launch {
            bagRepository.updateBagFirebase(userManager.getAccessToken())
        }
    }

    fun insertFavorite(product: Product, size: String, color: String) {
        viewModelScope.launch {
            val productNew = favoriteRepository.insertFavorite(product, size, color)
            favoriteRepository.updateIsBag(product.id, size, color, true)
            productRepository.update(productNew)
            favoriteRepository.updateFavoriteFirebase(userManager.getAccessToken())
        }
    }

    fun removeBag(bag: Bag) {
        viewModelScope.launch {
            bagRepository.delete(bag)
            favoriteRepository.updateIsBag(bag.idProduct, bag.size, bag.color, false)
            updateBagFirebase()
            favoriteRepository.updateFavoriteFirebase(userManager.getAccessToken())
        }
    }

    fun plusQuantity(bag: Bag) {
        viewModelScope.launch {
            bagRepository.updateQuantity(bag.idProduct, bag.color, bag.size, bag.quantity + 1)
            updateBagFirebase()
        }
    }

    fun minusQuantity(bag: Bag) {
        viewModelScope.launch {
            if (bag.quantity > 1) {
                bagRepository.updateQuantity(bag.idProduct, bag.color, bag.size, bag.quantity - 1)
                updateBagFirebase()
            } else {
                removeBag(bag)
            }
        }
    }


    fun insertBag(idProduct: String, color: String, size: String, favorite: Favorite?) {
        viewModelScope.launch {
            bagRepository.insertBag(
                idProduct,
                color,
                size
            )
            if (favorite != null) {
                favoriteRepository.updateIsBag(idProduct, size, color, true)
                favoriteRepository.updateFavoriteFirebase(userManager.getAccessToken())
            }
            updateBagFirebase()
            disMiss.postValue(true)
        }
    }
}
