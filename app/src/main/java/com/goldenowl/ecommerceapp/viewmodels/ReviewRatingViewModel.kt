package com.goldenowl.ecommerceapp.viewmodels

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.utilities.REVIEW_FIREBASE
import com.goldenowl.ecommerceapp.utilities.USER_FIREBASE
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class ReviewRatingViewModel @Inject constructor(
    private val ratingProductRepository: RatingProductRepository,
    val userManager: UserManager
) :
    BaseViewModel() {
    val db = Firebase.firestore
    val listReview: MutableLiveData<List<Review>> = MutableLiveData()
    val alertStar: MutableLiveData<Boolean> = MutableLiveData(false)
    val alertDescription: MutableLiveData<Boolean> = MutableLiveData(false)
    val dismiss: MutableLiveData<Boolean> = MutableLiveData(false)
    val statusRatingProduct = MutableStateFlow("")
    private val idUser = if (userManager.isLogged()) {
        userManager.getAccessToken()
    } else {
        null
    }
    private var storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private lateinit var allReview: List<Review>

    val ratingProduct: LiveData<RatingProduct> = statusRatingProduct.flatMapLatest {
        ratingProductRepository.getRatingProduct(it)
    }.asLiveData()

    fun fetchRatingProduct(idProduct: String) {
        db.collection(REVIEW_FIREBASE).whereEqualTo("idProduct", idProduct).get()
            .addOnSuccessListener { documents ->
                viewModelScope.launch {
                    ratingProductRepository.deleteAll()
                    val listRating: MutableList<Long> = mutableListOf(0, 0, 0, 0, 0)
                    for (document in documents) {
                        val review = document.toObject<Review>()
                        if (review.star in 1..5) {
                            listRating[review.star.toInt() - 1]++
                        }
                    }
                    ratingProductRepository.insert(RatingProduct(idProduct, listRating))
                }
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
                    allReview = list
                    listReview.postValue(allReview)
                }
        }
    }

    private fun setReviewOnFirebase(review: Review) {
        db.collection(REVIEW_FIREBASE).document(review.createdTimer?.seconds.toString()).set(review)
    }

    fun filterImage(isCheck: Boolean) {
        if (isCheck) {
            listReview.postValue(listReview.value?.filter {
                it.listImage.isNotEmpty()
            })
        } else {
            listReview.postValue(allReview)
        }
    }

    fun uploadImage(
        idReview: String,
        listImage: List<String>
    ): Pair<LiveData<List<String>>, LiveData<Boolean>> {
        val result = MutableLiveData<List<String>>()
        val isSuccess = MutableLiveData(true)
        val list: MutableList<String> = mutableListOf()
        viewModelScope.launch {
            for ((index, review) in listImage.withIndex()) {
                val ref =
                    storageReference.child("reviews/${idReview}/$index")
                val uploadTask = ref.putFile(File(review).toUri())
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        isSuccess.postValue(false)
                        task.exception?.let {
                            throw it
                        }
                    }
                    ref.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        list.add(downloadUri.toString())
                        result.postValue(list)
                    }
                }
                    .addOnFailureListener {
                        isSuccess.postValue(false)
                    }
            }
        }
        return Pair(result, isSuccess)
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

    fun removeHelpful(review: Review) {
        if (idUser == null) {
            return
        }
        db.collection(REVIEW_FIREBASE).document(review.createdTimer?.seconds.toString())
            .collection(HELPFUL).document(idUser).delete()
    }

    fun addHelpful(review: Review) {
        if (idUser == null) {
            return
        }
        val data = hashMapOf("idUser" to idUser)
        db.collection(REVIEW_FIREBASE).document(review.createdTimer?.seconds.toString())
            .collection(HELPFUL).document(idUser).set(data)
    }


    fun checkHelpfulForUser(review: Review): LiveData<Boolean> {
        val result = MutableLiveData(false)
        if (idUser == null) {
            return result
        }
        db.collection(REVIEW_FIREBASE).document(review.createdTimer?.seconds.toString())
            .collection(HELPFUL).document(idUser).get().addOnSuccessListener {
                if (it.data != null) {
                    result.postValue(true)
                }
            }
        return result
    }

    fun createReview(
        idProduct: String,
        description: String,
        star: Long,
        listImage: List<String>
    ): Review? {
        if (star < 1) {
            alertStar.postValue(true)
        } else if (description.isBlank()) {
            alertDescription.postValue(true)
        } else {
            return idUser?.let {
                Review(
                    idUser = it,
                    idProduct = idProduct,
                    description = description,
                    star = star,
                    createdTimer = Timestamp.now(),
                    listImage = listImage
                )
            }
        }
        return null
    }

    fun insertReview(review: Review) {
        viewModelScope.launch {
            setReviewOnFirebase(review)
        }
    }

    companion object {
        const val TAG = "REVIEW_RATING_VIEW_MODEL"
        const val HELPFUL = "helpful"
    }
}