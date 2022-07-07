package com.goldenowl.ecommerceapp.ui.Auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentForgotPasswordBinding
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        setupObserve()
        bind()
        return binding.root
    }

    private fun setupObserve() {
        viewModel.apply {
            toastMessage.observe(viewLifecycleOwner) { str ->
                Toast.makeText(
                    context,
                    str,
                    Toast.LENGTH_SHORT
                ).show()
                if(str == SUCCESS){
                    findNavController().navigateUp()
                }
            }

            validEmailLiveData.observe(viewLifecycleOwner) {
                alertEmail(it)
            }
        }
    }

    private fun bind() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = getString(R.string.forgot_password)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            btnForgetPassword.setOnClickListener {
                viewModel.validEmail(editTextEmail.text.toString())
                if (!txtLayoutEmail.isErrorEnabled) {
                    viewModel.forgotPassword(editTextEmail.text.toString())
                }
            }
        }
    }

    private fun alertEmail(alert: String?) {
        binding.apply {
            if (!alert.isNullOrEmpty()) {
                txtLayoutEmail.isErrorEnabled = true
                txtLayoutEmail.error = alert
                txtLayoutEmail.endIconMode = TextInputLayout.END_ICON_NONE
            } else {
                txtLayoutEmail.isErrorEnabled = false
                txtLayoutEmail.endIconMode = TextInputLayout.END_ICON_CUSTOM
            }
        }
    }
    companion object{
        const val SUCCESS = "Email sent."
    }
}