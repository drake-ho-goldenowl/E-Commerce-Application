package com.goldenowl.ecommerceapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.goldenowl.ecommerceapp.data.UserManager
import com.goldenowl.ecommerceapp.databinding.ActivitySettingBinding
import com.goldenowl.ecommerceapp.ui.DatePickerFragment
import com.goldenowl.ecommerceapp.ui.ModalBottomSheet
import com.goldenowl.ecommerceapp.viewmodels.SettingViewModel


class SettingActivity : AppCompatActivity() {
    private lateinit var viewModel: SettingViewModel
    private lateinit var binding: ActivitySettingBinding
    private lateinit var userManager: UserManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        userManager = UserManager.getInstance(this)
        viewModel = ViewModelProvider(this).get(SettingViewModel::class.java)
        Glide.with(this)
            .load(userManager.getAvatar())
            .error(R.drawable.ic_no_login)
            .into(binding.imgAvatar)
        bind()
        observeSetup()
        setContentView(binding.root)
    }

    private fun bind() {
        binding.apply {
            editTextFullName.setText(userManager.getName())
            editTextDateOfBirth.setText(userManager.getDOB())

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

            editTextFullName.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    viewModel.updateName(
                        editTextFullName.text.toString()
                    )
                }
            }

            editTextDateOfBirth.setOnClickListener {
                val newFragment = DatePickerFragment(editTextDateOfBirth, userManager.getDOB())
                newFragment.show(supportFragmentManager, "datePicker")
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
                val modalBottomSheet = ModalBottomSheet()
                modalBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)
            }

        }
    }

    private fun observeSetup() {
        viewModel.validNameLiveData.observe(this) {
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
            viewModel.uploadImage(filePath, userManager.getAccessToken())
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