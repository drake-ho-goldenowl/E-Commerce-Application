package com.goldenowl.ecommerceapp.ui

import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.R
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

    fun touchImage(images: List<String>,position: Int = 0){
        findNavController().navigate(
            R.id.largeImageFragment,
            bundleOf(
                URL_IMAGE to images.joinToString("`"),
                BUNDLE_KEY_POSITION to position
            )
        )
    }

    companion object {
        const val ID_PRODUCT = "idProduct"
        const val ID_ADDRESS = "idAddress"
        const val ID_PROMOTION = "idPromotion"
        const val URL_IMAGE= "urlImage"

        const val REQUEST_KEY = "request_key"
        const val BUNDLE_KEY_NAME = "bundle_name"
        const val BUNDLE_KEY_POSITION = "bundle_position"
        const val BUNDLE_KEY_MIN = "bundle_min"
        const val BUNDLE_KEY_MAX = "bundle_max"
        const val BUNDLE_KEY_IS_FAVORITE = "bundle_is_favorite"
        const val BUNDLE_KEY_NAME_COUNTRY = "bundle_name_country"

        const val GRIDVIEW_SPAN_COUNT = 2
        const val NAME_CATEGORY = "nameCategories"
        const val NAME_PRODUCT = "nameProduct"
    }
}