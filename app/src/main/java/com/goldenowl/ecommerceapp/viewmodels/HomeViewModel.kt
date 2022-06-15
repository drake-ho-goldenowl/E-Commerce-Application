package com.goldenowl.ecommerceapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.utilities.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
    private val bagRepository: BagRepository,
    private val userManager: UserManager
) : ViewModel() {
    val product = productRepository.getAll().asLiveData()
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
                bagRepository.deleteAll()
                favoriteRepository.deleteAll()
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
            for (doc in value!!) {
                viewModelScope.launch {
                    val product = doc.toObject<Product>()
                    productRepository.insert(product)
                }
            }
        }
    }

    private fun fetchFavorites() {
        if (!userManager.isLogged()) {
            return
        }
        db.collection(FAVORITE_FIREBASE).document(userManager.getAccessToken())
            .get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val favorites = documentSnapshot.toObject<Favorites>()!!

                    if (LAST_EDIT_TIME_FAVORITES == null || favorites.lastEdit!! > LAST_EDIT_TIME_FAVORITES) {

                        for (favorite in favorites.data!!) {
                            viewModelScope.launch {
                                favoriteRepository.insert(favorite)
                                productRepository.updateIsFavorite(favorite.idProduct,true)
                            }
                        }
                        LAST_EDIT_TIME_FAVORITES =
                            documentSnapshot.getTimestamp(FavoriteViewModel.LAST_EDIT)?.toDate()!!
                    } else if (favorites.lastEdit!! < LAST_EDIT_TIME_FAVORITES) {
                        viewModelScope.launch {
                            favoriteRepository.updateFavoriteFirebase(
                                db,
                                userManager.getAccessToken()
                            )
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

                        for (bag in bags.data!!) {
                            viewModelScope.launch {
                                bagRepository.insert(bag)
                            }
                        }
                        LAST_EDIT_TIME_BAG =
                            snapshot.getTimestamp(FavoriteViewModel.LAST_EDIT)?.toDate()!!
                    }
                }
            }
    }
}