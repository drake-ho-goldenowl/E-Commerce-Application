package com.goldenowl.ecommerceapp.ui.Shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListCategoriesAdapter2
import com.goldenowl.ecommerceapp.databinding.FragmentShopBinding
import com.goldenowl.ecommerceapp.viewmodels.ShopViewModel
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
        setupObserve()
        bind()
        return binding.root
    }

    private fun setupObserve() {
        viewModel.apply {
            allCategory.observe(viewLifecycleOwner) {
                adapterCategory.submitList(it)
            }
        }
    }

    private fun bind() {
        binding.apply {
            MaterialToolbar.title = getString(R.string.categories)
            recyclerViewCategories.layoutManager = LinearLayoutManager(context)
            recyclerViewCategories.adapter = adapterCategory

            btnViewAllItems.setOnClickListener {
                val action = ShopFragmentDirections.actionShopFragmentToCatalogFragment(
                    nameCategories = ""
                )
                findNavController().navigate(action)
            }




            searchBar.btnBack.setOnClickListener {
                setDefault()
            }

            // Handle Search Bar
            MaterialToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.ic_search -> {
                        setSearchView()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    fun setDefault() {
        binding.apply {
            searchBar.editTextSearch.setText("")
            searchBarLayout.visibility = View.GONE
        }
    }

    fun setSearchView() {
        binding.apply {
            searchBarLayout.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        setDefault()
    }

    companion object {
        const val NAME_CATEGORY = "nameCategories"
        val TEST = listOf("knitwear", "blazers", "shorts", "Light blouse big X", "rau cải")
    }

}