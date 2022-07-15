package com.goldenowl.ecommerceapp.ui.General

import android.app.AlertDialog
import androidx.fragment.app.Fragment


class ConfirmDialog(
    private val fragment: Fragment,
    private val onYesClick: () -> Unit
) {
    fun show() {
        val builder = AlertDialog.Builder(fragment.context)

        builder.setTitle(TITLE)
        builder.setMessage(MESSAGE)
        builder.setPositiveButton(
            YES
        ) { dialog, _ ->
            onYesClick()
            dialog.dismiss()
        }

        builder.setNegativeButton(
            NO
        ) { dialog, _ ->
            dialog.dismiss()
        }

        val alert: AlertDialog = builder.create()
        alert.show()
    }

    companion object {
        const val TITLE = "Confirm"
        const val MESSAGE = "Do you want to delete?"
        const val YES = "YES"
        const val NO = "NO"
    }
}