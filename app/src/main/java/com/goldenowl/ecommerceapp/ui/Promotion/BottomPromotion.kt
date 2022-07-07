package com.goldenowl.ecommerceapp.ui.Promotion

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.adapters.ListPromotionAdapter
import com.goldenowl.ecommerceapp.databinding.BottomLayoutPromotionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomPromotion(private val idPromotion: String?) : BottomSheetDialogFragment() {
    private val viewModel: PromotionViewModel by viewModels()
    private lateinit var binding: BottomLayoutPromotionBinding
    private lateinit var adapter: ListPromotionAdapter
    private var selectPromotion: String = ""
    private var salePercent: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomLayoutPromotionBinding.inflate(inflater, container, false)
        viewModel.fetchData()

        if (!idPromotion.isNullOrBlank()) {
            binding.editPromoCode.setText(idPromotion)
            viewModel.setStatusPromotion(idPromotion)
        }

        adapter = ListPromotionAdapter {
            selectPromotion = it.id
            salePercent = it.salePercent
            sendData(selectPromotion, salePercent)
            dismiss()
        }


        viewModel.promotions.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.promotion.observe(viewLifecycleOwner) {
            if (it != null) {
                selectPromotion = it.id
                salePercent = it.salePercent
                binding.txtWrongCode.visibility = View.GONE
            } else if (viewModel.statusPromotion.value.isNotBlank()) {
                binding.txtWrongCode.visibility = View.VISIBLE
            }
        }
        bind()
        return binding.root
    }

    fun bind() {
        binding.apply {
            recyclerViewPromotion.layoutManager = LinearLayoutManager(context)
            recyclerViewPromotion.adapter = adapter

            editPromoCode.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s.isNullOrBlank()) {
                        viewModel.setStatusPromotion(s.toString())
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })
        }
    }

    private fun sendData(select: String, sale: Long) {
        setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY_NAME to select, BUNDLE_KEY_SALE to sale))
    }

    companion object {
        const val TAG = "BOTTOM_SHEET_PROMOTION"
        const val REQUEST_KEY = "request"
        const val BUNDLE_KEY_NAME = "bundle_name_promotion"
        const val BUNDLE_KEY_SALE = "bundle_sale"
    }
}