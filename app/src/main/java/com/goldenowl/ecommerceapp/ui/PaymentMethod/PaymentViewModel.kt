package com.goldenowl.ecommerceapp.ui.PaymentMethod

import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.data.Card
import com.goldenowl.ecommerceapp.data.PaymentRepository
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
) : BaseViewModel() {
    val cards = paymentRepository.listCard
    val alertName = MutableLiveData(false)
    val alertNumberCard = MutableLiveData(false)
    val alertExpertDate = MutableLiveData(false)
    val alertCVV = MutableLiveData(false)


    fun fetchData() {
        paymentRepository.fetchData()
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
            paymentRepository.insertCard(name, number, expertDate, cvv, default)
            dismiss.postValue(true)
        }
    }

    fun setDefaultPayment(idCard: String) {
        paymentRepository.setDefaultPayment(idCard)
    }

    fun removeDefaultPayment() {
        paymentRepository.removeDefaultPayment()
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

    fun deleteCard(card: Card) {
        paymentRepository.deleteCard(card)
    }

    fun checkDefaultCard(idCard: String): Boolean {
        return paymentRepository.checkDefaultCard(idCard)
    }
}