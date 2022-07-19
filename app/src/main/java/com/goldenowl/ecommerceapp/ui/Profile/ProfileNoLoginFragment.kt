package com.goldenowl.ecommerceapp.ui.Profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentProfileNoLoginBinding
import com.goldenowl.ecommerceapp.ui.Auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileNoLoginFragment : Fragment() {
    private lateinit var binding: FragmentProfileNoLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileNoLoginBinding.inflate(inflater, container, false)
        binding.appBarLayout.topAppBar.title = getString(R.string.my_profile)

        binding.profileNoLoginLayout.setOnClickListener {
            startActivity(Intent(activity, AuthActivity::class.java))
            activity?.finish()
        }
        return binding.root
    }
}