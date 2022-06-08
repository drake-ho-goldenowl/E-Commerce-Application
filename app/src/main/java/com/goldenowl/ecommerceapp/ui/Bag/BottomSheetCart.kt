package com.goldenowl.ecommerceapp.ui.Bag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.goldenowl.ecommerceapp.EcommerceApplication
import com.goldenowl.ecommerceapp.adapters.ListSizeAdapter
import com.goldenowl.ecommerceapp.data.Favorite
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.BottomLayoutSelectSizeBinding
import com.goldenowl.ecommerceapp.viewmodels.BagViewModel
import com.goldenowl.ecommerceapp.viewmodels.BagViewModelFactory
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetCart(
    private val product: Product,
    private var selectSizeInt: Int,
    private val selectColorInt: Int
) : BottomSheetDialogFragment() {
    private val viewModel: BagViewModel by activityViewModels {
        BagViewModelFactory(
            (activity?.application as EcommerceApplication).database.productDao(),
            (activity?.application as EcommerceApplication).database.bagDao(),
            (activity?.application as EcommerceApplication).database.favoriteDao(),
            (activity?.application as EcommerceApplication).userManager
        )
    }

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
        val list = viewModel.getAllSize(product)
        viewModel.setFavorite(product.id, product.colors[selectColorInt].color!!)
        adapter = ListSizeAdapter {
            selectSize = it
        }
        adapter.submitList(list)

        adapter.positionCurrent = selectSizeInt
        selectSize = list[selectSizeInt]

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
                        product,
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