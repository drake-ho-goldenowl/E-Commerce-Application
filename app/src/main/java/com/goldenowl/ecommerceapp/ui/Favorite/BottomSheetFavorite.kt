package com.goldenowl.ecommerceapp.ui.Favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.goldenowl.ecommerceapp.adapters.ListSizeAdapter
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.BottomLayoutSelectSizeBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BottomSheetFavorite(
    private val product: Product,
    private val selectSizeInt: Int?,
    private var color: String?
) : BottomSheetDialogFragment() {
    private val viewModel: FavoriteViewModel by viewModels()
    private var selectSize: String? = null

    private lateinit var binding: BottomLayoutSelectSizeBinding
    private lateinit var adapter: ListSizeAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomLayoutSelectSizeBinding.inflate(inflater, container, false)

        val listSize = product.getAllSize()

        adapter = ListSizeAdapter {
            selectSize = it
        }
        adapter.submitList(listSize)

        if (selectSizeInt != null) {
            adapter.positionCurrent = selectSizeInt
            selectSize = listSize[selectSizeInt]
        }
        color = color ?: product.colors[0].color

        viewModel.disMiss.observe(viewLifecycleOwner) {
            if (it) {
                dismiss()
            }
        }

        observeSetup()
        bind()
        return binding.root
    }

    private fun observeSetup() {
        viewModel.apply {
            toastMessage.observe(viewLifecycleOwner) { str ->
                Toast.makeText(
                    context,
                    str,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun bind() {
        binding.apply {
//            val layoutManager = FlexboxLayoutManager(requireContext())
//            layoutManager.flexDirection = FlexDirection.ROW
//            layoutManager.justifyContent = JustifyContent.FLEX_START
//            layoutManager.alignItems = AlignItems.FLEX_START
//            layoutManager.flexWrap = FlexWrap.WRAP

            recyclerViewSize.layoutManager = GridLayoutManager(context, GRIDVIEW_SPAN_COUNT)

//            recyclerViewSize.layoutManager = layoutManager

            recyclerViewSize.adapter = adapter

            btnAddToCart.setOnClickListener {
                if (!selectSize.isNullOrBlank()) {
                    viewModel.insertFavorite(product, selectSize.toString(), color.toString())
                } else {
                    viewModel.toastMessage.postValue("Please select size")
                }
            }

        }
    }


    companion object {
        const val GRIDVIEW_SPAN_COUNT = 3
        const val TAG = "BOTTOM_SHEET_SIZE"
    }
}