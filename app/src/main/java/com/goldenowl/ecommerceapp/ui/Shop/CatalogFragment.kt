package com.goldenowl.ecommerceapp.ui.Shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.EcommerceApplication
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListCategoriesAdater
import com.goldenowl.ecommerceapp.adapters.ListProductAdapter
import com.goldenowl.ecommerceapp.adapters.ListProductGridAdapter
import com.goldenowl.ecommerceapp.databinding.FragmentCatalogBinding
import com.goldenowl.ecommerceapp.viewmodels.ShopViewModel
import com.goldenowl.ecommerceapp.viewmodels.ShopViewModelFactory

class CatalogFragment : Fragment() {
    private val viewModel: ShopViewModel by activityViewModels {
        ShopViewModelFactory(
            (activity?.application as EcommerceApplication).database.productDao()
        )
    }
    private var nameTitle: String? = null
    private lateinit var binding: FragmentCatalogBinding
    private lateinit var adapterProduct : ListProductAdapter
    private lateinit var adapterProductGrid : ListProductGridAdapter
    private lateinit var adapterCategory : ListCategoriesAdater

    private var isLinearLayoutManager = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            nameTitle = it.getString(NAME_CATEGORY).toString()
        }
        binding = FragmentCatalogBinding.inflate(inflater, container, false)
        if(nameTitle.isNullOrBlank()){
            viewModel.setCategory("")
        }
        else{
            viewModel.setCategory(nameTitle.toString())
        }
        viewModel.setSearch("")
        viewModel.setSort(0)


        adapterProduct = ListProductAdapter(this) {
        }

        adapterProductGrid = ListProductGridAdapter(this) {
        }

        adapterCategory = ListCategoriesAdater { str ->
            if(binding.appBarLayout.topAppBar.title == str){
                viewModel.setCategory("")
                binding.appBarLayout.topAppBar.title = "All product"
            }
            else{
                viewModel.setCategory(str)
                binding.appBarLayout.topAppBar.title = str
            }
        }

        observeSetup()
        bind()
        setFragmentListener()

        return binding.root
    }

    private fun observeSetup(){
        viewModel.allCategory.observe(this.viewLifecycleOwner) {
            adapterCategory.submitList(it)
        }

        viewModel.products.observe(this.viewLifecycleOwner) {
            val product = viewModel.filterSort(it)
            adapterProductGrid.submitList(product)
            adapterProduct.submitList(product)
        }
    }

    private fun bind(){
        binding.apply {
            if(nameTitle.isNullOrBlank()){
                appBarLayout.topAppBar.title = "All Product"
            }
            else{
                appBarLayout.topAppBar.title = nameTitle
            }

            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            appBarLayout.recyclerViewCategories.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL, false
            )

            appBarLayout.recyclerViewCategories.adapter = adapterCategory

            //Handle Button Change View
            appBarLayout.btnChangeView.background =
                if (isLinearLayoutManager)
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_view_module)
                else ContextCompat.getDrawable(requireContext(), R.drawable.ic_view_list)

            recyclerViewProduct.layoutManager = LinearLayoutManager(context)
            recyclerViewProduct.adapter = adapterProduct
            appBarLayout.btnChangeView.setOnClickListener {
                isLinearLayoutManager = !isLinearLayoutManager
                if (isLinearLayoutManager) {
                    recyclerViewProduct.layoutManager = LinearLayoutManager(context)
                    recyclerViewProduct.adapter = adapterProduct
                } else {
                    recyclerViewProduct.layoutManager = GridLayoutManager(context, GRIDVIEW_SPAN_COUNT)
                    recyclerViewProduct.adapter = adapterProductGrid
                }
            }

            appBarLayout.btnSort.setOnClickListener {
                val bottomSheetSort = BottomSheetSort(viewModel.statusFilter.value.third)
                bottomSheetSort.show(parentFragmentManager, BottomSheetSort.TAG)
            }


            // Handle Search Bar
            appBarLayout.MaterialToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.ic_search -> {
                        val searchView = it.actionView as SearchView
                        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                return true
                            }

                            override fun onQueryTextChange(newText: String?): Boolean {
                                if (newText!!.isNotEmpty()){
                                    println(viewModel.statusFilter.value.second)
                                    viewModel.setSearch(newText)

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
    }

    private fun setFragmentListener() {
        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
            val result = bundle.getString(BUNDLE_KEY_NAME)
            val position = bundle.getInt(BUNDLE_KEY_POSITION)
            binding.appBarLayout.btnSort.text = result
            viewModel.setSort(position)
        }
    }

    companion object {
        const val REQUEST_KEY = "request_key"
        const val BUNDLE_KEY_NAME = "bundle_name"
        const val BUNDLE_KEY_POSITION = "bundle_position"
        const val GRIDVIEW_SPAN_COUNT = 2
        const val NAME_CATEGORY = "nameCategories"
    }
}