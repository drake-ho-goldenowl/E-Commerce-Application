package com.goldenowl.ecommerceapp.ui.Auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.facebook.login.LoginManager
import com.goldenowl.ecommerceapp.MainActivity
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentSignUpBinding
import com.goldenowl.ecommerceapp.utilities.REQUEST_SIGN_IN
import com.goldenowl.ecommerceapp.viewmodels.AuthViewModel
import com.goldenowl.ecommerceapp.viewmodels.AuthViewModelFactory
import com.goldenowl.ecommerceapp.viewmodels.OnSignInStartedListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var authViewModel: AuthViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        binding.actionBar.toolBar.title = "Sign up"
        val factory = AuthViewModelFactory(this.requireActivity().application, object:
            OnSignInStartedListener {
            override fun onSignInStarted(client: GoogleSignInClient?) {
                startActivityForResult(client?.signInIntent, REQUEST_SIGN_IN)
            }
        })
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)
        observeSetup()
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

        authViewModel.validNameLiveData.observe(this.viewLifecycleOwner) {
            alertName(it)
        }

        authViewModel.validEmailLiveData.observe(this.viewLifecycleOwner) {
            alertEmail(it)
        }

        authViewModel.validPasswordLiveData.observe(this.viewLifecycleOwner) {
            alertPassword(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAlreadyHave.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                R.id.action_signUpFragment_to_loginFragment,
                null
            )
        )

        binding.actionBar.toolBar.setNavigationOnClickListener {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }

        binding.btnSignUp.setOnClickListener {
            authViewModel.validName(binding.editTextName.text.toString())
            authViewModel.validEmail(binding.editTextEmail.text.toString())
            authViewModel.validPassword(binding.editTextPassword.text.toString())
            if (!binding.txtLayoutPassword.isErrorEnabled && !binding.txtLayoutEmail.isErrorEnabled &&
                !binding.txtLayoutName.isErrorEnabled
            ) {
                authViewModel.signUp(
                    binding.editTextName.text.toString(),
                    binding.editTextEmail.text.toString(),
                    binding.editTextPassword.text.toString()
                )
            }
        }
        binding.editTextEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, count: Int) {}
            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
                authViewModel.validEmail(binding.editTextEmail.text.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, count: Int) {}
            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
                authViewModel.validPassword(binding.editTextPassword.text.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.btnGoogle.setOnClickListener {
            authViewModel.signInWithGoogle()
        }
        binding.btnFacebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this.requireActivity(), listOf("email"))
            authViewModel.loginWithFacebook()
        }
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


    private fun alertName(alert: String) {
        if (!alert.isNullOrEmpty()) {
            binding.txtLayoutName.isErrorEnabled = true
            binding.txtLayoutName.error = alert
            binding.txtLayoutName.endIconMode = TextInputLayout.END_ICON_NONE
        } else {
            binding.txtLayoutName.isErrorEnabled = false
            binding.txtLayoutName.endIconMode = TextInputLayout.END_ICON_CUSTOM

        }
    }

    private fun alertEmail(alert: String) {
        if (!alert.isNullOrEmpty()) {
            binding.txtLayoutEmail.isErrorEnabled = true
            binding.txtLayoutEmail.error = alert
            binding.txtLayoutEmail.endIconMode = TextInputLayout.END_ICON_NONE
        } else {
            binding.txtLayoutEmail.isErrorEnabled = false
            binding.txtLayoutEmail.endIconMode = TextInputLayout.END_ICON_CUSTOM

        }
    }

    private fun alertPassword(alert: String) {
        if (!alert.isNullOrEmpty()) {
            binding.txtLayoutPassword.isErrorEnabled = true
            binding.txtLayoutPassword.error = alert
        } else {
            binding.txtLayoutPassword.isErrorEnabled = false
        }
    }

}