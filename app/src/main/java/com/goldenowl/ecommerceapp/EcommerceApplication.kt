package com.goldenowl.ecommerceapp

import android.app.Application
import com.goldenowl.ecommerceapp.data.AppDatabase
import com.goldenowl.ecommerceapp.data.UserManager

class EcommerceApplication : Application(){
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val userManager: UserManager by lazy { UserManager.getInstance(this) }
}