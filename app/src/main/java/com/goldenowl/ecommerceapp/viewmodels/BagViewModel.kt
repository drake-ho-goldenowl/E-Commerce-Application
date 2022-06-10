package com.goldenowl.ecommerceapp.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.utilities.BAG_FIREBASE
import com.goldenowl.ecommerceapp.utilities.LAST_EDIT_TIME_BAG
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.*

class BagViewModel(
    private val productDao: ProductDao,
    private val bagDao: BagDao,
    private val favoriteDao: FavoriteDao,
    val userManager: UserManager
) :
    BaseViewModel() {
    private val db = Firebase.firestore
    private val statusIdFavorite = MutableStateFlow(Pair("",""))
    val favorite : LiveData<Favorite?> = statusIdFavorite.flatMapLatest {
        favoriteDao.getFavoriteFlow(it.first,it.second)
    }.asLiveData()


    fun setFavorite(idProduct: String,color: String){
        statusIdFavorite.value = Pair(idProduct,color)
    }


    private fun updateFavoriteFirebase(bags: List<Bag>){
        val docData: MutableMap<String, Any> = HashMap()
        LAST_EDIT_TIME_BAG = Date()
        docData[FavoriteViewModel.LAST_EDIT] = LAST_EDIT_TIME_BAG!!
        docData[FavoriteViewModel.DATA] = bags
        db.collection(BAG_FIREBASE).document(userManager.getAccessToken())
            .set(docData)
            .addOnSuccessListener {
                Log.d(UserManager.TAG, "DocumentSnapshot added")
            }
            .addOnFailureListener { e ->
                Log.w(UserManager.TAG, "Error adding document", e)
            }
    }

    fun removeBag(bag: Bag){
        viewModelScope.launch {
            bagDao.delete(bag)
            checkBags(bag)
            updateFavoriteFirebase(bagDao.getAllList())
        }
    }

    private suspend fun checkBags(bag: Bag){
        val favorite = favoriteDao.getFavoriteWithIdProduct(bag.idProduct,bag.color)
        favorite.isBag = false
        favoriteDao.update(favorite)
    }


    private fun createBag(idProduct: String, color: String, size: String): Bag {
        return Bag(
            size = size,
            color = color,
            idProduct =  idProduct,
            quantity = 1
        )
    }

    fun insertBag(product: Product, color: String, size: String,favorite: Favorite?) {
        val bag = createBag(product.id, color, size)
        viewModelScope.launch {
            bagDao.insert(bag)
            if(favorite != null){
                favorite.isBag = true
                favoriteDao.update(favorite)
            }
            updateFavoriteFirebase(bagDao.getAllList())
        }
    }
}


class BagViewModelFactory(
    private val productDao: ProductDao,
    private val bagDao: BagDao,
    private val favoriteDao: FavoriteDao,
    private val userManager: UserManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BagViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BagViewModel(productDao, bagDao,favoriteDao,userManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
