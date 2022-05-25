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
import com.goldenowl.ecommerceapp.databinding.ActivitySettingBinding
import com.goldenowl.ecommerceapp.model.UserManager
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

        binding.editTextFullName.setText(userManager.getName())
        binding.editTextDateOfBirth.setText(userManager.getDOB())

        if(viewModel.checkLoginWithFbOrGoogle()){
            binding.txtChange.visibility = View.GONE
        }
        else{
            binding.txtChange.visibility = View.VISIBLE
        }
        Glide.with(this)
            .load(userManager.getAvatar())
            .error(R.drawable.ic_no_login)
            .into(binding.imgAvatar)

        binding.editTextFullName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.validName(binding.editTextFullName.text.toString())
            }
        })

        binding.editTextFullName.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!binding.txtLayoutFullName.isErrorEnabled) {
                    viewModel.updateName(
                        binding.editTextFullName.text.toString()
                    )
                }
            }
        }

        binding.editTextDateOfBirth.setOnClickListener {
            val newFragment = DatePickerFragment(binding.editTextDateOfBirth, userManager.getDOB())
            newFragment.show(supportFragmentManager, "datePicker")
        }

        binding.editTextDateOfBirth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.updateDOB(binding.editTextDateOfBirth.text.toString())
            }

        })
        binding.txtChangeAvatar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 456)
        }
        binding.txtChange.setOnClickListener {
            val modalBottomSheet = ModalBottomSheet()
            modalBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)
        }
        observeSetup()
        setContentView(binding.root)
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
        if (!alert.isNullOrEmpty()) {
            binding.txtLayoutFullName.isErrorEnabled = true
            binding.txtLayoutFullName.error = alert
        } else {
            binding.txtLayoutFullName.isErrorEnabled = false
        }
    }

    companion object {
        const val PICK_IMAGE_REQUEST = 456
    }
}