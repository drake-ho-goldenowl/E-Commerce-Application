package com.goldenowl.ecommerceapp

import android.app.Application
import com.goldenowl.ecommerceapp.data.AppDatabase

class EcommerceApplication : Application(){
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}