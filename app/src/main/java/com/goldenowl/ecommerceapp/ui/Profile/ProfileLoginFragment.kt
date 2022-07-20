package com.goldenowl.ecommerceapp.ui.Profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentProfileLoginBinding
import com.goldenowl.ecommerceapp.ui.Auth.AuthActivity
import com.goldenowl.ecommerceapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileLoginFragment : BaseFragment() {
    private lateinit var binding: FragmentProfileLoginBinding
    private val viewModel: ProfileViewModel by viewModels()
    private var totalAddress = 0
    private var paymentTxt = ""
    private var totalOrder = 0
    private var totalReview = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileLoginBinding.inflate(inflater, container, false)
        viewModel.isLoading.postValue(true)
        setupObserve()
        bind()
        return binding.root
    }

    private fun setDefault() {
        viewModel.apply {
            this@ProfileLoginFragment.totalAddress = address.value?.size ?: 0
            setSubTitleCard()
            this@ProfileLoginFragment.totalOrder = totalOrder.value ?: 0
            this@ProfileLoginFragment.totalReview = reviews.value?.size ?: 0
        }
        binding.apply {
            txtSubTitleShipping.text = "$totalAddress addresses"
            txtSubTitlePayment.text = paymentTxt
            txtSubTitleOrder.text = "Already have $totalOrder orders"
            txtSubTitleReview.text = "Reviews for $totalReview items"
        }
    }

    private fun setupObserve() {
        viewModel.apply {
            address.observe(viewLifecycleOwner) {
                setDefault()
            }
            payment.observe(viewLifecycleOwner) {
                setDefault()
            }

            totalOrder.observe(viewLifecycleOwner) {
                setDefault()
            }
            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }
        }
    }

    private fun freshData(){
        viewModel.apply {
            getAddress()
            getPayment()
            getTotalOrder()
            getReviews()
        }

    }

    private fun setSubTitleCard(){
        var str = ""
        viewModel.payment.value?.let {
            if (it.id.isNotBlank()) {
                str = if (it.number[0] == '4') {
                    "Visa  **${it.number.substring(it.number.length - 2)}"
                } else {
                    "Mastercard  **${it.number.substring(it.number.length - 2)}"
                }
            }
        }
        this@ProfileLoginFragment.paymentTxt = str
    }

    private fun bind() {
        binding.apply {
            appBarLayout.topAppBar.title = getString(R.string.my_profile)
            setDefault()
            btnLogout.setOnClickListener {
                viewModel.logOut()
                startActivity(Intent(activity, AuthActivity::class.java))
                activity?.finish()
            }
            myOrderLayout.setOnClickListener {
                findNavController().navigate(R.id.ordersFragment)
            }
            shippingLayout.setOnClickListener {
                findNavController().navigate(R.id.shippingAddressFragment)
            }
            paymentLayout.setOnClickListener {
                findNavController().navigate(R.id.paymentMethodFragment)
            }
            promocodesLayout.setOnClickListener {
                findNavController().navigate(R.id.promoListFragment)
            }
            myReviewLayout.setOnClickListener {
                findNavController().navigate(R.id.reviewListFragment)
            }
            settingLayout.setOnClickListener {
                findNavController().navigate(R.id.settingFragment)
            }
        }
        viewModel.isLoading.postValue(false)
    }

    override fun onResume() {
        super.onResume()
        freshData()
        binding.apply {
            viewModel.setupProfileUI(this@ProfileLoginFragment, txtName, txtEmail, imgAvatar)
        }
    }
}