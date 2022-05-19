package com.goldenowl.ecommerceapp.ui.Auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.MainActivity
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentLoginBinding
import com.goldenowl.ecommerceapp.viewmodels.AuthViewModel
import com.google.android.material.textfield.TextInputLayout


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.actionBar.toolBar.title = "Login"
        authViewModel.toastMessage.observe(this.viewLifecycleOwner) { str ->
            Toast.makeText(
                this.context,
                str,
                Toast.LENGTH_SHORT
            ).show()
        }
        authViewModel.userLiveData.observe(this.viewLifecycleOwner) {
            if (it != null) {
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener {
            when {
                binding.editTextEmail.text.isNullOrBlank() -> {
                    binding.txtLayoutEmail.isErrorEnabled = true
                    binding.txtLayoutEmail.error = "Mustn't empty"
                    binding.txtLayoutEmail.endIconMode = TextInputLayout.END_ICON_NONE
                }
                binding.editTextPassword.text.isNullOrBlank() -> {
                    binding.txtLayoutPassword.isErrorEnabled = true
                    binding.txtLayoutPassword.error = "Mustn't empty"
                }
                else -> {
                    authViewModel.logIn(
                        binding.editTextEmail.text.toString(),
                        binding.editTextPassword.text.toString()
                    )
                }
            }
        }
//        binding.btnForgetPassword.setOnClickListener(
//            Navigation.createNavigateOnClickListener(R.id.action_to_SignUp, null)
//        )
        binding.actionBar.toolBar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

    }

}