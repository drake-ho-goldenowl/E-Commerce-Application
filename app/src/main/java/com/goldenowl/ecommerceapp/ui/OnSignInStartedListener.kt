package com.goldenowl.ecommerceapp.ui

import com.google.android.gms.auth.api.signin.GoogleSignInClient

interface OnSignInStartedListener {
    fun onSignInStarted(client: GoogleSignInClient?)
}