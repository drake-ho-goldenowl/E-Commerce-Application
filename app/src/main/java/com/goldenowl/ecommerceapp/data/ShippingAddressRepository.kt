package com.goldenowl.ecommerceapp.data

import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.utilities.ADDRESS_USER
import com.goldenowl.ecommerceapp.utilities.USER_FIREBASE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShippingAddressRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val userManager: UserManager
) {
    val listAddress = MutableLiveData<MutableList<ShippingAddress>>()
    val address = MutableLiveData<ShippingAddress>()
    fun fetchAddress() {
        if (userManager.isLogged()) {
            db.collection(USER_FIREBASE)
                .document(userManager.getAccessToken())
                .collection(ADDRESS_USER)
                .get()
                .addOnSuccessListener { result ->
                    val list = mutableListOf<ShippingAddress>()
                    for (document in result) {
                        list.add(document.toObject())
                    }
                    listAddress.postValue(list)
                }
        }
    }

    fun getAddress(idAddress: String) {
        db.collection(USER_FIREBASE)
            .document(userManager.getAccessToken())
            .collection(ADDRESS_USER)
            .document(idAddress)
            .get()
            .addOnSuccessListener { result ->
                if (result.exists() && result != null) {
                    address.postValue(result.toObject())
                } else {
                    address.postValue(ShippingAddress())
                }
            }
    }

    fun setAddressOnFirebase(address: ShippingAddress) {
        db.collection(USER_FIREBASE)
            .document(userManager.getAccessToken())
            .collection(ADDRESS_USER)
            .document(address.id.toString())
            .set(address)
            .addOnSuccessListener {
                fetchAddress()
            }
    }

    fun deleteAddressOnFirebase(address: ShippingAddress) {
        db.collection(USER_FIREBASE)
            .document(userManager.getAccessToken())
            .collection(ADDRESS_USER)
            .document(address.id.toString())
            .delete()
            .addOnSuccessListener {
                fetchAddress()
            }
    }
}