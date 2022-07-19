package com.goldenowl.ecommerceapp.ui.Favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.goldenowl.ecommerceapp.adapters.ListColorAdapter
import com.goldenowl.ecommerceapp.adapters.ListSizeAdapter
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.BottomLayoutSelectSizeBinding
import com.goldenowl.ecommerceapp.ui.BaseBottomSheetDialog
import com.goldenowl.ecommerceapp.ui.BaseFragment.Companion.BUNDLE_KEY_IS_FAVORITE
import com.goldenowl.ecommerceapp.ui.BaseFragment.Companion.REQUEST_KEY
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BottomSheetFavorite(
    private val product: Product,
    private val selectSizeInt: Int? = null,
    private var color: String? = null,
) : BaseBottomSheetDialog() {
    private val viewModel: FavoriteViewModel by viewModels()
    private var selectSize: String? = null

    private lateinit var binding: BottomLayoutSelectSizeBinding
    private lateinit var adapterSize: ListSizeAdapter
    private lateinit var adapterColor: ListColorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val listSize = product.getAllSize()
        val listColor = product.getAllColor()
        adapterSize = ListSizeAdapter {
            selectSize = it
        }
        adapterSize.submitList(listSize)
        adapterColor = ListColorAdapter {
            color = if (color == it) {
                null
            } else {
                it
            }
        }
        adapterColor.submitList(listColor)
        if (selectSizeInt != null) {
            adapterSize.positionCurrent = selectSizeInt
            selectSize = listSize[selectSizeInt]
        }
        if (!color.isNullOrBlank()) {
            adapterColor.positionCurrent = listColor.indexOf(color)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomLayoutSelectSizeBinding.inflate(inflater, container, false)

        observeSetup()
        bind()
        return binding.root
    }

    private fun observeSetup() {
        viewModel.apply {
            toastMessage.observe(viewLifecycleOwner) { str ->
                toastMessage(str)
                toastMessage.postValue("")
            }

            dismiss.observe(viewLifecycleOwner) {
                if (it) {
                    dismiss()
                }
            }
        }
    }

    fun bind() {
        binding.apply {
            recyclerViewSize.layoutManager = GridLayoutManager(context, GRIDVIEW_SPAN_COUNT)
            recyclerViewSize.adapter = adapterSize

            recyclerViewColor.layoutManager = GridLayoutManager(context, GRIDVIEW_SPAN_COUNT)
            recyclerViewColor.adapter = adapterColor

            btnAddToCart.setOnClickListener {
                if (selectSize.isNullOrBlank()) {
                    viewModel.toastMessage.postValue(WARNING_SELECT_SIZE)
                } else if (color.isNullOrBlank()) {
                    viewModel.toastMessage.postValue(WARNING_SELECT_COLOR)
                } else {
                    viewModel.insertFavorite(product.id, selectSize.toString(), color.toString())
                    sendData()
                }
            }
        }
    }

    private fun sendData() {
        setFragmentResult(
            REQUEST_KEY,
            bundleOf(BUNDLE_KEY_IS_FAVORITE to true)
        )
    }

    companion object {
        const val GRIDVIEW_SPAN_COUNT = 3
        const val TAG = "BOTTOM_SHEET_SIZE"
    }
}