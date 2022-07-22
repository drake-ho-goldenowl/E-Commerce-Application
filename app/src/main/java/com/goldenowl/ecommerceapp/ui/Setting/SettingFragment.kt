package com.goldenowl.ecommerceapp.ui.Setting

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.goldenowl.ecommerceapp.ui.General.DialogChooseImage
import com.goldenowl.ecommerceapp.ui.General.Permission
import com.goldenowl.ecommerceapp.ui.ReviewRating.BottomAddReview
import com.goldenowl.ecommerceapp.utilities.FileUtil
import com.goldenowl.ecommerceapp.utilities.GlideDefault
import com.goldenowl.ecommerceapp.utilities.REQUEST_CAMERA
import com.goldenowl.ecommerceapp.utilities.REQUEST_PICK_IMAGE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private val viewModel: SettingViewModel by viewModels()
    private lateinit var binding: FragmentSettingBinding
    private lateinit var requestStore: Permission
    private lateinit var requestCamera: Permission

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestStore = Permission(this, {
            val pickPhoto = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(pickPhoto, REQUEST_PICK_IMAGE);
        }, {})

        requestCamera = Permission(this, {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, REQUEST_CAMERA)
        }, {})
    }

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
                DialogChooseImage(this@SettingFragment, requestCamera, requestStore).show()
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
        if (resultCode == Activity.RESULT_OK){
            when (requestCode) {
                REQUEST_PICK_IMAGE -> {
                    selectCompressor(data)
                }
                REQUEST_CAMERA -> {
                    selectCompressor(data, true)
                }
            }
        }
    }

    private fun selectCompressor(data: Intent?, isCamera: Boolean = false) {
        if (data == null) {
            return
        }
        if (isCamera) {
            data.extras?.let { it ->
                FileUtil.getImageUri(
                    requireContext(),
                    it.get(BottomAddReview.DATA) as Bitmap
                )?.let {
                    loadImage(it)
                }
            }
        } else {
            val filePath = data.data
            filePath?.let {
                loadImage(it)
            }
        }
    }

    private fun loadImage(filePath: Uri) {
        binding.imgAvatar.setImageURI(filePath)
        viewModel.uploadImage(filePath, viewModel.userManager.getAccessToken())
        GlideDefault.userImage(
            requireContext(),
            filePath.toString(),
            binding.imgAvatar
        )
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