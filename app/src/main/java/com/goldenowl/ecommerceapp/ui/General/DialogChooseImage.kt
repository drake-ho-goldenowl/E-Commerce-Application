package com.goldenowl.ecommerceapp.ui.General

import android.app.AlertDialog
import androidx.fragment.app.Fragment

class DialogChooseImage(
    private val fragment: Fragment,
    private val requestCamera: Permission,
    private val requestStore: Permission
) {
    fun show() {
        val options = arrayOf<CharSequence>(TAKE_PHOTO, GALLERY, CANCEL)
        val builder = AlertDialog.Builder(fragment.context)
        builder.setTitle(TITLE)
        builder.setItems(options) { dialog, item ->
            if (options[item] == TAKE_PHOTO) {
                requestCamera.check(TypePermission.CAMERA)
                dialog.dismiss()
            } else if (options[item] == GALLERY) {
                requestStore.check(TypePermission.GALLERY)
                dialog.dismiss()
            } else if (options[item] == CANCEL) {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    companion object {
        const val TITLE = "Select Option"
        const val TAKE_PHOTO = "Take Photo"
        const val GALLERY = "Choose From Gallery"
        const val CANCEL = "Cancel"
    }
}