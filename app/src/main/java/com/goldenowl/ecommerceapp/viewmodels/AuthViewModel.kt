package com.goldenowl.ecommerceapp.viewmodels

import android.app.Application
import android.content.ContentValues
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.goldenowl.ecommerceapp.data.User
import com.goldenowl.ecommerceapp.data.UserManager
import com.goldenowl.ecommerceapp.utilities.Hash
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class AuthViewModel(application: Application, private val listener: OnSignInStartedListener) :
    BaseViewModel(application) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val userLiveData: MutableLiveData<FirebaseUser?> = MutableLiveData()
    val validNameLiveData: MutableLiveData<String> = MutableLiveData()
    val validEmailLiveData: MutableLiveData<String> = MutableLiveData()
    val validPasswordLiveData: MutableLiveData<String> = MutableLiveData()
    private val googleSignInClient: GoogleSignInClient
    val callbackManager = CallbackManager.Factory.create()
    private val userManager: UserManager
    private val db = Firebase.firestore


    init {
        if (firebaseAuth.currentUser != null) {
            userLiveData.postValue(firebaseAuth.currentUser)
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("901780403692-v39fpjhl0hj5rpur16nadpeemee34psf.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(application, gso)
        userManager = UserManager.getInstance(application)
    }

    fun signUp(name: String, email: String, password: String) {
        if (!validName(name) || !validEmail(email) || !validPassword(password)) {
            return
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let {
                        userLiveData.postValue(user)
                        val dob = Date()
                        val format = SimpleDateFormat("dd/MM/yyyy")

                        val account = User(
                            name,
                            email,
                            Hash.hashSHA256(password),
                            user.uid,
                            format.format(dob),
                            ""
                        )
                        userManager.addAccount(account)
                        userManager.writeProfile(account)
                        toastMessage.postValue("Registration Success")
                    }
                } else {
                    toastMessage.postValue("Registration Failure: " + task.exception)
                }
            }
    }

    fun forgotPassword(emailText: String) {
        if (emailText.isNullOrBlank()) {
            toastMessage.postValue("Please enter email")
        } else {
            firebaseAuth.sendPasswordResetEmail(emailText)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toastMessage.postValue("Email sent.")
                    }
                }
        }
    }

    fun logIn(email: String, password: String) {
        if (!validEmail(email) || !validPasswordLogin(password)) {
            return
        }
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    userLiveData.postValue(user)
                    toastMessage.postValue("Login Success")
                    user?.let {
                        db.collection("users").document(it.uid).get()
                            .addOnSuccessListener { documentSnapshot ->
                                val account = documentSnapshot.toObject<User>()
                                account?.let {
                                    if (account.password != Hash.hashSHA256(password)) {
                                        account.password = Hash.hashSHA256(password)
                                        userManager.writeProfile(account)
                                    }
                                    userManager.addAccount(account)
                                }
                            }
                    }
                } else {
                    toastMessage.postValue("Login Failure: " + task.exception)
                }
            }
    }

    fun logOut() {
        firebaseAuth.signOut()
        userLiveData.postValue(null)
    }

    fun signInWithGoogle() {
        listener.onSignInStarted(googleSignInClient)
    }

    // GOOGLE
    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = firebaseAuth.currentUser
                userLiveData.postValue(user)
                toastMessage.postValue("Login Success")
                db.collection("users").document(user!!.uid).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documentSnapshot = task.result
                        if (documentSnapshot.exists()) {
                            val account = documentSnapshot.toObject<User>()
                            account?.let {
                                userManager.addAccount(account)
                            }
                        } else {
                            createNewUserManagerForLoginSocial(user)
                        }
                    }
                }
            } else {
                toastMessage.postValue("Login Fail")
            }
        }
    }

    // FaceBook
    fun loginWithFacebook() {
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d(ContentValues.TAG, "facebook:onSuccess:$loginResult")
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.d(ContentValues.TAG, "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d(ContentValues.TAG, "facebook:onError", error)
                }
            })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(ContentValues.TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    userLiveData.postValue(user)
                    db.collection("users").document(user!!.uid).get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val documentSnapshot = task.result
                                if (documentSnapshot.exists()) {
                                    val account = documentSnapshot.toObject<User>()
                                    account?.let {
                                        userManager.addAccount(account)
                                    }
                                } else {
                                    createNewUserManagerForLoginSocial(user)
                                }
                            }
                        }


                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    toastMessage.postValue("Authentication failed.")
                }
            }
    }


    private fun createNewUserManagerForLoginSocial(user: FirebaseUser) {
        val dob = Date()
        val format = SimpleDateFormat("dd/MM/yyyy")
        val account = User(
            user.displayName.toString(),
            user.email.toString(),
            "",
            user.uid,
            format.format(dob),
            user.photoUrl.toString()
        )
        userManager.addAccount(account)
        userManager.writeProfile(account)
    }

    private fun isLettersOrDigit(string: String): Boolean {
        return string.filter { it.isLetterOrDigit() }.length == string.length
    }

    fun validName(nameText: String): Boolean {
        return when {
            nameText.isBlank() -> {
                validNameLiveData.postValue("Mustn't empty")
                false
            }
            !isLettersOrDigit(nameText) -> {
                validNameLiveData.postValue("Mustn't Contain Special Character")
                false
            }
            else -> {
                validNameLiveData.postValue("")
                true
            }
        }
    }

    fun validEmail(emailText: String): Boolean {
        return if (emailText.isBlank()) {
            validEmailLiveData.postValue("Mustn't empty")
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            validEmailLiveData.postValue("Invalid Email Address")
            false
        } else {
            validEmailLiveData.postValue("")
            true
        }
    }

    fun validPassword(passwordText: String): Boolean {
        if (passwordText.length < 8) {
            validPasswordLiveData.postValue("Minimum 8 Character Password")
            return false
        } else if (!passwordText.matches(".*[A-Z].*".toRegex())) {
            validPasswordLiveData.postValue("Must Contain 1 Upper-case Character")
            return false
        } else if (!passwordText.matches(".*[a-z].*".toRegex())) {
            validPasswordLiveData.postValue("Must Contain 1 Lower-case Character")
            return false
        }
//        if (!passwordText.matches(".*[@#\$%^&+=].*".toRegex())) {
//            return "Must Contain 1 Special Character (@#\$%^&+=)"
//        }
        else {
            validPasswordLiveData.postValue("")
            return true
        }
    }

    private fun validPasswordLogin(passwordText: String): Boolean {
        return if (passwordText.isEmpty()) {
            validPasswordLiveData.postValue("Mustn't empty")
            false
        } else {
            validPasswordLiveData.postValue("")
            true
        }
    }

}