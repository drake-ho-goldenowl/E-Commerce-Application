package com.goldenowl.ecommerceapp.ui.Profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentProfileLoginBinding
import com.goldenowl.ecommerceapp.ui.Auth.AuthActivity
import com.goldenowl.ecommerceapp.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileLoginFragment : Fragment() {
    private lateinit var binding: FragmentProfileLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileLoginBinding.inflate(inflater, container, false)

        bind()
        return binding.root
    }

    private fun bind() {
        binding.apply {
            if (authViewModel.userManager.isLogged()) {
                txtName.text = authViewModel.userManager.getName()
                txtEmail.text = authViewModel.userManager.getEmail()
                Glide.with(requireActivity())
                    .load(authViewModel.userManager.getAvatar())
                    .error(R.drawable.ic_no_login)
                    .into(binding.imgAvatar)
            }

            btnLogout.setOnClickListener {
                authViewModel.userManager.logOut()
                authViewModel.logOut()
                startActivity(Intent(activity, AuthActivity::class.java))
                activity?.finish()
            }
            settingLayout.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_settingFragment)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        authViewModel.apply {
            if (userManager.isLogged()) {
                binding.txtName.text = userManager.getName()
                binding.txtEmail.text = userManager.getEmail()
                Glide.with(this@ProfileLoginFragment)
                    .load(userManager.getAvatar())
                    .error(R.drawable.ic_no_login)
                    .into(binding.imgAvatar)
            }
        }

    }
}