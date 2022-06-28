package com.goldenowl.ecommerceapp.ui.Shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListCategoriesAdapter2
import com.goldenowl.ecommerceapp.databinding.FragmentShopBinding
import com.goldenowl.ecommerceapp.viewmodels.ShopViewModel
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ShopFragment : Fragment() {
    private val viewModel: ShopViewModel by viewModels()
    private lateinit var binding: FragmentShopBinding

    private lateinit var adapterCategory: ListCategoriesAdapter2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopBinding.inflate(inflater, container, false)
        arguments?.let {
            val selectCategory = it.getString(NAME_CATEGORY)
            if (selectCategory != null) {
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

        binding.apply {
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            layoutManager.alignItems = AlignItems.FLEX_START
            categoryLayout.recyclerViewCategories.layoutManager = layoutManager

            // Handle Search Bar
            MaterialToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.ic_search -> {
                        categoryLayout.mainLayout.visibility = View.VISIBLE
                        historyLayout.mainLayout.visibility = View.VISIBLE
                        val searchView = it.actionView as SearchView
                        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                return true
                            }

                            override fun onQueryTextChange(newText: String?): Boolean {
                                if (!newText.isNullOrEmpty()) {
                                    viewModel.setSearch(newText)
                                } else {
                                    viewModel.setSearch("")
                                    categoryLayout.mainLayout.visibility = View.VISIBLE
                                    historyLayout.mainLayout.visibility = View.VISIBLE
                                }
                                return true
                            }
                        })

                        true
                    }
                    else -> false
                }
            }
        }



        return binding.root
    }

    companion object {
        const val NAME_CATEGORY = "nameCategories"
    }

}