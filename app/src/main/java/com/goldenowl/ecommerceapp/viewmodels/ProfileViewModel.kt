package com.goldenowl.ecommerceapp.viewmodels

import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.Card
import com.goldenowl.ecommerceapp.data.OrderRepository
import com.goldenowl.ecommerceapp.data.ShippingAddressRepository
import com.goldenowl.ecommerceapp.data.UserManager
import com.goldenowl.ecommerceapp.utilities.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val shippingAddressRepository: ShippingAddressRepository,
    private val rsa: RSA,
    private val userManager: UserManager,
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    val totalAddress = shippingAddressRepository.getSize().asLiveData()
    val payment = MutableLiveData("")
    val totalOrder = orderRepository.getSize().asLiveData()

    fun setupProfileUI(
        fragment: Fragment,
        name: TextView,
        email: TextView,
        avatar: CircleImageView
    ) {
        if (userManager.isLogged()) {
            name.text = userManager.getName()
            email.text = userManager.getEmail()
            Glide.with(fragment)
                .load(userManager.getAvatar())
                .error(R.drawable.ic_no_login)
                .into(avatar)
        }
    }

    fun getPayment() {
        if (userManager.getPayment().isNotBlank()) {
            db.collection(USER_FIREBASE).document(userManager.getAccessToken()).collection(
                PAYMENT_USER
            ).document(userManager.getPayment()).get().addOnSuccessListener { doc ->
                if (doc.exists() && doc != null) {
                    val card = doc.toObject<Card>()
                    card?.let {
                        it.number = rsa.decrypt(it.number)
                        if (it.number[0] == '4') {
                            payment.postValue("Visa  **${it.number.substring(card.number.length - 2)}")
                        } else {
                            payment.postValue("Mastercard  **${it.number.substring(card.number.length - 2)}")
                        }
                    }
                } else {
                    payment.postValue("")
                }
            }
        } else {
            payment.postValue("")
        }
    }

    fun logOut() {
        firebaseAuth.signOut()
        userManager.logOut()
        LAST_EDIT_TIME_FAVORITES = null
        LAST_EDIT_TIME_BAG = null
    }
}