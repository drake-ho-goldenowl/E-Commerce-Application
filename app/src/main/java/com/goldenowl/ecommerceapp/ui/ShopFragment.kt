package com.goldenowl.ecommerceapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.EcommerceApplication
import com.goldenowl.ecommerceapp.adapters.RecycleListCategories
import com.goldenowl.ecommerceapp.adapters.RecycleListVertical
import com.goldenowl.ecommerceapp.databinding.FragmentShopBinding
import com.goldenowl.ecommerceapp.viewmodels.ShopViewModel
import com.goldenowl.ecommerceapp.viewmodels.ShopViewModelFactory

class ShopFragment : Fragment() {
    private val viewModel: ShopViewModel by activityViewModels {
        ShopViewModelFactory(
            (activity?.application as EcommerceApplication).database.productDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentShopBinding.inflate(inflater, container, false)


        val adapterProduct = RecycleListVertical {
        }
        binding.topAppBar.title = "All Product"
        viewModel.statusQuery.value = ""

        val adapterCategory = RecycleListCategories {str ->
            viewModel.filterByCategory(str)
            binding.topAppBar.title = str
        }

        viewModel.allCategory.observe(this.viewLifecycleOwner) {
            adapterCategory.submitList(it)
        }

        viewModel.products.observe(this.viewLifecycleOwner) {
            adapterProduct.submitList(it)
        }

        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(
            this.context,
            LinearLayoutManager.HORIZONTAL, false
        )

        binding.recyclerViewCategories.adapter = adapterCategory

        binding.recyclerViewProduct.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerViewProduct.adapter = adapterProduct

        return binding.root
    }

}