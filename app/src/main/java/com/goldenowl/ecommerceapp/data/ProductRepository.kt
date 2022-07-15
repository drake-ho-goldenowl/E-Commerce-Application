package com.goldenowl.ecommerceapp.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.goldenowl.ecommerceapp.utilities.PRODUCT_FIREBASE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    val products = MutableLiveData<List<Product>>()

    fun fetchProduct() {
        db.collection(PRODUCT_FIREBASE)
            .get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Product>()
                for (doc in documents) {
                    list.add(doc.toObject())
                }
                products.postValue(list)
            }
    }

    fun getProduct(idProduct: String): Flow<Product> {
        val result = MutableLiveData<Product>()
        if (idProduct.isNotBlank()) {
            db.collection(PRODUCT_FIREBASE)
                .document(idProduct)
                .get()
                .addOnSuccessListener { document ->
                    result.postValue(document.toObject())
                }
        }

        return result.asFlow()
    }

    fun getAllCategory(): Flow<List<String>> {
        val result = MutableLiveData<List<String>>()
        db.collection(PRODUCT_FIREBASE)
            .get()
            .addOnSuccessListener { documents ->
                val list = mutableSetOf<String>()
                for (doc in documents) {
                    val product = doc.toObject<Product>()
                    list.add(product.categoryName)
                }
                result.postValue(list.toList())
            }
        return result.asFlow()
    }
}