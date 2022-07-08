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

    companion object {
        const val ID_PRODUCT = "idProduct"
        const val REQUEST_KEY = "request_key"
        const val BUNDLE_KEY_NAME = "bundle_name"
        const val BUNDLE_KEY_POSITION = "bundle_position"
        const val BUNDLE_KEY_MIN = "bundle_min"
        const val BUNDLE_KEY_MAX = "bundle_max"
        const val BUNDLE_KEY_IS_FAVORITE = "bundle_is_favorite"
        const val BUNDLE_KEY_NAME_PROMOTION = "bundle_name_promotion"
        const val BUNDLE_KEY_SALE = "bundle_sale"
        const val GRIDVIEW_SPAN_COUNT = 2
        const val NAME_CATEGORY = "nameCategories"
        const val NAME_PRODUCT = "nameProduct"
    }
}