package com.goldenowl.ecommerceapp.data

import android.util.Log
import com.goldenowl.ecommerceapp.utilities.BAG_FIREBASE
import com.goldenowl.ecommerceapp.utilities.LAST_EDIT_TIME_BAG
import com.goldenowl.ecommerceapp.viewmodels.FavoriteViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class BagRepository @Inject constructor(
    private val bagDao: BagDao
) {

    suspend fun insert(bag: Bag) = bagDao.insert(bag)

    suspend fun update(bag: Bag) = bagDao.update(bag)

    suspend fun delete(bag: Bag) = bagDao.delete(bag)

    suspend fun deleteAll() = bagDao.deleteAll()

    fun getAll() = bagDao.getAll()

    fun getAllBagAndProduct() = bagDao.getAllBagAndProduct()

    suspend fun updateQuantity(idProduct: String, color: String, size: String, quantity: Long) =
        bagDao.updateQuantity(idProduct, color, size, quantity)

    private suspend fun getBag(idProduct: String, color: String, size: String) =
        bagDao.getBag(idProduct, color, size)

    fun filterBySearch(search: String) = bagDao.filterBySearch(search)

    private fun createBag(idProduct: String, color: String, size: String): Bag {
        return Bag(
            size = size,
            color = color,
            idProduct = idProduct,
            quantity = 1
        )
    }

    suspend fun insertBag(idProduct: String, color: String, size: String) {
        val bagTemp = getBag(idProduct, color, size)
        if (bagTemp == null){
            val bag = createBag(idProduct, color, size)
            bagDao.insert(bag)
        }
        else{
            bagDao.updateQuantity(idProduct,color,size,bagTemp.quantity+ 1)
        }
    }


    suspend fun updateBagFirebase(db: FirebaseFirestore, uid: String) {
        val bags = bagDao.getAllList()
        val docData: MutableMap<String, Any> = HashMap()
        LAST_EDIT_TIME_BAG = Date()
        docData[FavoriteViewModel.LAST_EDIT] = LAST_EDIT_TIME_BAG!!
        docData[FavoriteViewModel.DATA] = bags
        db.collection(BAG_FIREBASE).document(uid)
            .set(docData)
            .addOnSuccessListener {
                Log.d(UserManager.TAG, "DocumentSnapshot added")
            }
            .addOnFailureListener { e ->
                Log.w(UserManager.TAG, "Error adding document", e)
            }
    }

}