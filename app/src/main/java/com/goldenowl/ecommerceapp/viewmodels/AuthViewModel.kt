package com.goldenowl.ecommerceapp.viewmodels

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
import com.goldenowl.ecommerceapp.utilities.LAST_EDIT_TIME_BAG
import com.goldenowl.ecommerceapp.utilities.LAST_EDIT_TIME_FAVORITES
import com.goldenowl.ecommerceapp.utilities.USER_FIREBASE
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userManager: UserManager,
    private val googleSignInClient: GoogleSignInClient,
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) :
    BaseViewModel() {
    val userLiveData: MutableLiveData<FirebaseUser?> = MutableLiveData()
    val validNameLiveData: MutableLiveData<String> = MutableLiveData()
    val validEmailLiveData: MutableLiveData<String> = MutableLiveData()
    val validPasswordLiveData: MutableLiveData<String> = MutableLiveData()
    val callbackManager = CallbackManager.Factory.create()

    init {
        if (firebaseAuth.currentUser != null) {
            userLiveData.postValue(firebaseAuth.currentUser)
        }
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
                        userManager.writeProfile(db, account)
                        toastMessage.postValue(REGISTRATION_SUCCESS)
                    }
                } else {
                    toastMessage.postValue(REGISTRATION_FAIL)
                }
            }
    }

    fun forgotPassword(emailText: String) {
        if (emailText.isBlank()) {
            toastMessage.postValue("Please enter email")
        } else {
            firebaseAuth.sendPasswordResetEmail(emailText)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toastMessage.postValue("Email sent.")
                    }
                    else{
                        toastMessage.postValue("Email invalid")
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
                    user?.let {
                        actionLoginOrCreateFirebase(user, password)
                    }
                } else {
                    toastMessage.postValue("Login Failure: " + task.exception)
                }
            }
    }

    //If function hasn't password parameter mean login with social
    private fun actionLoginOrCreateFirebase(user: FirebaseUser, password: String?) {
        db.collection(USER_FIREBASE).document(user.uid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documentSnapshot = task.result
                if (documentSnapshot.exists()) {
                    val account = documentSnapshot.toObject<User>()
                    account?.let {
                        if (password != null && account.password != Hash.hashSHA256(password)) {
                            account.password = Hash.hashSHA256(password)
                            userManager.writeProfile(db, account)
                        }
                        userManager.addAccount(account)
                    }
                } else {
                    if (password == null) {
                        createNewUserManagerForLoginSocial(user)
                    }
                }
                userLiveData.postValue(user)
                toastMessage.postValue(LOGIN_SUCCESS)
            } else {
                toastMessage.postValue(LOGIN_FAIL)
            }
        }

    }

    fun isLogged(): Boolean {
        return userManager.isLogged()
    }

    fun logOut() {
        firebaseAuth.signOut()
        userManager.logOut()
        LAST_EDIT_TIME_FAVORITES = null
        LAST_EDIT_TIME_BAG = null
        userLiveData.postValue(null)
    }

    fun signInWithGoogle(listener: OnSignInStartedListener) {
        listener.onSignInStarted(googleSignInClient)
    }

    // GOOGLE
    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = firebaseAuth.currentUser
                user?.let {
                    actionLoginOrCreateFirebase(user, null)
                }
            } else {
                toastMessage.postValue(LOGIN_FAIL)
            }
        }
    }

    // FaceBook
    fun loginWithFacebook() {
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    Log.d(ContentValues.TAG, "facebook:onSuccess:$result")
                    handleFacebookAccessToken(result.accessToken)
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
                    user?.let {
                        actionLoginOrCreateFirebase(user, null)
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
        userManager.writeProfile(db, account)
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

    companion object {
        const val LOGIN_SUCCESS = "Login Success"
        const val LOGIN_FAIL = "Login Fail"
        const val REGISTRATION_SUCCESS = "Registration Success"
        const val REGISTRATION_FAIL = "Registration Fail"
    }
}