package com.goldenowl.ecommerceapp.ui.Shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListCategoriesAdater
import com.goldenowl.ecommerceapp.adapters.ListProductAdapter
import com.goldenowl.ecommerceapp.adapters.ListProductGridAdapter
import com.goldenowl.ecommerceapp.databinding.FragmentCatalogBinding
import com.goldenowl.ecommerceapp.ui.Favorite.BottomSheetFavorite
import com.goldenowl.ecommerceapp.viewmodels.ShopViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CatalogFragment : Fragment() {
    private val viewModel: ShopViewModel by viewModels()
    private var nameTitle: String? = null
    private lateinit var binding: FragmentCatalogBinding
    private lateinit var adapterProduct: ListProductAdapter
    private lateinit var adapterProductGrid: ListProductGridAdapter
    private lateinit var adapterCategory: ListCategoriesAdater
    private var isLinearLayoutManager = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            nameTitle = it.getString(NAME_CATEGORY).toString()
            val searchName = it.getString(NAME_PRODUCT)
            if (searchName != null) {
                viewModel.setSearch(searchName)
            } else {
                viewModel.setSearch("")
            }
        }
        binding = FragmentCatalogBinding.inflate(inflater, container, false)
        if (nameTitle.isNullOrBlank()) {
            viewModel.setCategory("")
        } else {
            viewModel.setCategory(nameTitle.toString())
        }
        viewModel.setSort(0)

        observeSetup()
        adapterSetup()
        bind()
        setFragmentListener()

        return binding.root
    }

    private fun adapterSetup() {
        adapterProduct = ListProductAdapter({
            val action = CatalogFragmentDirections.actionCatalogFragmentToProductDetailFragment(
                idProduct = it.id
            )
            findNavController().navigate(action)
        }, {
            val bottomSheetSize = BottomSheetFavorite(it, null, null)
            bottomSheetSize.show(parentFragmentManager, BottomSheetFavorite.TAG)
        }, { view, product ->
            viewModel.setButtonFavorite(requireContext(), view, product.id)
        })

        adapterProductGrid = ListProductGridAdapter({
            val action = CatalogFragmentDirections.actionCatalogFragmentToProductDetailFragment(
                idProduct = it.id
            )
            findNavController().navigate(action)
        }, {
            val bottomSheetSize = BottomSheetFavorite(it, null, null)
            bottomSheetSize.show(parentFragmentManager, BottomSheetFavorite.TAG)
        }, { view, product ->
            viewModel.setButtonFavorite(requireContext(), view, product.id)
        })

        adapterCategory = ListCategoriesAdater { str ->
            if (binding.appBarLayout.topAppBar.title == str) {
                viewModel.setCategory("")
                binding.appBarLayout.topAppBar.title = getString(R.string.all_product)
            } else {
                viewModel.setCategory(str)
                binding.appBarLayout.topAppBar.title = str
            }
        }
    }

    private fun observeSetup() {
        viewModel.apply {
            allCategory.observe(viewLifecycleOwner) {
                adapterCategory.submitList(it)
                adapterCategory.positionCurrent = it.indexOf(getCategory())
            }

            products.observe(viewLifecycleOwner) {
                val product = viewModel.filterSort(it)
                adapterProductGrid.submitList(product)
                adapterProduct.submitList(product)
            }

            favorites.observe(viewLifecycleOwner) {
                adapterProductGrid.notifyDataSetChanged()
                adapterProduct.notifyDataSetChanged()
            }
        }
    }

    private fun bind() {
        binding.apply {
            if (nameTitle.isNullOrBlank()) {
                appBarLayout.topAppBar.title = getString(R.string.all_product)
            } else {
                appBarLayout.topAppBar.title = nameTitle
            }

            nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
                if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                    println("asdasdasdas")
                    viewModel.products.value?.let {
                        println("asdasdasdas")
                        viewModel.loadMore(it)
                    }
                }
            })

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
                    recyclerViewProduct.layoutManager =
                        GridLayoutManager(context, GRIDVIEW_SPAN_COUNT)
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
                        findNavController().navigateUp()
                        findNavController().navigate(R.id.searchFragment)
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
        const val NAME_PRODUCT = "nameProduct"
    }
}