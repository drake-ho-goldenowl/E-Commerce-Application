package com.goldenowl.ecommerceapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.Review
import com.goldenowl.ecommerceapp.data.User
import com.goldenowl.ecommerceapp.utilities.REVIEW_FIREBASE
import com.goldenowl.ecommerceapp.utilities.USER_FIREBASE
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewRatingViewModel @Inject constructor() : ViewModel() {
    val db = Firebase.firestore
    val listReview: MutableLiveData<List<Review>> = MutableLiveData()
    val alertStar: MutableLiveData<Boolean> = MutableLiveData(false)
    val alertDescription: MutableLiveData<Boolean> = MutableLiveData(false)
    val dismiss: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getData(idProduct: String) {
        db.collection(REVIEW_FIREBASE).whereEqualTo("idProduct", idProduct).get()
            .addOnSuccessListener { documents ->
                val list: MutableList<Review> = mutableListOf()
                for (document in documents) {
                    list.add(document.toObject())
                }
                listReview.postValue(list)
            }
    }

    fun getDataLive(idProduct: String) {
        viewModelScope.launch {
            db.collection(REVIEW_FIREBASE).whereEqualTo("idProduct", idProduct)
                .addSnapshotListener { value, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    val list: MutableList<Review> = mutableListOf()
                    for (doc in value!!) {
                        list.add(doc.toObject())
                    }
                    listReview.postValue(list)
                }
        }
    }

    private fun setReviewOnFirebase(review: Review) {
        db.collection(REVIEW_FIREBASE).document(review.createdTimer?.seconds.toString()).set(review)
    }

    fun getNameAndAvatarUser(idUser: String): LiveData<Pair<String, String>> {
        val result = MutableLiveData<Pair<String, String>>()
        db.collection(USER_FIREBASE).document(idUser).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<User>()
                user?.let {
                    result.postValue(Pair(user.name, user.avatar))
                }
            }
        return result
    }

    fun updateHelpful(review: Review, idUser: String?) {
        if (idUser == null){
            return
        }
        db.collection(REVIEW_FIREBASE).document(review.createdTimer?.seconds.toString()).get()
            .addOnSuccessListener { documentSnapshot ->
                val reviewTemp = documentSnapshot.toObject<Review>()
                val newList = reviewTemp?.helpful?.toMutableList()
                newList?.let {
                    if (newList.contains(idUser)) {
                        newList.remove(idUser)
                    } else {
                        newList.add(idUser)
                    }
                    db.collection(REVIEW_FIREBASE).document(review.createdTimer?.seconds.toString())
                        .update("helpful", newList)
                }
            }
    }

    fun checkHelpfulForUser(review: Review, idUser: String?): LiveData<Boolean> {
        val result = MutableLiveData(false)
        if (idUser == null){
            return result
        }
        db.collection(REVIEW_FIREBASE).document(review.createdTimer?.seconds.toString()).get()
            .addOnSuccessListener { documentSnapshot ->
                val reviewTemp = documentSnapshot.toObject<Review>()
                reviewTemp?.let {
                    if (it.helpful.contains(idUser)) {
                        result.postValue(true)
                    }
                }
            }
        return result
    }

    private fun createReview(
        idUser: String,
        idProduct: String,
        description: String,
        star: Long,
        listImage: List<String>
    ): Review {
        return Review(
            idUser = idUser,
            idProduct = idProduct,
            description = description,
            star = star,
            createdTimer = Timestamp.now(),
            helpful = emptyList(),
            listImage = listImage

        )
    }

    fun insertReview(
        idUser: String,
        idProduct: String,
        description: String,
        star: Long,
        listImage: List<String>
    ) {
        if (star < 1) {
            alertStar.postValue(true)
        } else if (description.isBlank()) {
            alertDescription.postValue(true)
        } else {
            val review = createReview(idUser, idProduct, description, star, listImage)
            setReviewOnFirebase(review)
            dismiss.postValue(true)
        }
    }

    companion object {
        const val TAG = "REVIEW_RATING_VIEW_MODEL"
    }
}