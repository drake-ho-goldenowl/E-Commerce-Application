package com.goldenowl.ecommerceapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.utilities.LAST_EDIT_TIME
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class HomeViewModel(
    private val productDao: ProductDao,
    private val favoriteDao: FavoriteDao,
    private val userManager: UserManager
) : ViewModel() {
    val product = productDao.getAll().asLiveData()
    private val db = Firebase.firestore

    fun filterSale(products: List<Product>): List<Product> {
        return products.filter { it.salePercent != null }
    }

    fun filterNew(products: List<Product>): List<Product> {
        return products.filter { it.isPopular == true }
    }

    private var favorites = Favorites()


    init {
        println("Run Home")
        fetchData()
        fetchFavorites()
    }

    private fun fetchData() {
        db.collection("products").addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(ShopViewModel.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            viewModelScope.launch {
                favorites.data = favoriteDao.getAllList()
            }
            for (doc in value!!) {
                viewModelScope.launch {
                    val product = doc.toObject<Product>()
                    if (favoriteDao.getAllId().isNotEmpty()) {
                        product.isFavorite = checkFavorite(favoriteDao.getAllId(), product)
                    }
                    if (favorites.data!!.isNotEmpty()) {
                        refreshFavorites(product, favorites.data!!)
                    }
                    productDao.insert(product)
                }
            }
        }
    }

    private suspend fun refreshFavorites(product: Product, favorites: List<Favorite>) {
        var flag = false
        for (favorite in favorites) {
            if (flag) break
            if (favorite.id == product.id) {
                for (color in product.colors) {
                    if (flag) break
                    if (favorite.color == color.color) {
                        for (size in color.sizes) {
                            if (favorite.size == size.size && favorite.quantity != size.quantity) {
                                favorite.quantity = size.quantity
                                println("size ${favorite.quantity}")
                                favoriteDao.update(favorite)
                                flag = true
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    private fun fetchFavorites() {
        if (!userManager.isLogged()) {
            return
        }
        db.collection("favorites").document(userManager.getAccessToken())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(FavoriteViewModel.TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    favorites = snapshot.toObject<Favorites>()!!
                    if (favorites.lastEdit != LAST_EDIT_TIME) {
                        for (favorite in favorites.data!!) {
                            viewModelScope.launch {
                                favoriteDao.insert(favorite)
                            }
                        }
                        LAST_EDIT_TIME =
                            snapshot.getTimestamp(FavoriteViewModel.LAST_EDIT)?.toDate()!!
                    }
                }
            }
    }

    private fun checkFavorite(favorites: List<String>, product: Product): Boolean {
        for (favorite in favorites) {
            if (product.id == favorite) {
                return true
            }
        }
        return false
    }
}

class HomeViewModelFactory(
    private val productDao: ProductDao,
    private val favoriteDao: FavoriteDao,
    private val userManager: UserManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(productDao, favoriteDao, userManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}