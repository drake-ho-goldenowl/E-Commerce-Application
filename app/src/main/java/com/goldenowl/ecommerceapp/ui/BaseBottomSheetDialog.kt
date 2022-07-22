package com.goldenowl.ecommerceapp.ui

import android.widget.Toast
import com.goldenowl.ecommerceapp.ui.General.LoadingDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class BaseBottomSheetDialog : BottomSheetDialogFragment() {
    private val loadingDialog = LoadingDialog(this)

    fun toastMessage(string: String) {
        if (string.isNotBlank()) {
            Toast.makeText(
                activity,
                string,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            loadingDialog.startLoading()
        } else {
            loadingDialog.dismiss()
        }
    }

    companion object{
        const val GRIDVIEW_SPAN_COUNT = 3
        const val WARNING_SELECT_SIZE = "Please select size"
        const val WARNING_SELECT_COLOR = "Please select color"
    }
}