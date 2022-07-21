package com.goldenowl.ecommerceapp.ui.Shop

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.BottomLayoutSortBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment.Companion.BUNDLE_KEY_NAME
import com.goldenowl.ecommerceapp.ui.BaseFragment.Companion.BUNDLE_KEY_POSITION
import com.goldenowl.ecommerceapp.ui.BaseFragment.Companion.REQUEST_KEY
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetSort(private val select: Int) : BottomSheetDialogFragment() {
    lateinit var binding: BottomLayoutSortBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomLayoutSortBinding.inflate(inflater, container, false)

        binding.apply {
            btnPopular.setOnClickListener {
                sendData(btnPopular.text.toString(), 0)
                dismiss()
            }
            btnNewest.setOnClickListener {
                sendData(btnNewest.text.toString(), 1)
                dismiss()
            }
            btnCustomerReview.setOnClickListener {
                sendData(btnCustomerReview.text.toString(), 2)
                dismiss()
            }
            binding.btnLowestToHigh.setOnClickListener {
                sendData(btnLowestToHigh.text.toString(), 3)
                dismiss()
            }
            binding.btnHighestToLow.setOnClickListener {
                sendData(btnHighestToLow.text.toString(), 4)
                dismiss()
            }
            when (select) {
                0 -> setColorSelected(btnPopular)
                1 -> setColorSelected(btnNewest)
                2 -> setColorSelected(btnCustomerReview)
                3 -> setColorSelected(btnLowestToHigh)
                4 -> setColorSelected(btnHighestToLow)
            }
        }
        return binding.root
    }

    private fun setColorSelected(button: TextView) {
        button.background =
            ContextCompat.getDrawable(requireContext(), R.color.colorPrimary)
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        button.setTypeface(button.typeface, Typeface.BOLD)
    }

    private fun sendData(select: String, position: Int) {
        setFragmentResult(
            REQUEST_KEY,
            bundleOf(BUNDLE_KEY_NAME to select, BUNDLE_KEY_POSITION to position)
        )
    }

    companion object {
        const val TAG = "BottomSheetSort"
    }
}