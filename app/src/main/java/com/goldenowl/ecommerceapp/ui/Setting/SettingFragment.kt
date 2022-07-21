package com.goldenowl.ecommerceapp.ui.Setting

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
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentSettingBinding
import com.goldenowl.ecommerceapp.ui.ChangePassword.BottomSheetChangePassword
import com.goldenowl.ecommerceapp.ui.General.DatePickerFragment
import com.goldenowl.ecommerceapp.utilities.GlideDefault
import com.goldenowl.ecommerceapp.utilities.REQUEST_PICK_IMAGE
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
            appBarLayout.topAppBar.title = getString(R.string.setting)
            editTextFullName.setText(viewModel.userManager.getName())
            editTextDateOfBirth.setText(viewModel.userManager.getDOB())

            GlideDefault.userImage(
                requireContext(),
                viewModel.userManager.getAvatar(),
                binding.imgAvatar
            )

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
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            val filePath = data.data
            binding.imgAvatar.setImageURI(filePath)
            viewModel.uploadImage(filePath, viewModel.userManager.getAccessToken())
            GlideDefault.userImage(
                requireContext(),
                data.data.toString(),
                binding.imgAvatar
            )
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
}