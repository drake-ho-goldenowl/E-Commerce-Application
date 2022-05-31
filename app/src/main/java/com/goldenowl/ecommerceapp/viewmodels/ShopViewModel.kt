package com.goldenowl.ecommerceapp.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.data.ProductDao
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch


class ShopViewModel(private val productDao: ProductDao) : ViewModel() {
    private val db = Firebase.firestore
    val statusQuery = MutableStateFlow("")
    val statusSort = MutableStateFlow(0)
    val statusSearch = MutableStateFlow("")
    val allCategory: LiveData<List<String>> = productDao.getAllCategory().asLiveData()

    private val statusFilter = combine(statusSort, statusQuery,statusSearch) { sort, query, search ->
        "$sort|$query|$search"
    }
    val products: LiveData<List<Product>> = statusFilter.flatMapLatest {
        val str = it.split("|").toTypedArray()
        if(str[1].isNotBlank() && str[2].isNotBlank()){
            filterByCategoryAndSearch(str[2],str[1],str[0].toInt())
        }
        else if(str[1].isNotBlank()){
            filterByCategory(str[1],str[0].toInt())
        }
        else if(str[2].isNotBlank()){
            filterBySearch(str[2],str[0].toInt())
        }
        else filterBySort(str[0].toInt())
    }.asLiveData()

    private fun filterByCategory(category: String,select: Int): Flow<List<Product>> {
        return when (select) {
            0 -> productDao.filterByCategory(category, POPULAR)
            1 -> productDao.filterByCategory(category, NEWEST)
            2 -> productDao.filterByCategory(category, CUSTOMER_REVIEW)
            else -> {
                productDao.filterByCategory(category, CUSTOMER_REVIEW)
            }
        }
    }

    private fun filterBySort(select: Int): Flow<List<Product>> {
        return when (select) {
            0 -> productDao.filterBySort(POPULAR)
            1 -> productDao.filterBySort(NEWEST)
            2 -> productDao.filterBySort(CUSTOMER_REVIEW)
            else -> {
                productDao.filterBySort(CUSTOMER_REVIEW)
            }
        }
    }

    private fun filterBySearch(search: String, select: Int): Flow<List<Product>> {
        return when (select) {
            0 -> productDao.filterBySearch(search,POPULAR)
            1 -> productDao.filterBySearch(search,NEWEST)
            2 -> productDao.filterBySearch(search,CUSTOMER_REVIEW)
            else -> {
                productDao.filterBySearch(search,CUSTOMER_REVIEW)
            }
        }
    }


    private fun filterByCategoryAndSearch(search: String, category: String, select: Int): Flow<List<Product>> {
        return when (select) {
            0 -> productDao.filterByCategoryAndSearch(search,category,POPULAR)
            1 -> productDao.filterByCategoryAndSearch(search,category,NEWEST)
            2 -> productDao.filterByCategoryAndSearch(search,category,CUSTOMER_REVIEW)
            else -> {
                productDao.filterByCategoryAndSearch(search,category,CUSTOMER_REVIEW)
            }
        }
    }

    init {
        fetchData()
    }

    private fun fetchData() {
        db.collection("products").addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            for (doc in value!!) {
                viewModelScope.launch {
                    productDao.insert(doc.toObject<Product>())
                }
            }
        }
    }

    fun filterByCategory(category: String) {
        statusQuery.value = category
    }


    companion object {
        const val TAG = "ShopViewModel"
        const val POPULAR = "is_popular"
        const val NEWEST = "created_date"
        const val CUSTOMER_REVIEW = "number_reviews"
        const val PRICE_LOWEST_TO_HIGH = 3
        const val PRICE_HIGHEST_TO_LOW = 4
    }
}

class ShopViewModelFactory(private val productDao: ProductDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShopViewModel(productDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
