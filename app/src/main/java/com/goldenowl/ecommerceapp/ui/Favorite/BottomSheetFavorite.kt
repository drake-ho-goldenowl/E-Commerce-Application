package com.goldenowl.ecommerceapp.ui.Favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.goldenowl.ecommerceapp.EcommerceApplication
import com.goldenowl.ecommerceapp.adapters.ListSizeAdapter
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.BottomLayoutSelectSizeBinding
import com.goldenowl.ecommerceapp.viewmodels.FavoriteViewModel
import com.goldenowl.ecommerceapp.viewmodels.FavoriteViewModelFactory
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetFavorite(private val product: Product) :BottomSheetDialogFragment(){
    private val viewModel: FavoriteViewModel by activityViewModels {
        FavoriteViewModelFactory(
            (activity?.application as EcommerceApplication).database.productDao(),
            (activity?.application as EcommerceApplication).database.favoriteDao(),
            (activity?.application as EcommerceApplication).userManager
        )
    }
    private lateinit var binding: BottomLayoutSelectSizeBinding
    private var selectSize: String? = null
    private lateinit var adapter: ListSizeAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomLayoutSelectSizeBinding.inflate(inflater,container,false)

        val list = viewModel.getAllSize(product)

        adapter = ListSizeAdapter{
            selectSize = it
        }
        adapter.submitList(list)


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

    fun bind(){
        binding.apply {
            val layoutManager = FlexboxLayoutManager(requireContext())
            layoutManager.flexDirection = FlexDirection.ROW
//        layoutManager.justifyContent = JustifyContent.CENTER
            layoutManager.alignItems = AlignItems.CENTER
            recyclerViewSize.layoutManager = layoutManager

            recyclerViewSize.adapter = adapter
            btnAddToCart.setOnClickListener {
                if(!selectSize.isNullOrBlank()){
                    viewModel.insertFavorite(product,product.colors[0].color.toString(),selectSize.toString())
                    dismiss()
                }
                else{
                    viewModel.toastMessage.postValue("Please select size")
                }
            }

        }
    }



    companion object{
        const val TAG = "BOTTOM_SHEET_SIZE"
    }
}