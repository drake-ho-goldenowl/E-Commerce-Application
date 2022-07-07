package com.goldenowl.ecommerceapp.ui.Order


import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val bagRepository: BagRepository,
    private val orderRepository: OrderRepository,
    private val userManager: UserManager,
    private val db: FirebaseFirestore
) : BaseViewModel() {
    private val statusIdOrder = MutableStateFlow("")
    val allOrder = orderRepository.getAll().asLiveData()
    val order = statusIdOrder.flatMapLatest {
        orderRepository.getOrder(it)
    }.asLiveData()
    val dismiss = MutableLiveData(false)
    fun setIdOrder(id: String) {
        statusIdOrder.value = id
    }

    fun filterStatus(data: List<Order>, status: Int): LiveData<List<Order>> {
        val result: MutableLiveData<List<Order>> = MutableLiveData()
        result.postValue(data.filter { order -> order.status == status })
        return result
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
        viewModelScope.launch {
            for (productOrder in list) {
                productOrder.apply {
                    bagRepository.insert(
                        Bag(
                            size = size,
                            color = color,
                            idProduct = idProduct,
                            quantity = units.toLong(),
                        )
                    )
                }
            }
            bagRepository.updateBagFirebase(db, userManager.getAccessToken())
            dismiss.postValue(true)
        }
    }

    companion object {
        val statuses = listOf("Delivered", "Processing", "Cancelled")
        const val DELIVERED = 0
        const val PROCESSING = 1
        const val CANCELLED = 2
    }
}