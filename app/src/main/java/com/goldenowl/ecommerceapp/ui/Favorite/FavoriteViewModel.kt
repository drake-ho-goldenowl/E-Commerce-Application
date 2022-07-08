package com.goldenowl.ecommerceapp.ui.Favorite

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.data.BagRepository
import com.goldenowl.ecommerceapp.data.Favorite
import com.goldenowl.ecommerceapp.data.FavoriteRepository
import com.goldenowl.ecommerceapp.data.UserManager
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val bagRepository: BagRepository,
    private val userManager: UserManager,
) :
    BaseViewModel() {
    val favoriteAndProducts = favoriteRepository.favoriteAndProduct
    val disMiss = MutableLiveData(false)

    fun fetchFavorites() {
        favoriteRepository.fetchFavoriteAndProduct()
    }

    fun insertBag(idProduct: String, color: String, size: String) {
        bagRepository.insertBag(idProduct, color, size)
    }


    fun removeFavorite(favorite: Favorite) {
        favoriteRepository.removeFavoriteFirebase(favorite)
    }

    fun insertFavorite(idProduct: String, size: String, color: String) {
        favoriteRepository.insertFavorite(idProduct, color, size)
        disMiss.postValue(true)
    }


    fun setButtonBag(context: Context, buttonView: View, favorite: Favorite) {
        bagRepository.setButtonBag(context, buttonView, favorite)
    }

    fun isLogged(): Boolean {
        return userManager.isLogged()
    }

    companion object {
        const val TAG = "FAVORITE_VIEW_MODEL"
    }
}
