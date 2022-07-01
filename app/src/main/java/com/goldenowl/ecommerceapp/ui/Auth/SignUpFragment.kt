package com.goldenowl.ecommerceapp.ui.Auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.facebook.login.LoginManager
import com.goldenowl.ecommerceapp.MainActivity
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentSignUpBinding
import com.goldenowl.ecommerceapp.utilities.REQUEST_SIGN_IN
import com.goldenowl.ecommerceapp.viewmodels.AuthViewModel
import com.goldenowl.ecommerceapp.viewmodels.OnSignInStartedListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        observeSetup()
        bind()
        return binding.root
    }

    private fun bind() {
        binding.apply {
            appBarLayout.topAppBar.title = getString(R.string.sign_up)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }

            btnAlreadyHave.setOnClickListener(
                Navigation.createNavigateOnClickListener(
                    R.id.action_signUpFragment_to_loginFragment,
                    null
                )
            )

            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }

            btnSignUp.setOnClickListener {
                authViewModel.signUp(
                    editTextName.text.toString(),
                    editTextEmail.text.toString(),
                    editTextPassword.text.toString()
                )
            }

            editTextName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    authViewModel.validName(editTextName.text.toString())
                }
            }

            editTextEmail.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    authViewModel.validEmail(editTextEmail.text.toString())
                }
            }

            editTextPassword.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    authViewModel.validPassword(editTextPassword.text.toString())
                }
            }

            btnGoogle.setOnClickListener {
                authViewModel.signInWithGoogle(object :
                    OnSignInStartedListener {
                    override fun onSignInStarted(client: GoogleSignInClient?) {
                        startActivityForResult(client?.signInIntent, REQUEST_SIGN_IN)
                    }
                })
            }

            btnFacebook.setOnClickListener {
                LoginManager.getInstance()
                    .logInWithReadPermissions(
                        requireActivity(), authViewModel.callbackManager, listOf(
                            AuthViewModel.PUBLIC_PROFILE,
                            AuthViewModel.EMAIL,
                            AuthViewModel.USER_FRIEND
                        )
                    )
                authViewModel.loginWithFacebook()
            }
        }
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        authViewModel.callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGN_IN && resultCode == Activity.RESULT_OK && data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                account.idToken?.let { authViewModel.firebaseAuthWithGoogle(it) }
            } catch (e: ApiException) {
                Toast.makeText(this.context, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private fun alertName(alert: String) {
        binding.apply {
            if (!alert.isNullOrEmpty()) {
                txtLayoutName.isErrorEnabled = true
                txtLayoutName.error = alert
                txtLayoutName.endIconMode = TextInputLayout.END_ICON_NONE
            } else {
                txtLayoutName.isErrorEnabled = false
                txtLayoutName.endIconMode = TextInputLayout.END_ICON_CUSTOM
            }
        }
    }

    private fun alertEmail(alert: String) {
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

    private fun alertPassword(alert: String) {
        binding.apply {
            if (!alert.isNullOrEmpty()) {
                txtLayoutPassword.isErrorEnabled = true
                txtLayoutPassword.error = alert
            } else {
                txtLayoutPassword.isErrorEnabled = false
            }
        }
    }

}