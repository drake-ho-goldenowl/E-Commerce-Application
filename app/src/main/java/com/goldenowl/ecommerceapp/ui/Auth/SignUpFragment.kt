package com.goldenowl.ecommerceapp.ui.Auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.goldenowl.ecommerceapp.MainActivity
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentSignUpBinding
import com.goldenowl.ecommerceapp.utilities.REQUEST_SIGN_IN
import com.goldenowl.ecommerceapp.viewmodels.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        binding.actionBar.toolBar.title = "Sign up"
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

        setUpGoogleLogin()
        return binding.root
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

            when {
                binding.editTextName.text.isNullOrBlank() -> {
                    binding.txtLayoutName.isErrorEnabled = true
                    binding.txtLayoutName.error = "Mustn't empty"
                    binding.txtLayoutName.endIconMode = TextInputLayout.END_ICON_NONE
                }
                !validEmail().isNullOrBlank() -> {
                    binding.txtLayoutEmail.isErrorEnabled = true
                    binding.txtLayoutEmail.error = validEmail()
                    binding.txtLayoutEmail.endIconMode = TextInputLayout.END_ICON_NONE
                }
                !validPassword().isNullOrBlank() -> {
                    binding.txtLayoutPassword.isErrorEnabled = true
                    binding.txtLayoutPassword.error = validPassword()
                }
                else -> {
                    authViewModel.signUp(
                        binding.editTextEmail.text.toString(),
                        binding.editTextPassword.text.toString()
                    )
                }
            }
        }
        binding.editTextEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, count: Int) {}
            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
                alertEmail()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, count: Int) {}
            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
                alertPassword()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

//        binding.btnGoogle.setOnClickListener{
//            signInGoogle()
//        }
        binding.btnFacebook.setOnClickListener {
            loginWithFacebook()
        }
    }


    private fun setUpGoogleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("901780403692-v39fpjhl0hj5rpur16nadpeemee34psf.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this.requireActivity(), gso)
        binding.btnGoogle.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, REQUEST_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val googleSignInAccount = task.getResult(ApiException::class.java)
                googleSignInAccount?.let { getGoogleAuthCredential(it) }
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getGoogleAuthCredential(googleSignInAccount: GoogleSignInAccount) {
        val googleTokenId = googleSignInAccount.idToken
        val googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null)
        authViewModel.firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener {
            task ->
            if (task.isSuccessful) {
                val user = authViewModel.firebaseAuth.currentUser
                authViewModel.userLiveData.postValue(user)
                authViewModel.toastMessage.postValue("Login Success")
            } else {
                authViewModel.toastMessage.postValue("Login Failure: " + task.exception)
            }
        }
    }


    //-------------------------------
    private fun loginWithFacebook(){
        LoginManager.getInstance()
            .logInWithReadPermissions(this.requireActivity(), listOf("email"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        authViewModel.firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = authViewModel.firebaseAuth.currentUser
                    authViewModel.userLiveData.postValue(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    authViewModel.toastMessage.postValue("Authentication failed.")
                }
            }
    }

    private fun validEmail(): String? {
        val emailText = binding.editTextEmail.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return "Invalid Email Address"
        }
        return null
    }

    private fun validPassword(): String? {
        val passwordText = binding.editTextPassword.text.toString()
        if (passwordText.length < 8) {
            return "Minimum 8 Character Password"
        }
        if (!passwordText.matches(".*[A-Z].*".toRegex())) {
            return "Must Contain 1 Upper-case Character"
        }
        if (!passwordText.matches(".*[a-z].*".toRegex())) {
            return "Must Contain 1 Lower-case Character"
        }
//        if (!passwordText.matches(".*[@#\$%^&+=].*".toRegex())) {
//            return "Must Contain 1 Special Character (@#\$%^&+=)"
//        }
        return null
    }

    private fun alertEmail() {
        val alert = validEmail()
        if (!alert.isNullOrEmpty()) {
            binding.txtLayoutEmail.isErrorEnabled = true
            binding.txtLayoutEmail.error = alert
            binding.txtLayoutEmail.endIconMode = TextInputLayout.END_ICON_NONE
        } else {
            binding.txtLayoutEmail.isErrorEnabled = false
            binding.txtLayoutEmail.endIconMode = TextInputLayout.END_ICON_CUSTOM

        }
    }

    private fun alertPassword() {
        val alert = validPassword()
        if (!alert.isNullOrEmpty()) {
            binding.txtLayoutPassword.isErrorEnabled = true
            binding.txtLayoutPassword.error = alert
        } else {
            binding.txtLayoutPassword.isErrorEnabled = false
        }
    }

}