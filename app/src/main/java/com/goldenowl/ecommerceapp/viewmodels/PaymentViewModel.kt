package com.goldenowl.ecommerceapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.data.Card
import com.goldenowl.ecommerceapp.data.UserManager
import com.goldenowl.ecommerceapp.utilities.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val userManager: UserManager,
    private val rsa: RSA
) : BaseViewModel() {
    private val db = Firebase.firestore
    val listCard: MutableLiveData<List<Card>> = MutableLiveData()
    val alertName: MutableLiveData<Boolean> = MutableLiveData(false)
    val alertNumberCard: MutableLiveData<Boolean> = MutableLiveData(false)
    val alertExpertDate: MutableLiveData<Boolean> = MutableLiveData(false)
    val alertCVV: MutableLiveData<Boolean> = MutableLiveData(false)
    val dismiss: MutableLiveData<Boolean> = MutableLiveData(false)

    private fun setPaymentOnFirebase(card: Card) {
        db.collection(USER_FIREBASE).document(userManager.getAccessToken()).collection(PAYMENT_USER)
            .document(card.id).set(card).addOnSuccessListener {
                toastMessage.postValue(SUCCESS)
            }
            .addOnFailureListener {
                toastMessage.postValue(FAIL)
            }
        db.collection(USER_FIREBASE).document(userManager.getAccessToken()).collection(PAYMENT_USER)
            .document(LAST_EDIT).set(mapOf(VALUE_LAST_EDIT to Date().time))
    }

    fun fetchData() {
        db.collection(USER_FIREBASE).document(userManager.getAccessToken()).collection(
            PAYMENT_USER
        ).get()
            .addOnSuccessListener { result ->
                viewModelScope.launch {
                    val list: MutableList<Card> = mutableListOf()
                    for (document in result) {
                        if (document.id != LAST_EDIT) {
                            val card = document.toObject<Card>()
                            card.name = rsa.decrypt(card.name)
                            card.number = rsa.decrypt(card.number)
                            card.cvv = rsa.decrypt(card.cvv)
                            card.expireDate = rsa.decrypt(card.expireDate)
                            list.add(card)
                        }
                    }
                    listCard.postValue(list)
                }
            }
    }

    private fun createCard(
        name: String,
        number: String,
        expertDate: String,
        cvv: String,
    ): Card {
        return Card(
            id = Date().time.toString(),
            name = rsa.encrypt(name),
            number = rsa.encrypt(number),
            expireDate = rsa.encrypt(expertDate),
            cvv = rsa.encrypt(cvv)
        )
    }

    fun insertCard(
        name: String,
        number: String,
        expertDate: String,
        cvv: String,
        default: Boolean = false
    ) {
        if (checkName(name) &&
            checkNumberCard(number) &&
            checkExpertDate(expertDate) &&
            checkCVV(cvv)
        ) {
            viewModelScope.launch {
                val card = createCard(name, number, expertDate, cvv)
                setPaymentOnFirebase(card)
                if (default) {
                    setDefaultPayment(card.id)
                }
                dismiss.postValue(true)
            }
        }
    }

    fun setDefaultPayment(idCard: String) {
        userManager.setPayment(idCard)
        userManager.writeProfile(db, userManager.getUser())
    }

    fun removeDefaultPayment(){
        userManager.setPayment("")
        userManager.writeProfile(db, userManager.getUser())
    }

    private fun checkName(name: String): Boolean {
        if (name.length <= 2) {
            alertName.postValue(true)
            return false
        }
        return true
    }

    private fun checkNumberCard(numberCard: String): Boolean {
        if (numberCard.length < 19) {
            alertNumberCard.postValue(true)
            return false
        }
        if (numberCard[0] != '4' && numberCard[0] != '5') {
            alertNumberCard.postValue(true)
            return false
        }
        return true
    }

    private fun checkExpertDate(expertDate: String): Boolean {
        if (expertDate.length < 5) {
            alertExpertDate.postValue(true)
            return false
        }
        val date = expertDate.split('/')
        if (date.size < 2) {
            alertExpertDate.postValue(true)
            return false
        }
        val month = date[0].toInt()
        val day = date[1].toInt()
        if ((month < 1 || month > 12) || (day < 1 || day > 31)) {
            alertExpertDate.postValue(true)
            return false
        }
        return true
    }

    private fun checkCVV(cvv: String): Boolean {
        if (cvv.length < 3) {
            alertCVV.postValue(true)
            return false
        }
        return true
    }

    fun checkDefaultCard(idCard: String): Boolean {
        return userManager.getPayment() == idCard
    }

    companion object {
        const val SUCCESS = "Add success"
        const val FAIL = "Add fail"
    }
}