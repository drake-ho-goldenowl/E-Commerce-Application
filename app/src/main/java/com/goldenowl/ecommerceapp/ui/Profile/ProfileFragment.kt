package com.goldenowl.ecommerceapp.ui.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentProfileBinding
import com.goldenowl.ecommerceapp.ui.Auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        if (!viewModel.isLogged()) {
            findNavController().navigate(R.id.action_profileFragment_to_profileNoLoginFragment)
        } else {
            findNavController().navigate(R.id.action_profileFragment_to_profileLoginFragment)
        }
        return binding.root
    }
}