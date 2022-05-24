package com.goldenowl.ecommerceapp.ui.Auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.facebook.login.LoginManager
import com.goldenowl.ecommerceapp.MainActivity
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentLoginBinding
import com.goldenowl.ecommerceapp.utilities.REQUEST_SIGN_IN
import com.goldenowl.ecommerceapp.viewmodels.AuthViewModel
import com.goldenowl.ecommerceapp.viewmodels.AuthViewModelFactory
import com.goldenowl.ecommerceapp.viewmodels.OnSignInStartedListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.actionBar.toolBar.title = "Login"

        val factory = AuthViewModelFactory(this.requireActivity().application, object:
            OnSignInStartedListener {
            override fun onSignInStarted(client: GoogleSignInClient?) {
                startActivityForResult(client?.signInIntent, REQUEST_SIGN_IN)
            }
        })
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)
        observeSetup()


        binding.actionBar.toolBar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
        binding.btnLogin.setOnClickListener {
            authViewModel.validEmail(binding.editTextEmail.text.toString())
            authViewModel.validPassword(binding.editTextPassword.text.toString())
            if (!binding.txtLayoutPassword.isErrorEnabled && !binding.txtLayoutEmail.isErrorEnabled) {
                authViewModel.logIn(
                    binding.editTextEmail.text.toString(),
                    binding.editTextPassword.text.toString()
                )
            }
        }
        binding.btnGoogle.setOnClickListener {
            authViewModel.signInWithGoogle()
        }
        binding.btnFacebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this.requireActivity(), listOf("email"))
            authViewModel.loginWithFacebook()
        }
        binding.btnForgetPassword.setOnClickListener{
            authViewModel.validEmail(binding.editTextEmail.text.toString())
            if(!binding.txtLayoutEmail.isErrorEnabled){
                authViewModel.forgotPassword(binding.editTextEmail.text.toString())
            }
        }
        return binding.root
    }
    private fun observeSetup() {
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

        authViewModel.validEmailLiveData.observe(this.viewLifecycleOwner) {
            alertEmail(it)
        }

//        authViewModel.validPasswordLiveData.observe(this.viewLifecycleOwner) {
//            alertPassword(it)
//        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SIGN_IN && resultCode == Activity.RESULT_OK && data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!

                authViewModel.firebaseAuthWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                Toast.makeText(this.context, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
        authViewModel.callbackManager.onActivityResult(requestCode, resultCode, data)
    }



    private fun alertEmail(alert: String?) {
        if (!alert.isNullOrEmpty()) {
            binding.txtLayoutEmail.isErrorEnabled = true
            binding.txtLayoutEmail.error = alert
            binding.txtLayoutEmail.endIconMode = TextInputLayout.END_ICON_NONE
        } else {
            binding.txtLayoutEmail.isErrorEnabled = false
            binding.txtLayoutEmail.endIconMode = TextInputLayout.END_ICON_CUSTOM

        }
    }

//    private fun alertPassword(alert: String?) {
//        if (!alert.isNullOrEmpty()) {
//            binding.txtLayoutPassword.isErrorEnabled = true
//            binding.txtLayoutPassword.error = alert
//        } else {
//            binding.txtLayoutPassword.isErrorEnabled = false
//        }
//    }

}