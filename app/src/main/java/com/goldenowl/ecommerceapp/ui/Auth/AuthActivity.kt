package com.goldenowl.ecommerceapp.ui.Auth

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.MainActivity
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.utilities.NetworkChangeReceiver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity(R.layout.activity_auth) {
    private lateinit var host: NavHostFragment
    private val br: BroadcastReceiver = NetworkChangeReceiver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        host = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_log)
                as NavHostFragment? ?: return

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION).apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        }
        registerReceiver(br, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(br)
    }

    override fun onBackPressed() {
        if (host.findNavController().currentDestination?.id == R.id.signUpFragment) {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        } else {
            super.onBackPressed()
        }
    }
}