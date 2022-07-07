package com.goldenowl.ecommerceapp.ui

import android.widget.Toast
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseFragment : Fragment() {
    fun toastMessage(string: String) {
        Toast.makeText(
            this.context,
            string,
            Toast.LENGTH_SHORT
        ).show()
    }
}