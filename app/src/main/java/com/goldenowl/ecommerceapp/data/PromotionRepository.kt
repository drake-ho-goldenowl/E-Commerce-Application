package com.goldenowl.ecommerceapp.data

import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.utilities.PROMOTION_FIREBASE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromotionRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    val promotions = MutableLiveData<List<Promotion>>()
    val promotion = MutableLiveData<Promotion>()

    fun fetchData() {
        db.collection(PROMOTION_FIREBASE).get().addOnSuccessListener { documents ->
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
        db.collection(PROMOTION_FIREBASE).document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists() && document != null) {
                    promotion.postValue(document.toObject())
                }
            }
    }
}