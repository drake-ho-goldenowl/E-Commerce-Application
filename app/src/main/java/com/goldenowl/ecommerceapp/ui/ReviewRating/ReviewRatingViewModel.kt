package com.goldenowl.ecommerceapp.ui.ReviewRating

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import com.goldenowl.ecommerceapp.utilities.PRODUCT_FIREBASE
import com.goldenowl.ecommerceapp.utilities.REVIEW_FIREBASE
import com.goldenowl.ecommerceapp.utilities.USER_FIREBASE
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class ReviewRatingViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val userManager: UserManager,
    private val db: FirebaseFirestore
) :
    BaseViewModel() {
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    val listReview = MutableLiveData<List<Review>>(emptyList())
    val listRating = MutableLiveData<List<Int>>(emptyList())
    val alertStar = MutableLiveData(false)
    val alertDescription = MutableLiveData(false)
    val statusFilterImage = MutableLiveData(false)
    private val idUser = if (userManager.isLogged()) {
        userManager.getAccessToken()
    } else {
        null
    }

    private val statusIdProduct = MutableStateFlow("")
    val product = statusIdProduct.flatMapLatest {
        getProduct(it)
    }.asLiveData()


    fun isLogged(): Boolean {
        return userManager.isLogged()
    }

    private fun fetchRatingProduct(idProduct: String) {
        db.collection(REVIEW_FIREBASE)
            .whereEqualTo(ID_PRODUCT, idProduct)
            .get()
            .addOnSuccessListener { documents ->
                val list: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0)
                for (document in documents) {
                    val review = document.toObject<Review>()
                    if (review.star in 1..5) {
                        list[review.star.toInt() - 1]++
                    }
                }

                listRating.postValue(list)
                db.collection(PRODUCT_FIREBASE)
                    .document(idProduct)
                    .get()
                    .addOnSuccessListener { document ->
                        val productNew = document.toObject<Product>()
                        productNew?.let {
                            productNew.numberReviews = productNew.getTotalRating(list)
                            productNew.reviewStars = productNew.getAverageRating(list)
                            db.collection(PRODUCT_FIREBASE)
                                .document(idProduct)
                                .set(productNew)
                        }
                    }
            }
    }

    fun getRatingProduct(idProduct: String) {
        db.collection(REVIEW_FIREBASE)
            .whereEqualTo(ID_PRODUCT, idProduct)
            .get()
            .addOnSuccessListener { documents ->
                val list: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0)
                for (document in documents) {
                    val review = document.toObject<Review>()
                    if (review.star in 1..5) {
                        list[review.star.toInt() - 1]++
                    }
                }
                listRating.postValue(list)
            }
    }

    fun setIdProduct(idProduct: String) {
        statusIdProduct.value = idProduct
    }

    private fun getProduct(idProduct: String): Flow<Product> {
        return productRepository.getProduct(idProduct)
    }

    fun getReview(idProduct: String) {
        db.collection(REVIEW_FIREBASE)
            .whereEqualTo(ID_PRODUCT, idProduct)
            .get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Review>()
                for (doc in documents) {
                    list.add(doc.toObject())
                }
                listReview.postValue(list)
            }
    }

    private fun setReviewOnFirebase(review: Review) {
        db.collection(REVIEW_FIREBASE)
            .document(review.createdTimer?.seconds.toString())
            .set(review)
            .addOnSuccessListener {
                toastMessage.postValue(BottomAddReview.SUCCESS)
                dismiss.postValue(true)
                isLoading.postValue(false)
                getReview(review.idProduct)
            }
            .addOnFailureListener {
                toastMessage.postValue(BottomAddReview.SUCCESS)
                dismiss.postValue(true)
                isLoading.postValue(false)
            }
    }

    fun filterImage(isCheck: Boolean): List<Review>? {
        return if (isCheck) {
            listReview.value?.filter {
                it.listImage.isNotEmpty()
            }
        } else {
            listReview.value
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
        db.collection(USER_FIREBASE)
            .document(idUser)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<User>()
                user?.let {
                    result.postValue(Pair(user.name, user.avatar))
                }
            }
        return result
    }

    fun removeHelpful(review: Review) {
        if (idUser != null) {
            db.collection(REVIEW_FIREBASE)
                .document(review.createdTimer?.seconds.toString())
                .collection(HELPFUL)
                .document(idUser)
                .delete()
        }
    }

    fun addHelpful(review: Review) {
        if (idUser != null) {
            val data = hashMapOf(ID_USER to idUser)
            db.collection(REVIEW_FIREBASE)
                .document(review.createdTimer?.seconds.toString())
                .collection(HELPFUL)
                .document(idUser)
                .set(data)
        }

    }


    fun checkHelpfulForUser(review: Review): LiveData<Boolean> {
        val result = MutableLiveData(false)
        if (idUser != null) {
            db.collection(REVIEW_FIREBASE)
                .document(review.createdTimer?.seconds.toString())
                .collection(HELPFUL)
                .document(idUser)
                .get()
                .addOnSuccessListener {
                    if (it.data != null) {
                        result.postValue(true)
                    }
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
        setReviewOnFirebase(review)
        fetchRatingProduct(review.idProduct)
    }

    fun setColorHelpful(
        context: Context,
        isHelpful: Boolean,
        txtHelpful: TextView,
        icLike: ImageView
    ) {
        if (isHelpful) {
            txtHelpful.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                )
            )
            icLike.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_like2)
            )
        } else {
            txtHelpful.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.black
                )
            )
            icLike.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_like)
            )
        }
    }


    val reviews = MutableLiveData<List<Review>>(emptyList())

    fun getAllReviewOfUser() {
        if (userManager.isLogged()) {
            db.collection(REVIEW_FIREBASE)
                .whereEqualTo(ID_USER, userManager.getAccessToken())
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
                .whereEqualTo(ID_USER, userManager.getAccessToken())
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

    companion object {
        const val TAG = "REVIEW_RATING_VIEW_MODEL"
        const val HELPFUL = "helpful"
        const val ID_PRODUCT = "idProduct"
    }
}
enum class FilterReview(val value: String){
    DATE("createdTimer"),
    STAR("star")
}