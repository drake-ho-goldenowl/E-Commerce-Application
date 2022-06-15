package com.goldenowl.ecommerceapp.data

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class UserManager(context: Context) {
    private val accountManager: AccountManager = AccountManager.get(context)
    private val db = FirebaseFirestore.getInstance()

//    fun addAccount(
//        name: String,
//        email: String,
//        password: String,
//        access_token: String,
//        dob: String = "",
//        avatar: String = "",
//    ) {
//        val data = Bundle()
//            .apply {
//                this.putString(NAME, name)
//                this.putString(EMAIL, email)
//                this.putString(PASSWORD, password)
//                this.putString(DOB, dob)
//                this.putString(TOKEN, access_token)
//                this.putString(AVATAR, avatar)
//            }
//        val account = Account(email, ACCOUNT_TYPE)
//        accountManager.addAccountExplicitly(account, access_token, data)
//        accountManager.setAuthToken(account, AUTH_TOKEN_TYPE, access_token)
//    }

    fun addAccount(
        user: User
    ) {
        val data = Bundle()
            .apply {
                this.putString(NAME, user.name)
                this.putString(EMAIL, user.email)
                this.putString(PASSWORD, user.password)
                this.putString(DOB, user.dob)
                this.putString(TOKEN, user.token)
                this.putString(AVATAR, user.avatar)
            }
        val account = Account(user.email, ACCOUNT_TYPE)
        accountManager.addAccountExplicitly(account, user.token, data)
        accountManager.setAuthToken(account, AUTH_TOKEN_TYPE, user.token)
    }

    private fun getAccount(): Account? {
        return if (accountManager.getAccountsByType(ACCOUNT_TYPE).isNotEmpty()) {
            accountManager.getAccountsByType(ACCOUNT_TYPE)[0]
        } else {
            null
        }
    }

    fun getUser(): User {
        return User(
            getName(),
            getEmail(),
            getPassword(),
            getAccessToken(),
            getDOB(),
            getAvatar(),
        )
    }

    fun isLogged(): Boolean {
        val accounts = accountManager.getAccountsByType(ACCOUNT_TYPE)
        if (accounts.isNotEmpty()) {
            return true
        }
        return false
    }

    fun logOut() {
        if (getAccount() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                accountManager.removeAccountExplicitly(getAccount())
            }
        }
    }

    fun getAccessToken(): String {
        return accountManager.getUserData(getAccount(), TOKEN) ?: ""
    }

    fun getEmail(): String {
        return accountManager.getUserData(getAccount(), EMAIL)
    }

    fun getName(): String {
        return accountManager.getUserData(getAccount(), NAME)
    }

    fun getPassword(): String {
        return accountManager.getUserData(getAccount(), PASSWORD)
    }

    fun getDOB(): String {
        return accountManager.getUserData(getAccount(), DOB)
    }

    fun getAvatar(): String {
        return accountManager.getUserData(getAccount(), AVATAR)?: ""
    }

    fun setName(name: String) {
        accountManager.setUserData(getAccount(), NAME, name)
    }

    fun setPassword(password: String) {
        accountManager.setUserData(getAccount(), PASSWORD, password)
    }

    fun setDOB(dob: String) {
        accountManager.setUserData(getAccount(), DOB, dob)
    }

    fun setAvatar(avatar: String) {
        accountManager.setUserData(getAccount(), AVATAR, avatar)
    }

    fun writeProfile(user: User) {
        db.collection("users").document(user.token)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    companion object {
        const val AUTH_TOKEN_TYPE = "com.goldenowl.ecommerceapp"
        const val ACCOUNT_TYPE = "com.goldenowl.ecommerceapp"
        const val TOKEN = "access_token"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val NAME = "name"
        const val DOB = "Date_of_birth"
        const val AVATAR = "avatar"
        const val TAG = "USER_MANAGER"
        @Volatile
        private var instance: UserManager? = null

        fun getInstance(context: Context): UserManager {
            return instance ?: synchronized(this) {
                instance ?: UserManager(context)
            }
        }
    }
}