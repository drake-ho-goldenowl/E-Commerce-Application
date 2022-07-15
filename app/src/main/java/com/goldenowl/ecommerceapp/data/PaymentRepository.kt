package com.goldenowl.ecommerceapp.data

import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.utilities.PAYMENT_USER
import com.goldenowl.ecommerceapp.utilities.RSA
import com.goldenowl.ecommerceapp.utilities.USER_FIREBASE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(
    private val userManager: UserManager,
    private val db: FirebaseFirestore,
    private val rsa: RSA
) {
    val listCard: MutableLiveData<List<Card>> = MutableLiveData()
    val card: MutableLiveData<Card> = MutableLiveData()

    private fun setPaymentOnFirebase(card: Card) {
        db.collection(USER_FIREBASE)
            .document(userManager.getAccessToken())
            .collection(PAYMENT_USER)
            .document(card.id)
            .set(card)
            .addOnSuccessListener {
                fetchData()
            }
    }

    private fun deletePaymentOnFirebase(card: Card) {
        db.collection(USER_FIREBASE)
            .document(userManager.getAccessToken())
            .collection(PAYMENT_USER)
            .document(card.id)
            .delete()
            .addOnSuccessListener {
                fetchData()
            }
    }

    fun fetchData() {
        db.collection(USER_FIREBASE)
            .document(userManager.getAccessToken())
            .collection(PAYMENT_USER)
            .get()
            .addOnSuccessListener { documents ->
                val list: MutableList<Card> = mutableListOf()
                for (document in documents) {
                    val temp = document.toObject<Card>()
                    list.add(decryptCard(temp))
                }
                listCard.postValue(list)
            }
    }

    fun getCard(idCard: String) {
        db.collection(USER_FIREBASE)
            .document(userManager.getAccessToken())
            .collection(PAYMENT_USER)
            .document(idCard)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists() && document.data != null){
                    val temp = document.toObject<Card>()
                    temp?.let {
                        card.postValue(decryptCard(it))
                    }
                }
                else{
                    card.postValue(Card())
                }
            }
    }

    private fun decryptCard(card: Card): Card {
        val result = card
        result.name = rsa.decrypt(card.name)
        result.number = rsa.decrypt(card.number)
        result.cvv = rsa.decrypt(card.cvv)
        result.expireDate = rsa.decrypt(card.expireDate)
        return result
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
        val card = createCard(name, number, expertDate, cvv)
        setPaymentOnFirebase(card)
        if (default) {
            setDefaultPayment(card.id)
        }
    }

    fun setDefaultPayment(idCard: String) {
        userManager.setPayment(idCard)
        userManager.writeProfile(db, userManager.getUser())
    }

    fun removeDefaultPayment() {
        userManager.setPayment("")
        userManager.writeProfile(db, userManager.getUser())
    }

    fun checkDefaultCard(idCard: String): Boolean {
        return userManager.getPayment() == idCard
    }

    fun deleteCard(card: Card) {
        if (checkDefaultCard(card.id)) {
            removeDefaultPayment()
        }
        deletePaymentOnFirebase(card)
    }
}