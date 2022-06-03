package com.goldenowl.ecommerceapp.ui.Shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.EcommerceApplication
import com.goldenowl.ecommerceapp.adapters.ListCategoriesAdapter2
import com.goldenowl.ecommerceapp.databinding.FragmentShopBinding
import com.goldenowl.ecommerceapp.viewmodels.ShopViewModel
import com.goldenowl.ecommerceapp.viewmodels.ShopViewModelFactory


class ShopFragment : Fragment() {
    private val viewModel: ShopViewModel by activityViewModels {
        ShopViewModelFactory(
            (activity?.application as EcommerceApplication).database.productDao()
        )
    }
    private lateinit var binding: FragmentShopBinding

    private lateinit var adapterCategory: ListCategoriesAdapter2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopBinding.inflate(inflater, container, false)
        arguments?.let {
            val selectCategory = it.getString(NAME_CATEGORY)
            if(selectCategory != null){
                val action = ShopFragmentDirections.actionShopFragmentToCatalogFragment(
                    nameCategories = selectCategory
                )
                findNavController().navigate(action)
                return binding.root
            }
        }

        adapterCategory = ListCategoriesAdapter2 { str ->
            val action = ShopFragmentDirections.actionShopFragmentToCatalogFragment(
                nameCategories = str
            )
            findNavController().navigate(action)
        }


        viewModel.allCategory.observe(this.viewLifecycleOwner) {
            adapterCategory.submitList(it)
        }
        binding.MaterialToolbar.title = "Categories"
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewCategories.adapter = adapterCategory

        binding.btnViewAllItems.setOnClickListener {
            val action = ShopFragmentDirections.actionShopFragmentToCatalogFragment(
                nameCategories = ""
            )
            findNavController().navigate(action)
        }


        return binding.root
    }

    companion object{
        const val NAME_CATEGORY = "nameCategories"
    }

}