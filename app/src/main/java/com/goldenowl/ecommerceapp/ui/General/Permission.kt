package com.goldenowl.ecommerceapp.ui.General

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class Permission(
    private val fragment: Fragment,
    private val grantedOnClick: () -> Unit,
    private val rationaleOnClick: () -> Unit
) {
    private val request: ActivityResultLauncher<String> = fragment.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            grantedOnClick()
        }
    }

    fun check(typePermission: TypePermission) {
        when {
            ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                typePermission.value
            ) == PackageManager.PERMISSION_GRANTED -> {
                grantedOnClick()
            }
            fragment.shouldShowRequestPermissionRationale(typePermission.value) -> {
                rationaleOnClick()
            }
            else -> {
                request.launch(typePermission.value)
            }
        }
    }

}

enum class TypePermission(val value: String) {
    GALLERY(Manifest.permission.READ_EXTERNAL_STORAGE),
    CAMERA(Manifest.permission.CAMERA)
}