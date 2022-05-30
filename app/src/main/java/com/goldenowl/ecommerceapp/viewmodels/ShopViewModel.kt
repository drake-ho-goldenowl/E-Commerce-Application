package com.goldenowl.ecommerceapp.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.data.ProductDao
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch


class ShopViewModel(private val productDao: ProductDao): ViewModel() {
    private val db = Firebase.firestore
    val statusQuery = MutableStateFlow("")

    val allCategory: LiveData<List<String>> = productDao.getAllCategory().asLiveData()
    val products: LiveData<List<Product>> = statusQuery.flatMapLatest {
        if(it.isBlank())
            productDao.getAll()
        else
            productDao.filterByCategory(it)
    }.asLiveData()



    init {
        fetchData()
    }

    private fun fetchData(){
        db.collection("products").addSnapshotListener{value,e->
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

    fun filterByCategory(category: String){
        statusQuery.value = category
    }


    companion object{
        const val TAG = "ShopViewModel"
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
