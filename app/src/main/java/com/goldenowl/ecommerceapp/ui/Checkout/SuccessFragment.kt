package com.goldenowl.ecommerceapp.ui.Checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentSuccessBinding

class SuccessFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSuccessBinding.inflate(inflater, container, false)

        binding.btnContinueShopping.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }
        return binding.root
    }
}