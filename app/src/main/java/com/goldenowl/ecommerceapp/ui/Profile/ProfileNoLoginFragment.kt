package com.goldenowl.ecommerceapp.ui.Profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.goldenowl.ecommerceapp.databinding.FragmentProfileNoLoginBinding
import com.goldenowl.ecommerceapp.model.UserManager
import com.goldenowl.ecommerceapp.ui.Auth.AuthActivity
import com.goldenowl.ecommerceapp.utilities.REQUEST_SIGN_IN
import com.goldenowl.ecommerceapp.viewmodels.AuthViewModel
import com.goldenowl.ecommerceapp.viewmodels.AuthViewModelFactory
import com.goldenowl.ecommerceapp.viewmodels.OnSignInStartedListener
import com.google.android.gms.auth.api.signin.GoogleSignInClient

class ProfileNoLoginFragment : Fragment() {
    private lateinit var binding: FragmentProfileNoLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileNoLoginBinding.inflate(inflater,container,false)

        binding.profileNoLoginLayout.setOnClickListener {
            startActivity(Intent(activity, AuthActivity::class.java))
            activity?.finish()
        }

        val factory = AuthViewModelFactory(this.requireActivity().application, object:
            OnSignInStartedListener {
            override fun onSignInStarted(client: GoogleSignInClient?) {
                startActivityForResult(client?.signInIntent, REQUEST_SIGN_IN)
            }
        })
        val authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)
        val userManager = UserManager.getInstance(this.requireContext())
        userManager.logOut()
        authViewModel.logOut()
        return binding.root
    }
}