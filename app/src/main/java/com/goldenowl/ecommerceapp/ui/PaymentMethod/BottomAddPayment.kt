package com.goldenowl.ecommerceapp.ui.PaymentMethod

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.BottomLayoutAddPaymentBinding
import com.goldenowl.ecommerceapp.ui.BaseBottomSheetDialog
import com.goldenowl.ecommerceapp.utilities.NON_DIGITS
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BottomAddPayment : BaseBottomSheetDialog() {
    private lateinit var binding: BottomLayoutAddPaymentBinding
    private val viewModel: PaymentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomLayoutAddPaymentBinding.inflate(inflater, container, false)
        setupObserve()
        bind()
        return binding.root
    }

    private fun setupObserve() {
        viewModel.apply {

            alertName.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it) {
                        txtLayoutNameCard.isErrorEnabled = true
                        txtLayoutNameCard.error = ALERT_NAME
                    } else {
                        txtLayoutNameCard.isErrorEnabled = false
                    }
                }
            }

            alertNumberCard.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it) {
                        txtLayoutCardNumber.isErrorEnabled = true
                        txtLayoutCardNumber.error = ALERT_NUM
                    } else {
                        txtLayoutCardNumber.isErrorEnabled = false
                    }
                }
            }

            alertExpertDate.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it) {
                        txtLayoutExpireDate.isErrorEnabled = true
                        txtLayoutExpireDate.error = ALERT_EXPIRE_DATE
                    } else {
                        txtLayoutExpireDate.isErrorEnabled = false
                    }
                }
            }

            alertCVV.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it) {
                        txtLayoutCVV.isErrorEnabled = true
                        txtLayoutCVV.error = ALERT_CVV
                    } else {
                        txtLayoutCVV.isErrorEnabled = false
                    }
                }
            }

            dismiss.observe(viewLifecycleOwner) {
                if (it) {
                    dismiss()
                }
            }

            toastMessage.observe(viewLifecycleOwner) {
                toastMessage(it)
                toastMessage.postValue("")
            }
        }
    }

    private fun bind() {
        binding.apply {
            editTextCardNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    txtLayoutCardNumber.endIconDrawable = null
                    if (!s.isNullOrBlank()) {
                        if (s[0] == '4') {
                            txtLayoutCardNumber.endIconDrawable =
                                ContextCompat.getDrawable(requireContext(), R.drawable.ic_visa2)
                        } else if (s[0] == '5') {
                            txtLayoutCardNumber.endIconDrawable =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_mastercard
                                )
                        }
                    }
                }

                private var current = ""
                override fun afterTextChanged(s: Editable?) {
                    s?.let {
                        if (it.toString() != current) {
                            val userInput = it.toString().replace(NON_DIGITS, "")
                            if (userInput.length <= 16) {
                                current = userInput.chunked(4).joinToString(" ")
                                it.filters = arrayOfNulls<InputFilter>(0)
                            }
                            it.replace(0, it.length, current, 0, current.length)
                        }
                    }
                }
            })

            editTextExpireDate.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        s?.let {
                            if (start == 1 && start + count == 2 && !it.contains('/')) {
                                editTextExpireDate.setText("$it/")
                                editTextExpireDate.setSelection(editTextExpireDate.length())
                            } else if (start == 3 && start - before == 2 && it.contains('/')) {
                                editTextExpireDate.setText(it.toString().replace("/", ""))
                                editTextExpireDate.setSelection(editTextExpireDate.length())
                            }
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {
                    }
                })

            editTextCardNumber.transformationMethod = null
            editTextExpireDate.transformationMethod = null
            editTextCVV.transformationMethod = null

            editTextNameCard.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    viewModel.alertName.postValue(false)
                }
            }

            editTextCardNumber.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    viewModel.alertNumberCard.postValue(false)
                }
            }

            editTextExpireDate.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    viewModel.alertExpertDate.postValue(false)
                }
            }
            editTextCVV.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    viewModel.alertCVV.postValue(false)
                }
            }
            btnAddCard.setOnClickListener {
                viewModel.insertCard(
                    editTextNameCard.text.toString(),
                    editTextCardNumber.text.toString(),
                    editTextExpireDate.text.toString(),
                    editTextCVV.text.toString(),
                    checkboxDefault.isChecked
                )
            }
        }
    }

    companion object {
        const val TAG = "BOTTOM_ADD_PAYMENT"
        const val ALERT_NAME = "Name on card must more than 2"
        const val ALERT_NUM = "Card Number should start from '4' or '5' and must valid"
        const val ALERT_EXPIRE_DATE = "Expire Date invalid"
        const val ALERT_CVV = "CVV invalid"
    }
}