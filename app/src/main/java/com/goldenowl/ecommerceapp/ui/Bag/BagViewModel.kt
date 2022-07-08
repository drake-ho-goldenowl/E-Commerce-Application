package com.goldenowl.ecommerceapp.ui.Bag

import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BagViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val userManager: UserManager,
    private val bagRepository: BagRepository,
    private val db: FirebaseFirestore
) :
    BaseViewModel() {
    val bagAndProduct = bagRepository.bagAndProduct
    val totalPrice = MutableLiveData(0)
    val sale = MutableLiveData(0L)
    val disMiss = MutableLiveData(false)

    fun fetchBag() {
        bagRepository.fetchBagAndProduct()
    }

    fun insertBag(idProduct: String, color: String, size: String) {
        bagRepository.insertBag(idProduct, color, size)
        disMiss.postValue(true)
    }

    fun removeBagFirebase(bag: Bag) {
        bagRepository.removeBagFirebase(bag)
    }

    private fun updateBagFirebase(bag: Bag, isFetch: Boolean = true) {
        bagRepository.updateBagFirebase(bag,isFetch)
    }

    private fun changeQuantityAndCalculator(old: BagAndProduct, new: Bag) {
        val index = bagAndProduct.value?.indexOf(old) ?: -1
        if (index != -1) {
            var list = mutableListOf<BagAndProduct>()
            bagAndProduct.value?.let {
                it[index].bag = new
                list = it
            }
            calculatorTotal(list, sale.value ?: 0)
        }
    }

    fun plusQuantity(bagAndProduct: BagAndProduct, textView: TextView? = null) {
        val newBag = bagAndProduct.bag
        newBag.quantity += 1
        textView?.let {
            it.text = newBag.quantity.toString()
        }
        updateBagFirebase(newBag, false)
        changeQuantityAndCalculator(bagAndProduct, newBag)
    }

    fun minusQuantity(bagAndProduct: BagAndProduct, textView: TextView) {
        val newBag = bagAndProduct.bag
        if (newBag.quantity > 1) {
            newBag.quantity -= 1
            textView.text = newBag.quantity.toString()
            updateBagFirebase(newBag, false)
            changeQuantityAndCalculator(bagAndProduct, newBag)
        } else {
            removeBagFirebase(bagAndProduct.bag)
        }
    }

    fun insertFavorite(idProduct: String, size: String, color: String) {
        favoriteRepository.insertFavorite(idProduct, color, size)
    }

    fun calculatorTotal(lists: List<BagAndProduct>, salePercent: Long = 0) {
        totalPrice.postValue(bagRepository.calculatorTotal(lists, salePercent))
    }

    fun isLogged(): Boolean {
        return userManager.isLogged()
    }
}
