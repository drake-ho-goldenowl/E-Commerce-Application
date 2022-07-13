package com.goldenowl.ecommerceapp.ui.ShippingAddress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.ShippingAddress
import com.goldenowl.ecommerceapp.databinding.FragmentAddShippingAddressBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddShippingAddressFragment : BaseFragment() {
    private lateinit var binding: FragmentAddShippingAddressBinding
    private val viewModel: ShippingAddressViewModel by viewModels()
    private var shippingAddress: ShippingAddress? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let { it ->
            val idAddress = it.getString(ID_ADDRESS)
            if (!idAddress.isNullOrBlank()) {
                viewModel.getAddress(idAddress)
            }
        }
        binding = FragmentAddShippingAddressBinding.inflate(inflater, container, false)
        setupObserve()
        bind()
        setFragmentListener()
        return binding.root
    }

    private fun setupObserve() {
        viewModel.apply {
            dismiss.observe(viewLifecycleOwner) {
                if (it) {
                    findNavController().navigateUp()
                }
            }

            toastMessage.observe(viewLifecycleOwner) {
                toastMessage(it)
                toastMessage.postValue("")
            }

            alertFullName.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it) {
                        txtLayoutFullName.isErrorEnabled = true
                        txtLayoutFullName.error = ALERT_FULL_NAME
                    } else {
                        txtLayoutFullName.isErrorEnabled = false
                    }
                }
            }

            alertAddress.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it) {
                        txtLayoutAddress.isErrorEnabled = true
                        txtLayoutAddress.error = ALERT_ADDRESS
                    } else {
                        txtLayoutAddress.isErrorEnabled = false
                    }
                }
            }

            alertCity.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it) {
                        txtLayoutCity.isErrorEnabled = true
                        txtLayoutCity.error = ALERT_CITY
                    } else {
                        txtLayoutCity.isErrorEnabled = false
                    }
                }
            }

            alertSate.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it) {
                        txtLayoutState.isErrorEnabled = true
                        txtLayoutState.error = ALERT_REGION
                    } else {
                        txtLayoutState.isErrorEnabled = false
                    }
                }
            }

            alertZipCode.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it) {
                        txtLayoutZipCode.isErrorEnabled = true
                        txtLayoutZipCode.error = ALERT_ZIP_CODE
                    } else {
                        txtLayoutZipCode.isErrorEnabled = false
                    }
                }
            }

            alertCountry.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it) {
                        txtLayoutCountry.isErrorEnabled = true
                        txtLayoutCountry.error = ALERT_COUNTRY
                    } else {
                        txtLayoutCountry.isErrorEnabled = false
                    }
                }
            }
            address.observe(viewLifecycleOwner) {
                it?.let { shipping ->
                    if (shipping.address.isNotBlank()) {
                        binding.apply {
                            shippingAddress = shipping
                            editTextFullName.setText(shipping.fullName)
                            editTextAddress.setText(shipping.address)
                            editTextCity.setText(shipping.city)
                            editTextState.setText(shipping.state)
                            editTextZipCode.setText(shipping.zipCode)
                            editTextCountry.setText(shipping.country)
                        }
                    }
                }

            }
        }
    }

    private fun bind() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = getString(R.string.adding_shipping_address)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            editTextFullName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    viewModel.checkFullName(editTextFullName.text.toString())
                }
            }

            editTextAddress.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    viewModel.checkAddress(editTextAddress.text.toString())
                }
            }

            editTextCity.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    viewModel.checkCity(editTextCity.text.toString())
                }
            }
            editTextState.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    viewModel.checkState(editTextState.text.toString())
                }
            }
            editTextZipCode.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    viewModel.checkZipCode(editTextZipCode.text.toString())
                }
            }


            txtLayoutCountry.endIconMode = TextInputLayout.END_ICON_CUSTOM
            editTextCountry.setOnClickListener {
                val bottomSelectCountry = BottomSelectCountry()
                bottomSelectCountry.show(parentFragmentManager, BottomSelectCountry.TAG)
            }

            btnSaveAddress.setOnClickListener {
                if (shippingAddress != null) {
                    shippingAddress?.let {
                        viewModel.updateShippingAddress(
                            it,
                            editTextFullName.text.toString(),
                            editTextAddress.text.toString(),
                            editTextCity.text.toString(),
                            editTextState.text.toString(),
                            editTextZipCode.text.toString(),
                            editTextCountry.text.toString(),
                        )
                    }
                } else {
                    viewModel.insertShippingAddress(
                        editTextFullName.text.toString(),
                        editTextAddress.text.toString(),
                        editTextCity.text.toString(),
                        editTextState.text.toString(),
                        editTextZipCode.text.toString(),
                        editTextCountry.text.toString()
                    )
                }

            }
        }
    }

    private fun setFragmentListener() {
        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
            val result = bundle.getString(BUNDLE_KEY_NAME_COUNTRY)
            result?.let {
                binding.editTextCountry.setText(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setAddressLiveData()
    }

    companion object {
        const val ALERT_FULL_NAME = "Full Name must more than 1"
        const val ALERT_ADDRESS = "Address must more than 5"
        const val ALERT_CITY = "City must more than 2"
        const val ALERT_REGION = "Region must more than 1"
        const val ALERT_ZIP_CODE = "Zip Code must more than 1"
        const val ALERT_COUNTRY = "Please choose country"
    }
}