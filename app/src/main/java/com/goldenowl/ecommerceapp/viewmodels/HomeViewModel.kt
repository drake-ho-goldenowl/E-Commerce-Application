package com.goldenowl.ecommerceapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.FavoriteDao
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.data.ProductDao
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class HomeViewModel(private val productDao: ProductDao,private val favoriteDao: FavoriteDao): ViewModel(){
    val product = productDao.getAll().asLiveData()
    private val db = Firebase.firestore

    
    fun filterSale(product : List<Product>): List<Product>{
        return product.filter{it.salePercent != null }
    }


    init {
        fetchData()
    }

    private fun fetchData() {
        db.collection("products").addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(ShopViewModel.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            for (doc in value!!) {
                viewModelScope.launch {
                    val product = doc.toObject<Product>()
                    if(favoriteDao.getAllId().isNotEmpty()){
                        product.isFavorite = checkFavorite(favoriteDao.getAllId(),product)
                    }
                    productDao.insert(product)
                }
            }
        }
    }

    private fun checkFavorite(favorites: List<String>,product: Product): Boolean{
        for(favorite in favorites){
            if(product.id == favorite){
                return true
            }
        }
        return false
    }


}


class HomeViewModelFactory(
    private val productDao: ProductDao,
    private val favoriteDao: FavoriteDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(productDao,favoriteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}