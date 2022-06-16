package com.goldenowl.ecommerceapp.viewmodels

import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.R
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
    val favorites = favoriteRepository.getAll().asLiveData()
    private val db = Firebase.firestore

    fun filterSale(products: List<Product>): List<Product> {
        return products.filter { it.salePercent != null }
    }

    fun filterNew(products: List<Product>): List<Product> {
        return products.filter { it.isPopular }
    }

    private var bags = Bags()

    init {
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
            value?.let {
                for (doc in value) {
                    viewModelScope.launch {
                        val product = doc.toObject<Product>()
                        productRepository.insert(product)
                    }
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
                    val favorites = documentSnapshot.toObject<Favorites>()

                    favorites?.let {
                        if (favorites.lastEdit == null) {
                            viewModelScope.launch {
                                favoriteRepository.updateFavoriteFirebase(
                                    db,
                                    userManager.getAccessToken()
                                )
                            }
                        } else if (LAST_EDIT_TIME_FAVORITES == null || favorites.lastEdit!! > LAST_EDIT_TIME_FAVORITES) {

                            favorites.data?.let { list ->
                                for (favorite in list) {
                                    viewModelScope.launch {
                                        favoriteRepository.insert(favorite)
                                    }
                                }
                                LAST_EDIT_TIME_FAVORITES =
                                    documentSnapshot.getTimestamp(FavoriteViewModel.LAST_EDIT)
                                        ?.toDate()
                            }
                        } else {
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
                        bags.data?.let {
                            for (bag in it) {
                                viewModelScope.launch {
                                    bagRepository.insert(bag)
                                }
                            }
                            LAST_EDIT_TIME_BAG =
                                snapshot.getTimestamp(FavoriteViewModel.LAST_EDIT)?.toDate()
                        }
                    }
                }
            }
    }

    fun setButtonFavorite(context: Context, buttonView: View, idProduct: String) {
        if (!userManager.isLogged()) {
            buttonView.visibility = View.GONE
        } else {
            buttonView.visibility = View.VISIBLE
            viewModelScope.launch {
                val isFavorite = favoriteRepository.checkProductHaveFavorite(idProduct)
                if (isFavorite) {
                    buttonView.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.btn_favorite_active
                    )
                } else {
                    buttonView.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.btn_favorite_no_active
                    )
                }
            }
        }
    }
}