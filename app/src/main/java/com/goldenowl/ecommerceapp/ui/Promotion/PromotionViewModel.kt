package com.goldenowl.ecommerceapp.ui.Promotion

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.Promotion
import com.goldenowl.ecommerceapp.data.PromotionRepository
import com.goldenowl.ecommerceapp.ui.Shop.ShopViewModel
import com.goldenowl.ecommerceapp.utilities.PROMOTION_FIREBASE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromotionViewModel @Inject constructor(
    private val promotionRepository: PromotionRepository,
    private val db: FirebaseFirestore,
) : ViewModel() {
    val promotions: LiveData<List<Promotion>> = promotionRepository.getAll().asLiveData()

    val statusPromotion = MutableStateFlow("")
    val promotion: LiveData<Promotion> = statusPromotion.flatMapLatest {
        promotionRepository.getPromotion(it)
    }.asLiveData()

    fun fetchData() {
        db.collection(PROMOTION_FIREBASE).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(ShopViewModel.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            value?.let {
                for (doc in value) {
                    viewModelScope.launch {
                        val promotion = doc.toObject<Promotion>()
                        promotionRepository.insert(promotion)
                    }
                }
            }
        }
    }

    fun setStatusPromotion(string: String) {
        statusPromotion.value = string
    }
}