package com.goldenowl.ecommerceapp.viewmodels

import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.utilities.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
    private val bagRepository: BagRepository,
    private val shippingAddressRepository: ShippingAddressRepository,
    private val orderRepository: OrderRepository,
    private val userManager: UserManager,
    private val db: FirebaseFirestore,
) : ViewModel() {
    val category = productRepository.getAllCategory().asLiveData()
    val favorites = favoriteRepository.getAll().asLiveData()

    fun getProductWithCategory(category: String): MutableLiveData<List<Product>> {
        val result: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
        val source = Source.CACHE
        db.collection(PRODUCT_FIREBASE).whereEqualTo(CATEGORY, category).get(source)
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Product>()
                for (document in documents) {
                    list.add(document.toObject())
                }
                result.postValue(list)
            }
        return result
    }

    fun getNewProduct(): MutableLiveData<List<Product>> {
        val result: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
        val source = Source.CACHE
        db.collection(PRODUCT_FIREBASE).orderBy(CREATED_DATE, Query.Direction.DESCENDING).limit(
            LIMIT.toLong()
        )
            .get(source)
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Product>()
                for (document in documents) {
                    list.add(document.toObject())
                }
                result.postValue(list)
            }
        return result
    }

    fun getSaleProduct(): MutableLiveData<List<Product>> {
        val result: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
        val source = Source.CACHE
        db.collection(PRODUCT_FIREBASE).whereNotEqualTo(SALE_PERCENT, null).get(source)
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Product>()
                for (document in documents) {
                    list.add(document.toObject())
                }
                result.postValue(list)
            }
        return result
    }

    private var bags = Bags()

    init {
        if (!userManager.isLogged()) {
            viewModelScope.launch {
                bagRepository.deleteAll()
                favoriteRepository.deleteAll()
                shippingAddressRepository.deleteAll()
                orderRepository.deleteAll()
            }
        }
        fetchProduct()
        fetchFavorites()
        fetchAddress()
        fetchBag()
        fetchOrder()
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
                        fetchRatingProduct(product)
                    }
                }
            }
        }
    }

    private fun fetchRatingProduct(product: Product) {
        db.collection(REVIEW_FIREBASE).whereEqualTo(ID_PRODUCT, product.id).get()
            .addOnSuccessListener { documents ->
                viewModelScope.launch {
                    val listRating: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0)
                    for (document in documents) {
                        val review = document.toObject<Review>()
                        if (review.star in 1..5) {
                            listRating[review.star.toInt() - 1]++
                        }
                    }
                    product.numberReviews = product.getTotalRating(listRating)
                    product.reviewStars = product.getAverageRating(listRating)
                    productRepository.insert(product)
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

    private fun fetchAddress() {
        if (!userManager.isLogged()) {
            return
        }
        db.collection(USER_FIREBASE).document(userManager.getAccessToken()).collection(
            ADDRESS_USER
        ).get()
            .addOnSuccessListener { result ->
                viewModelScope.launch {
                    shippingAddressRepository.deleteAll()
                    for (document in result) {
                        if (document.id != LAST_EDIT) {
                            val shippingAddress = document.toObject<ShippingAddress>()
                            shippingAddressRepository.insert(shippingAddress)
                        }
                    }
                }
            }
    }

    private fun fetchOrder() {
        if (!userManager.isLogged()) {
            return
        }
        db.collection(USER_FIREBASE).document(userManager.getAccessToken()).collection(ORDER_USER)
            .get().addOnSuccessListener { result ->
                viewModelScope.launch {
                    orderRepository.deleteAll()
                    for (document in result) {
                        if (document.id != LAST_EDIT) {
                            orderRepository.insert(document.toObject())
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

    companion object {
        const val LIMIT = 4
        const val CATEGORY = "categoryName"
        const val CREATED_DATE  = "createdDate"
        const val SALE_PERCENT = "salePercent"
        const val ID_PRODUCT = "idProduct"
    }
}