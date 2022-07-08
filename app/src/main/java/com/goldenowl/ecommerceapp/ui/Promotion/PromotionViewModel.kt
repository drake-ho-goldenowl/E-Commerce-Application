package com.goldenowl.ecommerceapp.ui.Promotion

import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.data.Promotion
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import com.goldenowl.ecommerceapp.utilities.PROMOTION_FIREBASE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PromotionViewModel @Inject constructor(
    private val db: FirebaseFirestore,
) : BaseViewModel() {
    val promotions = MutableLiveData<List<Promotion>>()
    val promotion = MutableLiveData<Promotion>()

    init {
        fetchData()
    }

    fun fetchData() {
        db.collection(PROMOTION_FIREBASE).get(source).addOnSuccessListener { documents ->
            val list = mutableListOf<Promotion>()
            for (document in documents) {
                list.add(document.toObject())
            }
            promotions.postValue(list)
        }
    }

    fun getPromotion(id: String) {
        if (id.isBlank()) {
            return
        }
        db.collection(PROMOTION_FIREBASE).document(id).get(source)
            .addOnSuccessListener { document ->
                if (document.exists() && document != null) {
                    promotion.postValue(document.toObject())
                }
            }
    }
}