package com.goldenowl.ecommerceapp.data

import android.util.Log
import com.goldenowl.ecommerceapp.utilities.BAG_FIREBASE
import com.goldenowl.ecommerceapp.utilities.LAST_EDIT_TIME_BAG
import com.goldenowl.ecommerceapp.viewmodels.FavoriteViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt


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

    suspend fun checkBagHaveFavorite(idProduct: String, color: String, size: String): Boolean {
        return getBag(idProduct, color, size) != null
    }

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
        if (bagTemp == null) {
            val bag = createBag(idProduct, color, size)
            bagDao.insert(bag)
        } else {
            bagDao.updateQuantity(idProduct, color, size, bagTemp.quantity + 1)
        }
    }


    suspend fun updateBagFirebase(db: FirebaseFirestore, uid: String) {
        val bags = bagDao.getAllList()
        val docData: MutableMap<String, Any> = HashMap()
        LAST_EDIT_TIME_BAG = Date()
        docData[FavoriteViewModel.LAST_EDIT] = LAST_EDIT_TIME_BAG ?: Date()
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

    fun calculatorTotal(lists: List<BagAndProduct>, sale: Long): Int {
        var total = 0.0
        for (bagAndProduct in lists) {
            val size = bagAndProduct.product.getColorAndSize(
                bagAndProduct.bag.color,
                bagAndProduct.bag.size
            )
            if (size != null) {
                var salePercent = 0
                if (bagAndProduct.product.salePercent != null) {
                    salePercent = bagAndProduct.product.salePercent
                }
                val price = size.price * (100 - salePercent) / 100
                total += (price * bagAndProduct.bag.quantity)
            }
        }
        total -= (sale * total / 100)
        return total.roundToInt()
    }

}