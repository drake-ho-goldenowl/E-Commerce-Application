package com.goldenowl.ecommerceapp.ui

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.goldenowl.ecommerceapp.ui.General.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseFragment : Fragment() {
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

    companion object {
        const val ID_PRODUCT = "idProduct"
        const val ID_ADDRESS = "idAddress"
        const val ID_PROMOTION = "idPromotion"

        const val REQUEST_KEY = "request_key"
        const val BUNDLE_KEY_NAME = "bundle_name"
        const val BUNDLE_KEY_POSITION = "bundle_position"
        const val BUNDLE_KEY_MIN = "bundle_min"
        const val BUNDLE_KEY_MAX = "bundle_max"
        const val BUNDLE_DISMISS = "dismiss"
        const val BUNDLE_KEY_IS_FAVORITE = "bundle_is_favorite"
        const val BUNDLE_KEY_NAME_COUNTRY = "bundle_name_country"

        const val GRIDVIEW_SPAN_COUNT = 2
        const val NAME_CATEGORY = "nameCategories"
        const val NAME_PRODUCT = "nameProduct"
    }
}