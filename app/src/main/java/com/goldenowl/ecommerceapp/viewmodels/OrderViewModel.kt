package com.goldenowl.ecommerceapp.viewmodels


import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.Order
import com.goldenowl.ecommerceapp.data.UserManager
import com.goldenowl.ecommerceapp.utilities.USER_FIREBASE
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val userManager: UserManager
) : BaseViewModel() {
    private val db = Firebase.firestore
    val allOrder: MutableLiveData<List<Order>> = MutableLiveData()
    val order: MutableLiveData<Order> = MutableLiveData()
    fun fetchData() {
        db.collection(USER_FIREBASE).document(userManager.getAccessToken()).collection("order")
            .get().addOnSuccessListener { result ->
                val list: MutableList<Order> = mutableListOf()
                for (document in result) {
                    if (document.id != "LAST_EDIT") {
                        list.add(document.toObject())
                    }
                }
                allOrder.postValue(list)
            }
    }

    fun getOrder(idOrder: String) {
        db.collection(USER_FIREBASE).document(userManager.getAccessToken()).collection("order")
            .document(idOrder).get().addOnSuccessListener { documentSnapshot ->
                order.postValue(documentSnapshot.toObject<Order>())
            }
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
                textView.text = statuses[0]
                textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }
        }
    }

    companion object {
        val statuses = listOf("Delivered", "Processing", "Cancelled")
        const val DELIVERED = 0
        const val PROCESSING = 1
        const val CANCELLED = 2
    }
}