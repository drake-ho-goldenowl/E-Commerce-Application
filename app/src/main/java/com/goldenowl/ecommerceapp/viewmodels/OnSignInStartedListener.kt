package com.goldenowl.ecommerceapp.viewmodels

import com.google.android.gms.auth.api.signin.GoogleSignInClient

interface OnSignInStartedListener {
    fun onSignInStarted(client: GoogleSignInClient?)
}