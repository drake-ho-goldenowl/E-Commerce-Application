package com.goldenowl.ecommerceapp.data

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import com.goldenowl.ecommerceapp.utilities.BAG_FIREBASE
import com.goldenowl.ecommerceapp.utilities.PRODUCT_FIREBASE
import com.goldenowl.ecommerceapp.utilities.USER_FIREBASE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt


@Singleton
class BagRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val userManager: UserManager,
) {
    val bagAndProduct = MutableLiveData<MutableList<BagAndProduct>>()
    private val bags = MutableLiveData<List<Bag>>()
    fun fetchBagAndProduct() {
        if (userManager.isLogged()) {
            db.collection(USER_FIREBASE)
                .document(userManager.getAccessToken())
                .collection(BAG_FIREBASE)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.size() == 0) {
                        bagAndProduct.postValue(mutableListOf())
                    } else {
                        val list = mutableListOf<BagAndProduct>()
                        val listBag = mutableListOf<Bag>()
                        for (document in documents) {
                            val bag = document.toObject<Bag>()
                            db.collection(PRODUCT_FIREBASE).document(bag.idProduct).get()
                                .addOnSuccessListener { document2 ->
                                    document2.toObject<Product>()?.let {
                                        list.add(BagAndProduct(bag, it))
                                    }
                                    bagAndProduct.postValue(list)

                                }
                            listBag.add(bag)
                        }
                        bags.postValue(listBag)
                    }
                }
        }
    }

    fun getBags() {
        if (userManager.isLogged()) {
            db.collection(USER_FIREBASE)
                .document(userManager.getAccessToken())
                .collection(BAG_FIREBASE)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.size() > 0) {
                        val list = mutableListOf<Bag>()
                        for (document in documents) {
                            list.add(document.toObject())
                        }
                        bags.postValue(list)
                    } else {
                        bags.postValue(mutableListOf())
                    }
                }
        }
    }

    fun insertBag(idProduct: String, color: String, size: String, quantity: Long = 1) {
        val bag = Bag(
            id = Date().time.toString(),
            idProduct = idProduct,
            color = color,
            size = size,
            quantity = quantity,
        )
        checkExist(bag)
    }

    fun removeBagFirebase(bag: Bag) {
        db.collection(USER_FIREBASE)
            .document(userManager.getAccessToken())
            .collection(BAG_FIREBASE)
            .document(bag.id)
            .delete()
            .addOnSuccessListener {
                fetchBagAndProduct()
            }

    }

    fun removeAllFirebase() {
        db.collection(USER_FIREBASE)
            .document(userManager.getAccessToken())
            .collection(BAG_FIREBASE)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            bagAndProduct.postValue(mutableListOf())
                        }

                }
            }
    }

    fun updateBagFirebase(bag: Bag, isFetch: Boolean = true) {
        db.collection(USER_FIREBASE)
            .document(userManager.getAccessToken())
            .collection(BAG_FIREBASE)
            .document(bag.id)
            .set(bag)
            .addOnSuccessListener {
                if (isFetch) {
                    fetchBagAndProduct()
                }
            }
    }


    private fun checkExist(bag: Bag) {
        db.collection(USER_FIREBASE).document(userManager.getAccessToken())
            .collection(BAG_FIREBASE)
            .whereEqualTo(BaseViewModel.SIZE, bag.size)
            .whereEqualTo(BaseViewModel.COLOR, bag.color)
            .whereEqualTo(BaseViewModel.ID_PRODUCT, bag.idProduct)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    for (document in documents) {
                        plusQuantity(document.toObject())
                    }
                } else {
                    updateBagFirebase(bag)
                }
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


    private fun plusQuantity(bagAndProduct: BagAndProduct) {
        val newBag = bagAndProduct.bag
        newBag.quantity += 1
        updateBagFirebase(newBag)
    }

    fun setButtonBag(context: Context, buttonView: View, favorite: Favorite) {
        bags.value?.let {
            for (bag in it) {
                if (bag.size == favorite.size &&
                    bag.color == favorite.color &&
                    bag.idProduct == favorite.idProduct
                ) {
                    buttonView.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.btn_bag_active
                    )
                    break
                } else {
                    buttonView.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.btn_bag_no_active
                    )
                }
            }
        }
    }
}