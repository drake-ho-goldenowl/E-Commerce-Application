package com.goldenowl.ecommerceapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.Promotion
import com.goldenowl.ecommerceapp.data.PromotionRepository
import com.goldenowl.ecommerceapp.utilities.PROMOTION_FIREBASE
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromotionViewModel @Inject constructor(
    private val promotionRepository: PromotionRepository
) : ViewModel() {
    private val db = Firebase.firestore
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