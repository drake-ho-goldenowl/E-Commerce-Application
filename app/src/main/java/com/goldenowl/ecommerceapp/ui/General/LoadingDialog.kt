package com.goldenowl.ecommerceapp.ui.General

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.goldenowl.ecommerceapp.R
import javax.inject.Singleton

@Singleton
class LoadingDialog(private val fragment: Fragment) {
    private lateinit var isDialog: AlertDialog
    private val handlerFragment = Handler()

    fun startLoading() {
        dismiss()
        val inflater = fragment.layoutInflater
        val dialogView = inflater.inflate(R.layout.item_loading, null)

        val builder = AlertDialog.Builder(fragment.context)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        isDialog.show()
        handlerFragment.removeMessages(0)
        handlerFragment.postDelayed({
            Toast.makeText(fragment.context, "Something wrong", Toast.LENGTH_SHORT).show()
            dismiss()
        }, TIME_DELAY.toLong())
    }

    fun dismiss() {
        if (::isDialog.isInitialized) {
            isDialog.dismiss()
            handlerFragment.removeMessages(0)
        }
    }

    companion object {
        const val TIME_DELAY = 1000 * 10
    }
}