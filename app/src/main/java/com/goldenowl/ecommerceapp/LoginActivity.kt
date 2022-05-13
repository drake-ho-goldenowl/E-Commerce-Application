package com.goldenowl.ecommerceapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

class LoginActivity : AppCompatActivity(R.layout.activity_login){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment_log)
                as NavHostFragment? ?: return
    }
}