package com.goldenowl.ecommerceapp.ui.Home

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import com.goldenowl.ecommerceapp.ui.Shop.ShopViewModel
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
    private val shippingAddressRepository: ShippingAddressRepository,
    private val orderRepository: OrderRepository,
    private val userManager: UserManager,
    private val db: FirebaseFirestore,
) : BaseViewModel() {
    val category = productRepository.getAllCategory().asLiveData()
    val btnFavorite = MutableLiveData<View>()
    fun getProductWithCategory(category: String): MutableLiveData<List<Product>> {
        val result: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
        val source = Source.CACHE
        db.collection(PRODUCT_FIREBASE).whereEqualTo(CATEGORY_NAME, category).get(source)
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

    init {
        if (!userManager.isLogged()) {
            viewModelScope.launch {
                shippingAddressRepository.deleteAll()
                orderRepository.deleteAll()
            }
        }
        fetchProduct()
        fetchAddress()
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
        favoriteRepository.setButtonFavorite(context, buttonView, idProduct)
    }
}