package com.goldenowl.ecommerceapp.ui.Splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.goldenowl.ecommerceapp.MainActivity
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.ui.Tutorial.TutorialActivity
import com.goldenowl.ecommerceapp.utilities.IS_FIRST

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return@postDelayed
            val isFirst = sharedPref.getBoolean(IS_FIRST, true);
            if (isFirst) {
                sharedPref.edit().putBoolean(IS_FIRST, false).apply()
                val intent = Intent(this, TutorialActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 500)
    }
}