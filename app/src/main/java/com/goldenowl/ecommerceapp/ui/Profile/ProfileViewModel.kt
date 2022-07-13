package com.goldenowl.ecommerceapp.ui.Profile

import android.widget.TextView
import androidx.fragment.app.Fragment
import com.goldenowl.ecommerceapp.data.OrderRepository
import com.goldenowl.ecommerceapp.data.PaymentRepository
import com.goldenowl.ecommerceapp.data.ShippingAddressRepository
import com.goldenowl.ecommerceapp.data.UserManager
import com.goldenowl.ecommerceapp.ui.BaseViewModel
import com.goldenowl.ecommerceapp.utilities.GlideDefault
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val shippingAddressRepository: ShippingAddressRepository,
    private val paymentRepository: PaymentRepository,
    private val userManager: UserManager,
    private val firebaseAuth: FirebaseAuth
) : BaseViewModel() {
    val totalAddress = shippingAddressRepository.countAddress()
    val payment = paymentRepository.card
    val totalOrder = orderRepository.getSize()

    fun setupProfileUI(
        fragment: Fragment,
        name: TextView,
        email: TextView,
        avatar: CircleImageView
    ) {
        if (userManager.isLogged()) {
            name.text = userManager.getName()
            email.text = userManager.getEmail()

            GlideDefault.userImage(
                fragment.requireContext(),
                userManager.getAvatar(),
                avatar
            )
        }
    }

    fun getPayment() {
        if (userManager.getPayment().isNotBlank()) {
            paymentRepository.getCard(userManager.getPayment())
        }
    }

    fun logOut() {
        firebaseAuth.signOut()
        userManager.logOut()
    }
}