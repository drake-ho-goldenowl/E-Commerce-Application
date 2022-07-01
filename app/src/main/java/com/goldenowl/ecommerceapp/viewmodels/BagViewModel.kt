package com.goldenowl.ecommerceapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.*
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class BagViewModel @Inject constructor(
    private val bagRepository: BagRepository,
    private val favoriteRepository: FavoriteRepository,
    private val userManager: UserManager,
    private val db: FirebaseFirestore
) :
    BaseViewModel() {
    private val statusIdFavorite = MutableStateFlow(Triple("", "", ""))
    val favorite: LiveData<Favorite?> = statusIdFavorite.flatMapLatest {
        favoriteRepository.getFavorite(it.first, it.second, it.third)
    }.asLiveData()

    private val filterSearch = MutableStateFlow("")
    val bags: LiveData<List<BagAndProduct>> = filterSearch.flatMapLatest {
        if (it.isNotBlank()) {
            bagRepository.filterBySearch(it)
        } else {
            bagRepository.getAllBagAndProduct()
        }
    }.asLiveData()

    val allBags: LiveData<List<BagAndProduct>> = bagRepository.getAllBagAndProduct().asLiveData()

    val disMiss: MutableLiveData<Boolean> = MutableLiveData()

    fun setFavorite(idProduct: String, size: String, color: String) {
        statusIdFavorite.value = Triple(idProduct, size, color)
    }

    private fun updateBagFirebase() {
        viewModelScope.launch {
            bagRepository.updateBagFirebase(db, userManager.getAccessToken())
        }
    }

    fun insertFavorite(product: Product, size: String, color: String) {
        viewModelScope.launch {
            favoriteRepository.insertFavorite(product, size, color)
            favoriteRepository.updateFavoriteFirebase(db, userManager.getAccessToken())
        }
    }

    fun removeBag(bag: Bag) {
        viewModelScope.launch {
            bagRepository.delete(bag)
            updateBagFirebase()
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


    fun insertBag(idProduct: String, color: String, size: String) {
        viewModelScope.launch {
            bagRepository.insertBag(
                idProduct,
                color,
                size
            )
            updateBagFirebase()
            disMiss.postValue(true)
        }
    }

    fun calculatorTotal(lists: List<BagAndProduct>, salePercent: Long): Int {
        var total = 0.0
        viewModelScope.launch {
            for (bagAndProduct in lists) {
                val size = bagAndProduct.product.getColorAndSize(
                    bagAndProduct.bag.color,
                    bagAndProduct.bag.size
                )
                if (size != null) {
                    var salePercent = 0
                    if (bagAndProduct.product.salePercent != null) {
                        salePercent = bagAndProduct.product.salePercent
                    }
                    val price = size.price * (100 - salePercent) / 100
                    total += (price * bagAndProduct.bag.quantity)
                }
            }
        }
        total -= (salePercent * total / 100)
        return total.roundToInt()
    }

    fun setSearch(string: String) {
        filterSearch.value = string
    }

    fun isLogged(): Boolean {
        return userManager.isLogged()
    }
}
