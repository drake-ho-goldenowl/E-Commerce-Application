package com.goldenowl.ecommerceapp.data

import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.ui.Promotion.FilterPromotion
import com.goldenowl.ecommerceapp.utilities.PROMOTION_FIREBASE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromotionRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    val promotions = MutableLiveData<List<Promotion>>()
    val promotion = MutableLiveData<Promotion>()

    fun fetchData() {
        db.collection(PROMOTION_FIREBASE)
            .get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Promotion>()
                for (document in documents) {
                    val promotion = document.toObject<Promotion>()
                    promotion.endDate?.let {
                        if (it.time - Date().time > 0) {
                            list.add(document.toObject())
                        }
                    }
                }
                promotions.postValue(list)
            }
    }

    fun filterPromotion(filterPromotion: FilterPromotion, typeSort: TypeSort) {
        db.collection(PROMOTION_FIREBASE)
            .orderBy(filterPromotion.value, typeSort.value)
            .get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Promotion>()
                for (document in documents) {
                    val promotion = document.toObject<Promotion>()
                    promotion.endDate?.let {
                        if (it.time - Date().time > 0) {
                            list.add(document.toObject())
                        }
                    }
                }
                promotions.postValue(list)
            }
    }

    fun getPromotion(id: String) {
        if (id.isBlank()) {
            promotion.postValue(Promotion())
        }
        db.collection(PROMOTION_FIREBASE).document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists() && document != null) {
                    promotion.postValue(document.toObject())
                }
            }
    }
}