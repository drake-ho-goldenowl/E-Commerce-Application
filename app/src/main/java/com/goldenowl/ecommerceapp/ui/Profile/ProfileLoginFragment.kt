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

    private fun setupObserve() {
        viewModel.apply {
            viewModel.getPayment()
            totalAddress.observe(viewLifecycleOwner) {
                binding.txtSubTitleShipping.text = "$it addresses"
            }
            payment.observe(viewLifecycleOwner) {
                if (it.id.isNotBlank()) {
                    if (it.number[0] == '4') {
                        binding.txtSubTitlePayment.text =
                            "Visa  **${it.number.substring(it.number.length - 2)}"
                    } else {
                        binding.txtSubTitlePayment.text =
                            "Mastercard  **${it.number.substring(it.number.length - 2)}"
                    }
                } else {
                    binding.txtSubTitlePayment.text = ""
                }
            }

            totalOrder.observe(viewLifecycleOwner) {
                binding.txtSubTitleOrder.text = "Already have $it orders"
            }

            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }
        }
    }

    private fun bind() {
        binding.apply {
            viewModel.setupProfileUI(this@ProfileLoginFragment, txtName, txtEmail, imgAvatar)
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
            settingLayout.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_settingFragment)
            }
        }
        viewModel.isLoading.postValue(false)
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            viewModel.setupProfileUI(this@ProfileLoginFragment, txtName, txtEmail, imgAvatar)
            viewModel.getPayment()
        }
    }
}