package com.goldenowl.ecommerceapp.ui.Bag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.goldenowl.ecommerceapp.adapters.ListSizeAdapter
import com.goldenowl.ecommerceapp.data.Favorite
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.BottomLayoutSelectSizeBinding
import com.goldenowl.ecommerceapp.viewmodels.BagViewModel
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BottomSheetCart(
    private val product: Product,
    private var selectSizeInt: Int,
    private val selectColorInt: Int
) : BottomSheetDialogFragment() {
    private val viewModel: BagViewModel by viewModels()

    private lateinit var binding: BottomLayoutSelectSizeBinding
    private var selectSize: String? = null
    private lateinit var adapter: ListSizeAdapter
    private var favorite: Favorite? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomLayoutSelectSizeBinding.inflate(inflater, container, false)
        val listSize = product.getAllSize()
        val color = product.colors[selectColorInt]
        viewModel.setFavorite(product.id, color.sizes[selectSizeInt].size,color.color.toString())
        adapter = ListSizeAdapter {
            selectSize = it
        }
        adapter.submitList(listSize)

        adapter.positionCurrent = selectSizeInt
        selectSize = listSize[selectSizeInt]

        viewModel.toastMessage.observe(viewLifecycleOwner) { str ->
            Toast.makeText(
                this.context,
                str,
                Toast.LENGTH_SHORT
            ).show()
        }

//        viewModel.favorite.observe(viewLifecycleOwner) {
//            favorite = it
//        }

        bind()
        return binding.root
    }

    fun bind() {
        binding.apply {
            val layoutManager = FlexboxLayoutManager(requireContext())
            layoutManager.flexDirection = FlexDirection.ROW
//        layoutManager.justifyContent = JustifyContent.CENTER
            layoutManager.alignItems = AlignItems.CENTER
            recyclerViewSize.layoutManager = layoutManager

            recyclerViewSize.adapter = adapter
            btnAddToCart.text = "Add to Cart"
            btnAddToCart.setOnClickListener {
                if (!selectSize.isNullOrBlank()) {
                    viewModel.insertBag(
                        product.id,
                        product.colors[selectColorInt].color.toString(),
                        selectSize.toString(),
                        favorite
                    )
                    dismiss()
                    viewModel.toastMessage.postValue("Add to Cart Success")
                } else {
                    viewModel.toastMessage.postValue("Please select size")
                }
            }

        }
    }


    companion object {
        const val TAG = "BOTTOM_SHEET_SIZE"
    }
}