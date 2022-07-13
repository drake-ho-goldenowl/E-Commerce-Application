package com.goldenowl.ecommerceapp.ui.ShippingAddress

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.ShippingAddress
import com.goldenowl.ecommerceapp.data.ShippingAddressRepository
import com.goldenowl.ecommerceapp.data.UserManager
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ShippingAddressViewModel @Inject constructor(
    private val shippingAddressRepository: ShippingAddressRepository,
    private val userManager: UserManager,
    private val db: FirebaseFirestore
) : BaseViewModel() {
    val listAddress = shippingAddressRepository.listAddress
    val address = shippingAddressRepository.address

    val alertFullName = MutableLiveData(false)
    val alertAddress = MutableLiveData(false)
    val alertCity = MutableLiveData(false)
    val alertSate = MutableLiveData(false)
    val alertZipCode = MutableLiveData(false)
    val alertCountry = MutableLiveData(false)


    fun getAddress(id: String) {
        shippingAddressRepository.getAddress(id)
    }

    fun fetchAddress() {
        shippingAddressRepository.fetchAddress()
    }

    private fun setAddressOnFirebase(address: ShippingAddress) {
        shippingAddressRepository.setAddressOnFirebase(address)
    }

    private fun deleteAddressOnFirebase(address: ShippingAddress) {
        shippingAddressRepository.deleteAddressOnFirebase(address)
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
            val shippingAddress =
                createShippingAddress(fullName, address, city, state, zipCode, country)
            setAddressOnFirebase(shippingAddress)
            toastMessage.postValue(SUCCESS)
            dismiss.postValue(true)
        }
    }

    fun updateShippingAddress(
        shippingAddress: ShippingAddress,
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
                shippingAddress.apply {
                    this.fullName = fullName
                    this.address = address
                    this.city = city
                    this.state = state
                    this.zipCode = zipCode
                    this.country = country
                }
                setAddressOnFirebase(shippingAddress)
                toastMessage.postValue(SUCCESS_EDIT)
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

    fun deleteShippingAddress(shippingAddress: ShippingAddress) {
        if (checkDefaultShippingAddress(shippingAddress.id.toString())) {
            removeDefaultAddress()
        }
        deleteAddressOnFirebase(shippingAddress)
    }

    fun setAddressLiveData() {
        shippingAddressRepository.address.postValue(ShippingAddress())
    }

    companion object {
        const val SUCCESS = "Add success"
        const val SUCCESS_EDIT = "Edit success"
    }
}