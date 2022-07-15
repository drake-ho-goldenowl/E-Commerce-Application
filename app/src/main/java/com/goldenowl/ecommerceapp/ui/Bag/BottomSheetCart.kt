package com.goldenowl.ecommerceapp.ui.Bag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListSizeAdapter
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.BottomLayoutSelectSizeBinding
import com.goldenowl.ecommerceapp.ui.BaseBottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BottomSheetCart(
    private val product: Product,
    private var selectSizeInt: Int,
    private val selectColorInt: Int
) : BaseBottomSheetDialog() {
    private val viewModel: BagViewModel by viewModels()
    private lateinit var binding: BottomLayoutSelectSizeBinding
    private lateinit var adapter: ListSizeAdapter
    private var selectSize: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val listSize = product.getAllSize()
        adapter = ListSizeAdapter {
            selectSize = it
        }
        adapter.submitList(listSize)

        adapter.positionCurrent = selectSizeInt
        selectSize = listSize[selectSizeInt]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomLayoutSelectSizeBinding.inflate(inflater, container, false)

        setupObserve()
        bind()
        return binding.root
    }

    private fun setupObserve() {
        viewModel.apply {
            dismiss.observe(viewLifecycleOwner) {
                if (it) {
                    dismiss()
                }
            }
            toastMessage.observe(viewLifecycleOwner) { str ->
                toastMessage(str)
                toastMessage.postValue("")
            }
        }
    }

    fun bind() {
        binding.apply {
            recyclerViewSize.layoutManager = GridLayoutManager(context, GRIDVIEW_SPAN_COUNT)
            recyclerViewSize.adapter = adapter

            btnAddToCart.text = getString(R.string.add_to_cart)
            btnAddToCart.setOnClickListener {
                if (!selectSize.isNullOrBlank()) {
                    viewModel.insertBag(
                        product.id,
                        product.colors[selectColorInt].color.toString(),
                        selectSize.toString(),
                    )
                    viewModel.toastMessage.postValue("Add to Cart Success")
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