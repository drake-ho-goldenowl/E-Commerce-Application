package com.goldenowl.ecommerceapp.ui.Shop

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.goldenowl.ecommerceapp.data.FavoriteRepository
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.data.ProductRepository
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import com.goldenowl.ecommerceapp.utilities.PRODUCT_FIREBASE
import com.goldenowl.ecommerceapp.utilities.SALE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject


@HiltViewModel
class ShopViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
    private val db: FirebaseFirestore
) :
    BaseViewModel() {
    private val statusIdProduct = MutableStateFlow("")
    private val statusFilter = MutableStateFlow(Pair("", ""))
    val product: LiveData<Product> = statusIdProduct.flatMapLatest {
        productRepository.getProduct(it)
    }.asLiveData()
    val statusSort = MutableLiveData(0)
    val allCategory = productRepository.getAllCategory().asLiveData()
    var loadMore = MutableLiveData(true)
    val products = MutableLiveData<List<Product>>()
    val btnFavorite = MutableLiveData<View>()
    var query: Query? = null

    fun filterPrice(min: Float, max: Float, list: List<Product>): MutableList<Product> {
        val temp = list.toMutableList()
        for (product in list) {
            val price = product.colors[0].sizes[0].price
            if (price < min || price > max) {
                temp.remove(product)
            }
        }
        return temp
    }

    fun loadMore(list: List<Product>) {
        isLoading.postValue(true)
        statusFilter.value.apply {
            if (this.first.isNotBlank() && this.second.isNotBlank()) {
                loadMoreCategoryAndSearch(this.second, this.first, list)
            } else if (this.first.isNotBlank()) {
                loadMoreCategory(this.first, list)
            } else if (this.second.isNotBlank()) {
                loadMoreSearch(this.second, list)
            } else {
                loadMoreAll(list)
            }
        }
    }

    private fun loadMoreAll(list: List<Product>) {
        val queryBase = db.collection(PRODUCT_FIREBASE)
            .orderBy(CREATED_DATE, Query.Direction.DESCENDING)
            .limit(LIMIT.toLong())
        loadMoreBase(list, queryBase)
    }

    private fun loadMoreCategory(category: String, list: List<Product>) {
        if (category == SALE) {
            loadMoreSaleProduct(list)
        } else {
            val queryBase = db.collection(PRODUCT_FIREBASE)
                .whereEqualTo(CATEGORY_NAME, category)
                .orderBy(CREATED_DATE, Query.Direction.DESCENDING)
                .limit(LIMIT.toLong())
            loadMoreBase(list, queryBase)
        }
    }

    private fun loadMoreSearch(search: String, list: List<Product>) {
        val queryBase = db.collection(PRODUCT_FIREBASE)
            .orderBy(CREATED_DATE, Query.Direction.DESCENDING)
            .limit(LIMIT.toLong())
        loadMoreBase(list, queryBase,search)
    }

    private fun loadMoreCategoryAndSearch(search: String, category: String, list: List<Product>) {
        val queryBase = db.collection(PRODUCT_FIREBASE)
            .whereEqualTo(CATEGORY_NAME, category)
            .orderBy(CREATED_DATE, Query.Direction.DESCENDING)
            .limit(LIMIT.toLong())
        loadMoreBase(list, queryBase,search)
    }

    private fun loadMoreSaleProduct(list: List<Product>) {
        val queryBase = db.collection(PRODUCT_FIREBASE)
            .whereNotEqualTo(SALE_PERCENT, null)
            .orderBy(SALE_PERCENT)
            .limit(LIMIT.toLong())
        loadMoreBase(list, queryBase)
    }

    private fun loadMoreBase(list: List<Product>, queryBase: Query,search : String = "") {
        if (query == null) {
            query = queryBase
        }
        query?.let {
            it.get()
                .addOnSuccessListener { documents ->
                    val temp = mutableListOf<Product>()
                    if (documents.size() != 0) {
                        loadMore.postValue(true)
                        temp.addAll(list)
                        for (document in documents) {
                            if(search.isNotBlank()){
                                val product = document.toObject<Product>()
                                if (product.title.lowercase().contains(search.lowercase())) {
                                    if(!temp.contains(product)){
                                        temp.add(product)
                                    }
                                }
                            }
                            else{
                                temp.add(document.toObject())
                            }
                        }
                        val lastVisible = documents.documents[documents.size() - 1]
                        products.postValue(temp)
                        query = queryBase.startAfter(lastVisible)
                    } else {
                        loadMore.postValue(false)
                    }
                    isLoading.postValue(false)
                }
                .addOnFailureListener {
                    loadMore.postValue(false)
                    isLoading.postValue(false)
                }
        }
    }


    fun setProduct(idProduct: String) {
        statusIdProduct.value = idProduct
    }

    fun filterSort(products: List<Product>): List<Product> {
        return when (statusSort.value) {
            0 -> products.sortedByDescending {
                it.isPopular
            }
            1 -> products.sortedByDescending {
                it.createdDate
            }
            2 -> products.sortedByDescending {
                it.numberReviews
            }
            3 -> products.sortedBy {
                val sale = it.salePercent ?: 0
                val price = it.colors[0].sizes[0].price - (sale.toFloat() / 100 * it.colors[0].sizes[0].price)
                println(it.salePercent.toString() + "     " + sale)
                println("------------------------------------------------")
                println(price)
                price
            }
            else -> {
                products.sortedByDescending {
                    val sale = it.salePercent ?: 0
                    val price =
                        it.colors[0].sizes[0].price - (sale.toFloat() / 100 * it.colors[0].sizes[0].price)
                    price
                }
            }
        }
    }

    fun setCategory(category: String) {
        statusFilter.value = Pair(category, statusFilter.value.second)
    }

    fun getCategory(): String {
        return statusFilter.value.first
    }

    fun setSearch(search: String) {
        statusFilter.value = Pair(statusFilter.value.first, search)
    }

    fun setSort(select: Int) {
        statusSort.postValue(select)
    }

    fun getAllSize(): MutableList<String> {
        val sizes: MutableSet<String> = mutableSetOf()
        product.value?.let {
            for (color in it.colors) {
                for (size in color.sizes) {
                    if (size.quantity > 0) {
                        sizes.add(size.size)
                    }
                }
            }
        }
        return sizes.toMutableList()
    }


    fun getAllColor(): MutableList<String> {
        val colors: MutableSet<String> = mutableSetOf()
        product.value?.let {
            for (color in it.colors) {
                color.color?.let { str ->
                    colors.add(str)
                }
            }
        }
        return colors.toMutableList()
    }

    fun setButtonFavorite(context: Context, buttonView: View, idProduct: String) {
        favoriteRepository.setButtonFavorite(context, buttonView, idProduct)
    }

    companion object {
        const val TAG = "ShopViewModel"
    }
}