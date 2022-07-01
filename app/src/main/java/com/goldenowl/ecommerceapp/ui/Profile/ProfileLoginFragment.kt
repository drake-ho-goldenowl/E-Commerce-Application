package com.goldenowl.ecommerceapp.ui.Profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentProfileLoginBinding
import com.goldenowl.ecommerceapp.ui.Auth.AuthActivity
import com.goldenowl.ecommerceapp.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileLoginFragment : Fragment() {
    private lateinit var binding: FragmentProfileLoginBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileLoginBinding.inflate(inflater, container, false)
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
                binding.txtSubTitlePayment.text = it
            }
            totalOrder.observe(viewLifecycleOwner) {
                binding.txtSubTitleOrder.text = "Already have $it orders"
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
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            viewModel.setupProfileUI(this@ProfileLoginFragment, txtName, txtEmail, imgAvatar)
            viewModel.getPayment()
        }
    }
}