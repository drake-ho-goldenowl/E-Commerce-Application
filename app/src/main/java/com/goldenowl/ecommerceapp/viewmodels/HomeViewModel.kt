package com.goldenowl.ecommerceapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.utilities.LAST_EDIT_TIME_BAG
import com.goldenowl.ecommerceapp.utilities.LAST_EDIT_TIME_FAVORITES
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class HomeViewModel(
    private val productDao: ProductDao,
    private val favoriteDao: FavoriteDao,
    private val bagDao: BagDao,
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
    private var bags = Bags()


    init {
        println("Run Home")
        fetchData()
        fetchFavorites()
        fetchBag()
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
                        product.isFavorite = checkFavorite(favoriteDao.getAllIdProduct(), product)
                    }
                    productDao.insert(product)
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
                    if (favorites.lastEdit != LAST_EDIT_TIME_FAVORITES) {
                        viewModelScope.launch {
                            for (favorite in favorites.data!!) {
                                val product = productDao.getProduct(favorite.idProduct)
                                if(product.isFavorite){
                                    favoriteDao.insert(favorite)
                                }
                                else{
                                    insertFavorite(product,favorite)
                                }
                            }
                        }
                        LAST_EDIT_TIME_FAVORITES =
                            snapshot.getTimestamp(FavoriteViewModel.LAST_EDIT)?.toDate()!!
                    }
                }
            }
    }

    private fun insertFavorite(product: Product,favorite: Favorite) {
        viewModelScope.launch {
            favoriteDao.insert(favorite)
            product.isFavorite = true
            fetchProduct(product)
        }
    }


    private fun fetchBag() {
        if (!userManager.isLogged()) {
            return
        }
        db.collection("bags").document(userManager.getAccessToken())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(FavoriteViewModel.TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    bags = snapshot.toObject<Bags>()!!
                    if (bags.lastEdit != LAST_EDIT_TIME_BAG) {
                        viewModelScope.launch {
                            for (bag in bags.data!!) {
                                bagDao.insert(bag)
                            }
                        }
                        LAST_EDIT_TIME_BAG =
                            snapshot.getTimestamp(FavoriteViewModel.LAST_EDIT)?.toDate()!!
                    }
                }
            }
    }


    private fun fetchProduct(product: Product) {
        viewModelScope.launch {
            productDao.update(product)
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
    private val bagDao: BagDao,
    private val userManager: UserManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(productDao, favoriteDao,bagDao ,userManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}