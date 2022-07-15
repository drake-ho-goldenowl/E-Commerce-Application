package com.goldenowl.ecommerceapp.ui.General

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.goldenowl.ecommerceapp.MainActivity
import com.goldenowl.ecommerceapp.databinding.ActivityNoInternetBinding
import com.goldenowl.ecommerceapp.ui.Auth.AuthActivity
import com.goldenowl.ecommerceapp.utilities.NetworkHelper

class NoInternetActivity : AppCompatActivity() {
    private lateinit var binding : ActivityNoInternetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoInternetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTryAgain.setOnClickListener {
            if(NetworkHelper.isNetworkAvailable(this)){
                val intent = Intent(this, AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            else{
                Toast.makeText(this, WARNING, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    companion object{
        const val WARNING = "Please check again"
    }
}