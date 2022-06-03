package com.goldenowl.ecommerceapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.goldenowl.ecommerceapp.data.Favorite
import com.goldenowl.ecommerceapp.data.FavoriteDao
import com.goldenowl.ecommerceapp.data.ProductDao
import com.goldenowl.ecommerceapp.data.UserManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FavoriteViewModel(
    private val productDao: ProductDao,
    private val favoriteDao: FavoriteDao,
    private val userManager: UserManager
) :
    ViewModel() {
    private val db = Firebase.firestore
    private val products = productDao.getAll().asLiveData()
    private var favorites = favoriteDao.getAll().asLiveData()

    init {
//        fetchData()
    }
//    fun fetchData(){
//        db.collection("favorites").document(userManager.getAccessToken()).addSnapshotListener { snapshot, error ->
//            if (error != null) {
//                Log.w(TAG, "Listen failed.", error)
//                return@addSnapshotListener
//            }
//            if (snapshot != null && snapshot.exists()) {
//                val lists = snapshot.data
//                for (doc in lists!!) {
//                    viewModelScope.launch {
//                        productDao.insert(doc)
//                    }
//                }
//            } else {
//            }
//        }
//    }



    fun addFavorite(favorites: List<Favorite>){
        db.collection("users").document(userManager.getAccessToken())
            .set(favorites)
            .addOnSuccessListener {
                Log.d(UserManager.TAG, "DocumentSnapshot added")
            }
            .addOnFailureListener { e ->
                Log.w(UserManager.TAG, "Error adding document", e)
            }
    }


    companion object{
        const val TAG = "FAVORITE_VIEW_MODEL"
    }
}

class FavoriteViewModelFactory(
    private val productDao: ProductDao,
    private val favoriteDao: FavoriteDao,
    private val userManager: UserManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteViewModel(productDao, favoriteDao, userManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
