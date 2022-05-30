package com.goldenowl.ecommerceapp.ui.Profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.SettingActivity
import com.goldenowl.ecommerceapp.data.UserManager
import com.goldenowl.ecommerceapp.databinding.FragmentProfileLoginBinding
import com.goldenowl.ecommerceapp.ui.Auth.AuthActivity
import com.goldenowl.ecommerceapp.utilities.REQUEST_SIGN_IN
import com.goldenowl.ecommerceapp.viewmodels.AuthViewModel
import com.goldenowl.ecommerceapp.viewmodels.AuthViewModelFactory
import com.goldenowl.ecommerceapp.viewmodels.OnSignInStartedListener
import com.google.android.gms.auth.api.signin.GoogleSignInClient

class ProfileLoginFragment : Fragment() {
    private lateinit var binding:FragmentProfileLoginBinding
    private lateinit var userManager: UserManager
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileLoginBinding.inflate(inflater,container,false)
        userManager = UserManager.getInstance(this.requireContext())
        val factory = AuthViewModelFactory(this.requireActivity().application, object:
            OnSignInStartedListener {
            override fun onSignInStarted(client: GoogleSignInClient?) {
                startActivityForResult(client?.signInIntent, REQUEST_SIGN_IN)
            }
        })
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)


        bind()
        return binding.root
    }

    private fun bind(){
        binding.apply {
            if(userManager.isLogged()){
                txtName.text =  userManager.getName()
                txtEmail.text = userManager.getEmail()
                Glide.with(requireActivity())
                    .load(userManager.getAvatar())
                    .error(R.drawable.ic_no_login)
                    .into(binding.imgAvatar)
            }

            btnLogout.setOnClickListener{
                userManager.logOut()
                authViewModel.logOut()
                startActivity(Intent(activity, AuthActivity::class.java))
                activity?.finish()
            }
            settingLayout.setOnClickListener{
                startActivity(Intent(activity, SettingActivity::class.java))
//            activity?.finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(userManager.isLogged()){
            binding.txtName.text =  userManager.getName()
            binding.txtEmail.text = userManager.getEmail()
            Glide.with(this)
                .load(userManager.getAvatar())
                .error(R.drawable.ic_no_login)
                .into(binding.imgAvatar)
        }
    }
}