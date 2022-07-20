package com.goldenowl.ecommerceapp.ui.ChangePassword

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.BottomLayoutChangePasswordBinding
import com.goldenowl.ecommerceapp.ui.BaseBottomSheetDialog
import com.goldenowl.ecommerceapp.utilities.Notification
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetChangePassword : BaseBottomSheetDialog() {
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
        viewModel.apply {
            validOldPasswordLiveData.observe(viewLifecycleOwner) {
                alertOldPassword(it)
            }
            validNewPasswordLiveData.observe(viewLifecycleOwner) {
                alertNewPassword(it)
            }
            validRepeatPasswordLiveData.observe(viewLifecycleOwner) {
                alertRepeatPassword(it)
            }
            toastMessage.observe(viewLifecycleOwner) { str ->
                toastMessage(str)
            }

            validChangePasswordLiveData.observe(viewLifecycleOwner) {
                if (it) {
                    Notification(requireContext()).notify(
                        getString(R.string.notification), getString(
                            R.string.update_password_success
                        )
                    )
                    dismiss()
                }
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