package com.goldenowl.ecommerceapp.ui.Bag

import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BagViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val promotionRepository: PromotionRepository,
    private val userManager: UserManager,
    private val bagRepository: BagRepository,
) :
    BaseViewModel() {
    val bagAndProduct = bagRepository.bagAndProduct
    val totalPrice = MutableLiveData(0)
    val promotion = promotionRepository.promotion
    val isRemoveButton = MutableLiveData(false)

    fun fetchBag() {
         bagRepository.fetchBagAndProduct()
    }

    fun insertBag(idProduct: String, color: String, size: String) {
        bagRepository.insertBag(idProduct, color, size)
        dismiss.postValue(true)
    }

    fun removeBagFirebase(bag: Bag) {
        isLoading.postValue(true)
        bagRepository.removeBagFirebase(bag)
    }

    private fun updateBagFirebase(bag: Bag, isFetch: Boolean = true) {
        bagRepository.updateBagFirebase(bag, isFetch)
    }

    private fun changeQuantityAndCalculator(old: BagAndProduct, new: Bag, isPlus: Boolean = true) {
        val size = old.product.getColorAndSize(
            new.color,
            new.size
        )
        if (size != null) {
            var salePercent = 0
            if (old.product.salePercent != null) {
                salePercent = old.product.salePercent
            }
            val salePromotion = promotion.value?.salePercent ?: 0
            var price = size.price * (100 - salePercent) / 100
            price -= (price * salePromotion / 100)
            if (isPlus) {
                val total = totalPrice.value?.plus(price) ?: 0
                totalPrice.postValue(total.toInt())
            } else {
                val total = totalPrice.value?.minus(price) ?: 0
                totalPrice.postValue(total.toInt())
            }
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
            changeQuantityAndCalculator(bagAndProduct, newBag, false)
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

    fun getPromotion(id: String){
        if (id.isNotBlank()){
            promotionRepository.getPromotion(id)
        }
        else{
            promotionRepository.promotion.postValue(Promotion())
        }
    }
}
