package com.goldenowl.ecommerceapp.ui.Favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.goldenowl.ecommerceapp.adapters.ListSizeAdapter
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.BottomLayoutSelectSizeBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment.Companion.BUNDLE_KEY_IS_FAVORITE
import com.goldenowl.ecommerceapp.ui.BaseFragment.Companion.REQUEST_KEY
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BottomSheetFavorite(
    private val product: Product,
    private val selectSizeInt: Int? = null,
    private var color: String? = null,
    ) : BottomSheetDialogFragment() {
    private val viewModel: FavoriteViewModel by viewModels()
    private var selectSize: String? = null

    private lateinit var binding: BottomLayoutSelectSizeBinding
    private lateinit var adapter: ListSizeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                Toast.makeText(
                    context,
                    str,
                    Toast.LENGTH_SHORT
                ).show()
            }

            disMiss.observe(viewLifecycleOwner) {
                if (it) {
                    dismiss()
                }
            }
        }
    }

    fun bind() {
        binding.apply {
            recyclerViewSize.layoutManager = GridLayoutManager(context, GRIDVIEW_SPAN_COUNT)
            recyclerViewSize.adapter = adapter

            btnAddToCart.setOnClickListener {
                if (!selectSize.isNullOrBlank()) {
                    viewModel.insertFavorite(product.id, selectSize.toString(), color.toString())
                    sendData()
                } else {
                    viewModel.toastMessage.postValue(WARNING_SELECT_SIZE)
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
        const val WARNING_SELECT_SIZE = "Please select size"
    }
}