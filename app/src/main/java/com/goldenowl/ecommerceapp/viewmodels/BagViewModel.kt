package com.goldenowl.ecommerceapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.utilities.BAG_FIREBASE
import com.goldenowl.ecommerceapp.utilities.LAST_EDIT_TIME_BAG
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.set

@HiltViewModel
class BagViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val bagRepository: BagRepository,
    private val favoriteRepository: FavoriteRepository,
    val userManager: UserManager
) :
    BaseViewModel() {
    private val db = Firebase.firestore
    private val statusIdFavorite = MutableStateFlow(Pair("",""))
    val favorite : LiveData<Favorite?> = statusIdFavorite.flatMapLatest {
        favoriteRepository.getFavoriteFlow(it.first,it.second)
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
            bagRepository.delete(bag)
            checkBags(bag)
            updateFavoriteFirebase(bagRepository.getAllList())
        }
    }

    private suspend fun checkBags(bag: Bag){
        val favorite = favoriteRepository.getFavoriteWithIdProduct(bag.idProduct,bag.color)
        favorite.isBag = false
        favoriteRepository.update(favorite)
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
            bagRepository.insert(bag)
            if(favorite != null){
                favorite.isBag = true
                favoriteRepository.update(favorite)
            }
            updateFavoriteFirebase(bagRepository.getAllList())
        }
    }
}
