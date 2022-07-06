package com.goldenowl.ecommerceapp.ui.Profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.goldenowl.ecommerceapp.Notification
import com.goldenowl.ecommerceapp.databinding.BottomLayoutChangePasswordBinding
import com.goldenowl.ecommerceapp.viewmodels.ChangePasswordViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetChangePassword : BottomSheetDialogFragment() {
    private lateinit var binding: BottomLayoutChangePasswordBinding
    private val viewModel: ChangePasswordViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomLayoutChangePasswordBinding.inflate(inflater, container, false)

        observeSetup()
        bind()
        return binding.root
    }

    private fun bind() {
        binding.apply {
            btnSavePassword.setOnClickListener {
                viewModel.changePassword(
                    editTextNewPassword.text.toString(),
                    editTextRepeatNewPassword.text.toString(),
                    editTextOldPassword.text.toString()
                )
            }

            txtForgotPassword.setOnClickListener {
                viewModel.forgotPassword()
            }

            editTextOldPassword.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    viewModel.checkOldPassword(editTextOldPassword.text.toString())
                }
            }

            editTextNewPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    viewModel.validPassword(
                        editTextNewPassword.text.toString(),
                        editTextOldPassword.text.toString()
                    )

                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            editTextRepeatNewPassword.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    viewModel.checkRepeatPassword(
                        editTextRepeatNewPassword.text.toString(),
                        editTextNewPassword.text.toString()
                    )
                }
            }

        }

    }

    private fun observeSetup() {
        viewModel.validOldPasswordLiveData.observe(this) {
            alertOldPassword(it)
        }
        viewModel.validNewPasswordLiveData.observe(this) {
            alertNewPassword(it)
        }
        viewModel.validRepeatPasswordLiveData.observe(this) {
            alertRepeatPassword(it)
        }
        viewModel.toastMessage.observe(this.viewLifecycleOwner) { str ->
            Toast.makeText(
                this.context,
                str,
                Toast.LENGTH_SHORT
            ).show()
        }

        viewModel.validChangePasswordLiveData.observe(this) {
            if (it) {
                Notification(requireContext()).notify("Notification","Update password success")
                this.dismiss()
            }
        }
    }

    private fun alertOldPassword(alert: String) {
        if (!alert.isNullOrEmpty()) {
            binding.txtLayoutOldPassword.isErrorEnabled = true
            binding.txtLayoutOldPassword.error = alert
        } else {
            binding.txtLayoutOldPassword.isErrorEnabled = false
        }
    }

    private fun alertNewPassword(alert: String) {
        if (!alert.isNullOrEmpty()) {
            binding.txtLayoutNewPassword.isErrorEnabled = true
            binding.txtLayoutNewPassword.error = alert
        } else {
            binding.txtLayoutNewPassword.isErrorEnabled = false
        }
    }

    private fun alertRepeatPassword(alert: String) {
        if (!alert.isNullOrEmpty()) {
            binding.txtLayoutRepeatNewPassword.isErrorEnabled = true
            binding.txtLayoutRepeatNewPassword.error = alert
        } else {
            binding.txtLayoutRepeatNewPassword.isErrorEnabled = false
        }
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}