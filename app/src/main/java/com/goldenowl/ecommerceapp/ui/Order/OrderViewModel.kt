package com.goldenowl.ecommerceapp.ui.Order


import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.BagRepository
import com.goldenowl.ecommerceapp.data.Order
import com.goldenowl.ecommerceapp.data.OrderRepository
import com.goldenowl.ecommerceapp.data.ProductOrder
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val bagRepository: BagRepository,
    private val orderRepository: OrderRepository,
) : BaseViewModel() {
    private val idOrder = MutableStateFlow("")
    val order = idOrder.flatMapLatest {
        orderRepository.getOrder(it)
    }.asLiveData()


    fun setIdOrder(id: String) {
        idOrder.value = id
    }

    fun getOrderStatus(status: Int): MutableLiveData<List<Order>> {
        return orderRepository.getOrderStatus(status)
    }

    fun setUIStatus(context: Context, textView: TextView, status: Int) {
        when (status) {
            DELIVERED -> {
                textView.text = statuses[0]
                textView.setTextColor(ContextCompat.getColor(context, R.color.green))
            }
            PROCESSING -> {
                textView.text = statuses[1]
                textView.setTextColor(ContextCompat.getColor(context, R.color.yellow))
            }
            CANCELLED -> {
                textView.text = statuses[2]
                textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }
        }
    }

    fun reOrder(list: List<ProductOrder>) {
        for (productOrder in list) {
            productOrder.apply {
                bagRepository.insertBag(
                    size = size,
                    color = color,
                    idProduct = idProduct,
                    quantity = units.toLong()
                )
            }
        }
        dismiss.postValue(true)
    }
}