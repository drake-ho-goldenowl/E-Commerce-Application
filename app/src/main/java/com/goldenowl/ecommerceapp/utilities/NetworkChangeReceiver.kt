package com.goldenowl.ecommerceapp.utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.goldenowl.ecommerceapp.NoInternetActivity

class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            if (!NetworkHelper.isNetworkAvailable(it)) {
                if (it.toString().contains(AUTH_ACTIVITY)) {
                    val intentTemp = Intent(it, NoInternetActivity::class.java)
                    intentTemp.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    it.startActivity(intentTemp)
                } else {
                    Toast.makeText(it, WARNING, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        const val AUTH_ACTIVITY = "AuthActivity"
    }
}