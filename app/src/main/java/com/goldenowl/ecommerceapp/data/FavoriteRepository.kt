package com.goldenowl.ecommerceapp.data

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import com.goldenowl.ecommerceapp.utilities.FAVORITE_FIREBASE
import com.goldenowl.ecommerceapp.utilities.PRODUCT_FIREBASE
import com.goldenowl.ecommerceapp.utilities.USER_FIREBASE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FavoriteRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val userManager: UserManager,
) {
    val favoriteAndProduct = MutableLiveData<MutableList<FavoriteAndProduct>>()
    val listIdProductFavorite = MutableLiveData<List<String>>()
    fun fetchFavoriteAndProduct() {
        if (userManager.isLogged()) {
            db.collection(USER_FIREBASE)
                .document(userManager.getAccessToken())
                .collection(FAVORITE_FIREBASE)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.size() == 0) {
                        favoriteAndProduct.postValue(mutableListOf())
                    } else {
                        val list = mutableListOf<FavoriteAndProduct>()
                        for (document in documents) {
                            val favorite = document.toObject<Favorite>()
                            db.collection(PRODUCT_FIREBASE).document(favorite.idProduct).get()
                                .addOnSuccessListener { document2 ->
                                    document2.toObject<Product>()?.let {
                                        list.add(FavoriteAndProduct(favorite, it))
                                    }
                                    favoriteAndProduct.postValue(list)
                                }
                        }
                    }
                }
        }
        getListIdProductFavorite()
    }


    fun insertFavorite(idProduct: String, color: String, size: String) {
        val favorite = Favorite(
            id = Date().time.toString(),
            idProduct = idProduct,
            color = color,
            size = size,
        )
        checkExist(favorite)
    }

    fun removeFavoriteFirebase(favorite: Favorite) {
        db.collection(USER_FIREBASE)
            .document(userManager.getAccessToken())
            .collection(FAVORITE_FIREBASE)
            .document(favorite.id).delete()
            .addOnSuccessListener {
                fetchFavoriteAndProduct()
            }
    }

    fun getListIdProductFavorite() {
        if (userManager.isLogged()) {
            db.collection(USER_FIREBASE)
                .document(userManager.getAccessToken())
                .collection(FAVORITE_FIREBASE)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.size() > 0) {
                        val list = mutableSetOf<String>()
                        for (document in documents) {
                            val favorite = document.toObject<Favorite>()
                            list.add(favorite.idProduct)
                        }
                        listIdProductFavorite.postValue(list.toList())
                    }
                }
        }
    }

    private fun updateFavoriteFirebase(favorite: Favorite) {
        db.collection(USER_FIREBASE)
            .document(userManager.getAccessToken())
            .collection(FAVORITE_FIREBASE)
            .document(favorite.id)
            .set(favorite)
            .addOnSuccessListener {
                fetchFavoriteAndProduct()
            }
    }


    private fun checkExist(favorite: Favorite) {
        db.collection(USER_FIREBASE).document(userManager.getAccessToken())
            .collection(FAVORITE_FIREBASE)
            .whereEqualTo(BaseViewModel.SIZE, favorite.size)
            .whereEqualTo(BaseViewModel.COLOR, favorite.color)
            .whereEqualTo(BaseViewModel.ID_PRODUCT, favorite.idProduct)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.size() == 0) {
                    updateFavoriteFirebase(favorite)
                }
            }
    }

    fun setButtonFavorite(context: Context, buttonView: View, idProduct: String) {
        if (!userManager.isLogged()) {
            buttonView.visibility = View.GONE
        } else {
            buttonView.visibility = View.VISIBLE
            listIdProductFavorite.value?.let {
                if (it.contains(idProduct)) {
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
}