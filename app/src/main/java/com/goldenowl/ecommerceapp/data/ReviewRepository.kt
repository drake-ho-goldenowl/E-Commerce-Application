package com.goldenowl.ecommerceapp.data

import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import com.goldenowl.ecommerceapp.ui.ReviewRating.FilterReview
import com.goldenowl.ecommerceapp.utilities.REVIEW_FIREBASE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor(
    private val db : FirebaseFirestore,
    private val userManager: UserManager
) {
    val reviews = MutableLiveData<List<Review>>(emptyList())

    fun getAllReviewOfUser() {
        if (userManager.isLogged()) {
            db.collection(REVIEW_FIREBASE)
                .whereEqualTo(BaseViewModel.ID_USER, userManager.getAccessToken())
                .get()
                .addOnSuccessListener { documents ->
                    val list = mutableListOf<Review>()
                    for (document in documents) {
                        list.add(document.toObject())
                    }
                    reviews.postValue(list)
                }
        }
    }

    fun filterReview(filterReview: FilterReview, typeSort: TypeSort) {
        if (userManager.isLogged()) {
            db.collection(REVIEW_FIREBASE)
                .whereEqualTo(BaseViewModel.ID_USER, userManager.getAccessToken())
                .orderBy(filterReview.value,typeSort.value)
                .get()
                .addOnSuccessListener { documents ->
                    val list = mutableListOf<Review>()
                    for (document in documents) {
                        list.add(document.toObject())
                    }
                    reviews.postValue(list)
                }
        }
    }

}