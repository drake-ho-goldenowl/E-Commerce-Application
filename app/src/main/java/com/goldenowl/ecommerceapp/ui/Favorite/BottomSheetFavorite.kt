package com.goldenowl.ecommerceapp.ui.Favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.goldenowl.ecommerceapp.adapters.ListSizeAdapter
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.BottomLayoutSelectSizeBinding
import com.goldenowl.ecommerceapp.viewmodels.FavoriteViewModel
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BottomSheetFavorite(private val product: Product,private val selectSizeInt: Int?, private var color: String?) : BottomSheetDialogFragment() {
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

        if(selectSizeInt != null){
            adapter.positionCurrent = selectSizeInt
            selectSize = listSize[selectSizeInt]
        }
        color = color ?: product.colors[0].color

        viewModel.toastMessage.observe(this.viewLifecycleOwner) { str ->
            Toast.makeText(
                this.context,
                str,
                Toast.LENGTH_SHORT
            ).show()
        }

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
            btnAddToCart.setOnClickListener {
                if (!selectSize.isNullOrBlank()) {
                    viewModel.insertFavorite(product, selectSize.toString(),color.toString())
                    viewModel.updateFavoriteFirebase()
                    dismiss()
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