package com.goldenowl.ecommerceapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.utilities.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.*

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
        return products.filter { it.isPopular }
    }

    private var bags = Bags()


    init {
        println("Run Home")
        if (!userManager.isLogged()) {
            viewModelScope.launch {
                bagDao.deleteAll()
                favoriteDao.deleteAll()
            }
        }
        fetchProduct()
        fetchFavorites()
        fetchBag()
    }

    private fun fetchProduct() {
        db.collection(PRODUCT_FIREBASE).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(ShopViewModel.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            viewModelScope.launch {
                for (doc in value!!) {
                    val product = doc.toObject<Product>()
                    if (favoriteDao.countFavorite() > 0) {
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
        var favorites: Favorites
        db.collection(FAVORITE_FIREBASE).document(userManager.getAccessToken())
            .get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    favorites = documentSnapshot.toObject<Favorites>()!!

                    if (LAST_EDIT_TIME_FAVORITES == null || favorites.lastEdit!! > LAST_EDIT_TIME_FAVORITES) {
                        viewModelScope.launch {
                            for (favorite in favorites.data!!) {
                                val product = productDao.getProduct(favorite.idProduct)
                                if (product.isFavorite) {
                                    favoriteDao.insert(favorite)
                                } else {
                                    insertFavorite(product, favorite)
                                }
                            }
                        }
                        LAST_EDIT_TIME_FAVORITES =
                            documentSnapshot.getTimestamp(FavoriteViewModel.LAST_EDIT)?.toDate()!!
                    } else if (favorites.lastEdit!! < LAST_EDIT_TIME_FAVORITES) {
                        viewModelScope.launch {
                            updateFavoriteFirebase(favoriteDao.getAllList())
                        }
                    }
                }
            }
    }

    private fun fetchBag() {
        if (!userManager.isLogged()) {
            return
        }
        db.collection(BAG_FIREBASE).document(userManager.getAccessToken())
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

    private fun updateFavoriteFirebase(favorites: List<Favorite>) {
        val docData: MutableMap<String, Any> = HashMap()
        LAST_EDIT_TIME_FAVORITES = Date()
        docData[FavoriteViewModel.LAST_EDIT] = LAST_EDIT_TIME_FAVORITES!!
        docData[FavoriteViewModel.DATA] = favorites
        db.collection(FAVORITE_FIREBASE).document(userManager.getAccessToken())
            .set(docData)
            .addOnSuccessListener {
                Log.d(UserManager.TAG, "DocumentSnapshot added")
            }
            .addOnFailureListener { e ->
                Log.w(UserManager.TAG, "Error adding document", e)
            }
    }


    private fun insertFavorite(product: Product, favorite: Favorite) {
        viewModelScope.launch {
            favoriteDao.insert(favorite)
            product.isFavorite = true
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
            return HomeViewModel(productDao, favoriteDao, bagDao, userManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}