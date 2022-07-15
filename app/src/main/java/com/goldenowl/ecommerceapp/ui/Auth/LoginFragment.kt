package com.goldenowl.ecommerceapp.ui.Auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.facebook.login.LoginManager
import com.goldenowl.ecommerceapp.MainActivity
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentLoginBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment
import com.goldenowl.ecommerceapp.ui.OnSignInStartedListener
import com.goldenowl.ecommerceapp.utilities.REQUEST_SIGN_IN
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    private lateinit var binding: FragmentLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        observeSetup()
        bind()
        return binding.root
    }


    private fun bind() {
        binding.apply {
            appBarLayout.topAppBar.title = getString(R.string.login)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }

            btnLogin.setOnClickListener {
                authViewModel.logIn(
                    editTextEmail.text.toString(),
                    editTextPassword.text.toString()
                )
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

            btnForgetPassword.setOnClickListener {
                findNavController().navigate(R.id.forgotPasswordFragment)
            }
        }
    }

    private fun observeSetup() {
        authViewModel.apply {
            toastMessage.observe(viewLifecycleOwner) { str ->
                toastMessage(str)
                toastMessage.postValue("")
            }
            userLiveData.observe(viewLifecycleOwner) {
                if (it != null) {
                    startActivity(Intent(activity, MainActivity::class.java))
                    activity?.finish()
                }
            }

            validEmailLiveData.observe(viewLifecycleOwner) {
                alertEmail(it)
            }
            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }

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
}