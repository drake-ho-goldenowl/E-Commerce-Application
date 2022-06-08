package com.goldenowl.ecommerceapp.ui.Auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.goldenowl.ecommerceapp.R

class AuthActivity : AppCompatActivity(R.layout.activity_auth) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment_log)
                as NavHostFragment? ?: return
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }
}