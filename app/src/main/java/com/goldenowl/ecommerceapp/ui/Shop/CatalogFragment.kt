package com.goldenowl.ecommerceapp.ui.Shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListCategoriesAdater
import com.goldenowl.ecommerceapp.adapters.ListProductAdapter
import com.goldenowl.ecommerceapp.adapters.ListProductGridAdapter
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.FragmentCatalogBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment
import com.goldenowl.ecommerceapp.ui.Favorite.BottomSheetFavorite
import com.goldenowl.ecommerceapp.utilities.NEW
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CatalogFragment : BaseFragment() {
    private val viewModel: ShopViewModel by viewModels()
    private var nameTitle: String? = null
    private lateinit var binding: FragmentCatalogBinding
    private lateinit var adapterProduct: ListProductAdapter
    private lateinit var adapterProductGrid: ListProductGridAdapter
    private lateinit var adapterCategory: ListCategoriesAdater
    private var isLinearLayoutManager = true
    private var filterPrice = emptyList<Float>()
    private var listProduct: List<Product> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            nameTitle = it.getString(NAME_CATEGORY).toString()
            val searchName = it.getString(NAME_PRODUCT)
            if (searchName != null) {
                viewModel.setSearch(searchName)
            } else {
                viewModel.setSearch("")
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCatalogBinding.inflate(inflater, container, false)
        viewModel.setSort(0)
        viewModel.lastVisible = ""

        if (nameTitle.isNullOrBlank()) {
            viewModel.setCategory("")
        } else {
            if (nameTitle.toString() == NEW) {
                viewModel.setCategory("")
                viewModel.setSort(1)
            } else {
                viewModel.setCategory(nameTitle.toString())
            }
        }
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
        }, { btnFavorite, product ->
            val bottomSheetSize = BottomSheetFavorite(product)
            bottomSheetSize.show(parentFragmentManager, BottomSheetFavorite.TAG)
            viewModel.btnFavorite.postValue(btnFavorite)
        }, { view, product ->
            viewModel.setButtonFavorite(requireContext(), view, product.id)
        })

        adapterProductGrid = ListProductGridAdapter({
            val action = CatalogFragmentDirections.actionCatalogFragmentToProductDetailFragment(
                idProduct = it.id
            )
            findNavController().navigate(action)
        }, { btnFavorite, product ->
            val bottomSheetSize = BottomSheetFavorite(product)
            bottomSheetSize.show(parentFragmentManager, BottomSheetFavorite.TAG)
        }, { view, product ->
            viewModel.setButtonFavorite(requireContext(), view, product.id)
        })

        adapterCategory = ListCategoriesAdater { str ->
            if (binding.appBarLayout.topAppBar.title == str) {
                val action = CatalogFragmentDirections.actionCatalogFragmentSelf(
                    nameCategories = "",
                    nameProduct = null
                )
                findNavController().navigate(action)
            } else {
                val action = CatalogFragmentDirections.actionCatalogFragmentSelf(
                    nameCategories = str,
                    nameProduct = null
                )
                findNavController().navigate(action)
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
                var product = viewModel.filterSort(it)
                if (filterPrice.isNotEmpty()) product =
                    viewModel.filterPrice(filterPrice[0], filterPrice[1], product)
                else {
                    listProduct = product
                }
                binding.progressBar.visibility = View.INVISIBLE
                adapterProductGrid.submitList(product)
                adapterProduct.submitList(product)
            }

            loadMore.observe(viewLifecycleOwner) {
                if (!it) {
                    binding.progressBar.visibility = View.INVISIBLE
                } else {
                    binding.progressBar.visibility = View.VISIBLE
                }
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

            if (viewModel.statusFilter.value.third == 1) {
                appBarLayout.btnSort.text = getString(R.string.newest)
            }

            nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
                if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                    viewModel.products.value?.let {
                        if (viewModel.loadMore.value == true) {
                            progressBar.visibility = View.VISIBLE
                            viewModel.loadMore(it)
                        }
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
            appBarLayout.btnFilter.setOnClickListener {
                if (filterPrice.isNotEmpty()) {
                    val bottomFilter = BottomFilter(filterPrice[0], filterPrice[1])
                    bottomFilter.show(parentFragmentManager, BottomFilter.TAG)
                } else {
                    val bottomFilter = BottomFilter()
                    bottomFilter.show(parentFragmentManager, BottomFilter.TAG)
                }

            }
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
            if (!result.isNullOrBlank()) {
                binding.appBarLayout.btnSort.text = result
                viewModel.setSort(position)
            }
            val min = bundle.getFloat(BUNDLE_KEY_MIN)
            val max = bundle.getFloat(BUNDLE_KEY_MAX)
            if (min >= 0 && max > 0) {
                filterPrice = listOf(min, max)
                viewModel.apply {
                    products.postValue(filterPrice(min, max, listProduct))
                }
            } else {
                filterPrice = emptyList()
                viewModel.products.postValue(listProduct)
            }
            val isFavorite = bundle.getBoolean(BUNDLE_KEY_IS_FAVORITE, false)
            if (isFavorite) {
                viewModel.btnFavorite.value?.let {
                    it.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.btn_favorite_active
                    )
                }
            }
        }
    }
}