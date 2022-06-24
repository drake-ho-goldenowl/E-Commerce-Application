package com.goldenowl.ecommerceapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.ShippingAddress
import com.goldenowl.ecommerceapp.data.ShippingAddressRepository
import com.goldenowl.ecommerceapp.data.UserManager
import com.goldenowl.ecommerceapp.utilities.ADDRESS_USER
import com.goldenowl.ecommerceapp.utilities.LAST_EDIT
import com.goldenowl.ecommerceapp.utilities.USER_FIREBASE
import com.goldenowl.ecommerceapp.utilities.VALUE_LAST_EDIT
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ShippingAddressViewModel @Inject constructor(
    private val shippingAddressRepository: ShippingAddressRepository,
    private val userManager: UserManager
) : BaseViewModel() {
    private val db = Firebase.firestore
    val listAll = shippingAddressRepository.getAll().asLiveData()
    val alertFullName: MutableLiveData<Boolean> = MutableLiveData(false)
    val alertAddress: MutableLiveData<Boolean> = MutableLiveData(false)
    val alertCity: MutableLiveData<Boolean> = MutableLiveData(false)
    val alertSate: MutableLiveData<Boolean> = MutableLiveData(false)
    val alertZipCode: MutableLiveData<Boolean> = MutableLiveData(false)
    val alertCountry: MutableLiveData<Boolean> = MutableLiveData(false)
    val dismiss: MutableLiveData<Boolean> = MutableLiveData(false)


    private fun setAddressOnFirebase(address: ShippingAddress) {
        db.collection(USER_FIREBASE).document(userManager.getAccessToken()).collection(ADDRESS_USER)
            .document(address.id.toString()).set(address)
        db.collection(USER_FIREBASE).document(userManager.getAccessToken()).collection(ADDRESS_USER)
            .document(LAST_EDIT).set(mapOf(VALUE_LAST_EDIT to Date().time))
    }


    fun setDefaultAddress(idAddress: String) {
        userManager.setAddress(idAddress)
        userManager.writeProfile(db, userManager.getUser())
    }

    fun removeDefaultAddress() {
        userManager.setAddress("")
        userManager.writeProfile(db, userManager.getUser())
    }

    private fun createShippingAddress(
        fullName: String,
        address: String,
        city: String,
        state: String,
        zipCode: String,
        country: String,
    ): ShippingAddress {
        return ShippingAddress(
            id = Date().time,
            fullName = fullName,
            address = address,
            city = city,
            state = state,
            zipCode = zipCode,
            country = country
        )
    }

    fun insertShippingAddress(
        fullName: String,
        address: String,
        city: String,
        state: String,
        zipCode: String,
        country: String,
    ) {
        if (checkFullName(fullName) &&
            checkAddress(address) &&
            checkCity(city) &&
            checkState(state) &&
            checkZipCode(zipCode) &&
            checkCountry(country)
        ) {
            viewModelScope.launch {
                val shippingAddress =
                    createShippingAddress(fullName, address, city, state, zipCode, country)
                shippingAddressRepository.insert(shippingAddress)
                setAddressOnFirebase(shippingAddress)
                toastMessage.postValue(SUCCESS)
                dismiss.postValue(true)
            }
        }
    }

    fun checkFullName(fullName: String): Boolean {
        if (fullName.length < 2) {
            alertFullName.postValue(true)
            return false
        }
        alertFullName.postValue(false)
        return true
    }

    fun checkAddress(address: String): Boolean {
        if (address.length < 6) {
            alertAddress.postValue(true)
            return false
        }
        alertAddress.postValue(false)
        return true
    }

    fun checkCity(city: String): Boolean {
        if (city.length < 3) {
            alertCity.postValue(true)
            return false
        }
        alertCity.postValue(false)
        return true
    }

    fun checkState(state: String): Boolean {
        if (state.length < 6) {
            alertSate.postValue(true)
            return false
        }
        alertSate.postValue(false)
        return true
    }

    fun checkZipCode(zipCode: String): Boolean {
        if (zipCode.length < 5) {
            alertZipCode.postValue(true)
            return false
        }
        alertZipCode.postValue(false)
        return true
    }

    private fun checkCountry(country: String): Boolean {
        if (country.isEmpty()) {
            alertCountry.postValue(true)
            return false
        }
        alertCountry.postValue(false)
        return true
    }

    fun checkDefaultShippingAddress(idAddress: String): Boolean {
        return userManager.getAddress() == idAddress
    }

    companion object {
        const val SUCCESS = "Add success"
    }
}