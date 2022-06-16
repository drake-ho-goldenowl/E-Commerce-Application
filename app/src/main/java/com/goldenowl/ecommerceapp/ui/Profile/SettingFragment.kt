package com.goldenowl.ecommerceapp.ui.Profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentSettingBinding
import com.goldenowl.ecommerceapp.ui.DatePickerFragment
import com.goldenowl.ecommerceapp.viewmodels.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private val viewModel: SettingViewModel by viewModels()
    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        bind()
        observeSetup()
        return binding.root
    }

    private fun bind() {
        binding.apply {
            appBarLayout.topAppBar.title = "Setting"
            editTextFullName.setText(viewModel.userManager.getName())
            editTextDateOfBirth.setText(viewModel.userManager.getDOB())
            Glide.with(this@SettingFragment)
                .load(viewModel.userManager.getAvatar())
                .error(R.drawable.ic_no_login)
                .into(binding.imgAvatar)

            if (viewModel.checkLoginWithFbOrGoogle()) {
                txtChange.visibility = View.GONE
            } else {
                txtChange.visibility = View.VISIBLE
            }

            editTextFullName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    viewModel.validName(editTextFullName.text.toString())
                }
            })

            editTextFullName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    viewModel.updateName(
                        editTextFullName.text.toString()
                    )
                }
            }

            editTextDateOfBirth.setOnClickListener {
                val newFragment =
                    DatePickerFragment(editTextDateOfBirth, viewModel.userManager.getDOB())
                newFragment.show(parentFragmentManager, "datePicker")
            }

            editTextDateOfBirth.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    viewModel.updateDOB(editTextDateOfBirth.text.toString())
                }

            })
            txtChangeAvatar.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 456)
            }
            txtChange.setOnClickListener {
                val modalBottomSheet = BottomSheetChangePassword()
                modalBottomSheet.show(parentFragmentManager, BottomSheetChangePassword.TAG)
            }

            binding.appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

        }
    }

    private fun observeSetup() {
        viewModel.validNameLiveData.observe(viewLifecycleOwner) {
            alertName(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            val filePath = data.data
            binding.imgAvatar.setImageURI(filePath)
            viewModel.uploadImage(filePath, viewModel.userManager.getAccessToken())
        }
    }

    private fun alertName(alert: String) {
        binding.apply {
            if (!alert.isNullOrEmpty()) {
                txtLayoutFullName.isErrorEnabled = true
                txtLayoutFullName.error = alert
            } else {
                txtLayoutFullName.isErrorEnabled = false
            }
        }

    }

    companion object {
        const val PICK_IMAGE_REQUEST = 456
    }
}