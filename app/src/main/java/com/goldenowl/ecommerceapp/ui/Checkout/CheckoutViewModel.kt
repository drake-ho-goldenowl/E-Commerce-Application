package com.goldenowl.ecommerceapp.ui.Checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val shippingAddressRepository: ShippingAddressRepository,
    private val paymentRepository: PaymentRepository,
    private val orderRepository: OrderRepository,
    private val bagRepository: BagRepository,
    private val promotionRepository: PromotionRepository,
    private val userManager: UserManager,
) : BaseViewModel() {
    private val statusIdPayment = MutableStateFlow("")
    val success: MutableLiveData<Boolean> = MutableLiveData(false)

    val bag = bagRepository.bagAndProduct
    val shippingAddress = shippingAddressRepository.address

    init {
        bagRepository.fetchBagAndProduct()
    }

    val payment = statusIdPayment.flatMapLatest {
        getPayment(it).asFlow()
    }.asLiveData()


    private fun getPayment(idPayment: String): LiveData<Card> {
        val result = paymentRepository.card
        if (idPayment.isNotBlank()) {
            paymentRepository.getCard(idPayment)
        } else {
            paymentRepository.card.postValue(null)
        }
        return result
    }

    fun getPromotion(idPromotion: String): LiveData<Promotion> {
        val result = promotionRepository.promotion
        if (idPromotion.isNotBlank()) {
            promotionRepository.getPromotion(idPromotion)
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
        setOrderOnFirebase(order)
        bagRepository.removeAllFirebase()
        success.postValue(true)
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
        orderRepository.setOrderFirebase(order)
    }

    fun setIdAddressDefault() {
        if (userManager.getAddress().isNotBlank()) {
            shippingAddressRepository.getAddress(userManager.getAddress())
        } else {
            shippingAddressRepository.address.postValue(null)
        }
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