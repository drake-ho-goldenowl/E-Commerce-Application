package com.goldenowl.ecommerceapp.ui.General

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.Fragment
import com.goldenowl.ecommerceapp.R

class LoadingDialog(private val fragment: Fragment) {
    private lateinit var isDialog: AlertDialog
    fun startLoading() {
        val inflater = fragment.layoutInflater
        val dialogView = inflater.inflate(R.layout.item_loading, null)

        val builder = AlertDialog.Builder(fragment.context)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        isDialog.show()
    }

    fun dismiss() {
        isDialog.dismiss()
    }
}