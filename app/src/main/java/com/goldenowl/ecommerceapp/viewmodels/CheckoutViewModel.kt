package com.goldenowl.ecommerceapp.viewmodels

import androidx.lifecycle.*
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.utilities.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val shippingAddressRepository: ShippingAddressRepository,
    private val orderRepository: OrderRepository,
    private val bagRepository: BagRepository,
    private val rsa: RSA,
    private val userManager: UserManager,
    private val db: FirebaseFirestore,
) : BaseViewModel() {
    private val statusIdAddress = MutableStateFlow("")
    private val statusIdPayment = MutableStateFlow("")
    val success: MutableLiveData<Boolean> = MutableLiveData(false)
    val bag = bagRepository.getAllBagAndProduct().asLiveData()
    val shippingAddress = statusIdAddress.flatMapLatest {
        shippingAddressRepository.getShippingAddress(it)
    }.asLiveData()

    val payment = statusIdPayment.flatMapLatest {
        getPayment(it).asFlow()
    }.asLiveData()

    private fun getPayment(idPayment: String): LiveData<Card> {
        val result = MutableLiveData<Card>()
        if (!idPayment.isNullOrBlank()) {
            db.collection(USER_FIREBASE).document(userManager.getAccessToken()).collection(
                PAYMENT_USER
            ).document(idPayment).get().addOnSuccessListener { documentSnapShpt ->
                val card = documentSnapShpt.toObject<Card>()
                card?.let {
                    it.number = rsa.decrypt(it.number)
                    result.postValue(it)
                }
            }
        } else {
            result.postValue(Card())
        }
        return result
    }

    fun getPromotion(idPromotion: String): LiveData<Promotion> {
        val result = MutableLiveData<Promotion>()
        db.collection(PROMOTION_FIREBASE).document(idPromotion).get().addOnSuccessListener {
            result.postValue(it.toObject())
        }
        return result
    }

    fun submitOrder(
        bags: List<BagAndProduct>,
        address: ShippingAddress?,
        card: Card?,
        total: Int,
        delivery: Delivery?,
        promotion: Promotion?
    ) {
        if (address == null) {
            toastMessage.postValue(ALERT_ADDRESS)
            return
        }
        if (card == null) {
            toastMessage.postValue(ALERT_PAYMENT)
            return
        }
        if (delivery == null) {
            toastMessage.postValue(ALERT_DELIVERY)
            return
        }
        val listBag = getBag(bags)
        val numberCard = getNumberCard(card)
        var sale = 0
        promotion?.let {
            sale = it.salePercent.toInt()
        }
        val order = createOrder(
            listBag,
            total.toFloat(),
            getAddress(address),
            numberCard.first,
            numberCard.second,
            delivery,
            sale.toString(),
        )
        viewModelScope.launch {
            setOrderOnFirebase(order)
            orderRepository.insert(order)
            bagRepository.deleteAll()
            bagRepository.updateBagFirebase(db, userManager.getAccessToken())
            success.postValue(true)
        }
    }

    private fun createOrder(
        product: List<ProductOrder>,
        total: Float,
        shippingAddress: String,
        payment: String,
        typePayment: Int,
        delivery: Delivery,
        promotion: String,
    ) = Order(
        products = product,
        total = total,
        status = 1,
        shippingAddress = shippingAddress,
        payment = payment,
        isTypePayment = typePayment,
        delivery = delivery,
        promotion = promotion,
    )

    private fun getBag(bagAndProducts: List<BagAndProduct>): MutableList<ProductOrder> {
        val list: MutableList<ProductOrder> = mutableListOf()
        for (bagAndProduct in bagAndProducts) {
            bagAndProduct.apply {
                val size = product.getColorAndSize(
                    bag.color,
                    bag.size
                )
                var price: Long = 0
                size?.let {
                    var salePercent = 0
                    if (product.salePercent != null) {
                        salePercent = product.salePercent
                    }
                    price = size.price * (100 - salePercent) / 100
                }

                list.add(
                    ProductOrder(
                        idProduct = product.id,
                        image = product.images[0],
                        title = product.title,
                        brandName = product.brandName,
                        size = bag.size,
                        color = bag.color,
                        units = bag.quantity.toInt(),
                        price = price.toFloat(),
                    )
                )
            }
        }
        return list
    }

    private fun getAddress(shippingAddress: ShippingAddress): String {
        shippingAddress.apply {
            return "$address\n$city, $state $zipCode, $country"
        }
    }

    private fun getNumberCard(card: Card): Pair<String, Int> {
        val type = if (card.number[0] == '4') {
            0
        } else {
            1
        }
        val number = card.number.substring(card.number.length - 4)
        return Pair(number, type)
    }

    private fun setOrderOnFirebase(order: Order) {
        db.collection(USER_FIREBASE).document(userManager.getAccessToken()).collection(ORDER_USER)
            .document(order.id).set(order)
        db.collection(USER_FIREBASE).document(userManager.getAccessToken()).collection(ORDER_USER)
            .document(LAST_EDIT).set(mapOf(VALUE_LAST_EDIT to Date().time))
    }

    fun setIdAddressDefault() {
        statusIdAddress.value = userManager.getAddress()
    }

    fun setIdPaymentDefault() {
        statusIdPayment.value = userManager.getPayment()
    }

    fun calculatorTotalOrder(list: List<BagAndProduct>, salePercent: Long = 0): Int {
        return bagRepository.calculatorTotal(list, salePercent)
    }

    companion object {
        const val ALERT_DELIVERY = "Please choose one delivery"
        const val ALERT_ADDRESS = "Please choose one address"
        const val ALERT_PAYMENT = "Please choose one payment"
    }
}