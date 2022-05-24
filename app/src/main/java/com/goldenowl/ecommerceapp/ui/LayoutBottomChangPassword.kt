package com.goldenowl.ecommerceapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.goldenowl.ecommerceapp.databinding.BottomLayoutChangePasswordBinding
import com.goldenowl.ecommerceapp.viewmodels.ChangePasswordViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModalBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: BottomLayoutChangePasswordBinding
    private lateinit var viewModel: ChangePasswordViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomLayoutChangePasswordBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ChangePasswordViewModel::class.java)

        observeSetup()

        binding.btnSavePassword.setOnClickListener {
            viewModel.checkOldPassword(binding.editTextOldPassword.text.toString())
            viewModel.validPassword(
                binding.editTextNewPassword.text.toString(),
                binding.editTextOldPassword.text.toString()
            )
            viewModel.checkRepeatPassword(
                binding.editTextRepeatNewPassword.text.toString(),
                binding.editTextNewPassword.text.toString()
            )
            if (!binding.txtLayoutOldPassword.isErrorEnabled && !binding.txtLayoutNewPassword.isErrorEnabled &&
                !binding.txtLayoutRepeatNewPassword.isErrorEnabled
            ) {
                viewModel.changePassword(
                    binding.editTextNewPassword.toString(),
                    binding.editTextOldPassword.text.toString()
                )
            }
        }

        binding.txtForgotPassword.setOnClickListener {
            viewModel.forgotPassword()
        }

        binding.editTextOldPassword.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                viewModel.checkOldPassword(binding.editTextOldPassword.text.toString())
            }
        }

        binding.editTextNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.validPassword(
                    binding.editTextNewPassword.text.toString(),
                    binding.editTextOldPassword.text.toString()
                )

            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.editTextRepeatNewPassword.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                viewModel.checkRepeatPassword(
                    binding.editTextRepeatNewPassword.text.toString(),
                    binding.editTextNewPassword.text.toString()
                )
            }
        }
        return binding.root
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