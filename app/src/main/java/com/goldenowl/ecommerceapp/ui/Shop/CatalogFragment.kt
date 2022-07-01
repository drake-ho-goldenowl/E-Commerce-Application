package com.goldenowl.ecommerceapp.ui.Shop

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.*
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.FragmentCatalogBinding
import com.goldenowl.ecommerceapp.ui.Favorite.BottomSheetFavorite
import com.goldenowl.ecommerceapp.utilities.HISTORY
import com.goldenowl.ecommerceapp.viewmodels.ShopViewModel
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CatalogFragment : Fragment() {
    private val viewModel: ShopViewModel by viewModels()
    private var nameTitle: String? = null
    private lateinit var binding: FragmentCatalogBinding
    private lateinit var adapterProduct: ListProductAdapter
    private lateinit var adapterProductGrid: ListProductGridAdapter
    private lateinit var adapterCategory: ListCategoriesAdater

    private lateinit var adapterHistoryAdapter: ListHistoryAdapter
    private lateinit var adapterCategoryAdapter: ListCategoryAdapter
    private lateinit var listCategory: List<String>
    private lateinit var listProduct: List<Product>
    private val listHistory: MutableSet<String> = mutableSetOf()
    private val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
    private var isLinearLayoutManager = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            nameTitle = it.getString(NAME_CATEGORY).toString()
        }
        binding = FragmentCatalogBinding.inflate(inflater, container, false)
        if (nameTitle.isNullOrBlank()) {
            viewModel.setCategory("")
        } else {
            viewModel.setCategory(nameTitle.toString())
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

        adapterHistoryAdapter = ListHistoryAdapter {
            binding.searchBar.editTextSearch.setText(it)
        }

        adapterCategoryAdapter = ListCategoryAdapter { str ->
            setDefault()
            viewModel.setCategory(str)
            binding.appBarLayout.topAppBar.title = str
            adapterCategory.positionCurrent = listCategory.indexOf(str)
            adapterCategory.notifyDataSetChanged()
        }
    }

    private fun observeSetup() {
        viewModel.allCategory.observe(viewLifecycleOwner) {
            adapterCategory.submitList(it)
            listCategory = it
        }

        viewModel.products.observe(viewLifecycleOwner) {
            val product = viewModel.filterSort(it)
            listProduct = product
            adapterProductGrid.submitList(product)
            adapterProduct.submitList(product)
        }

        viewModel.favorites.observe(viewLifecycleOwner) {
            adapterProductGrid.notifyDataSetChanged()
            adapterProduct.notifyDataSetChanged()
        }
    }

    private fun bind() {
        binding.apply {
            if (nameTitle.isNullOrBlank()) {
                appBarLayout.topAppBar.title = getString(R.string.all_product)
            } else {
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
                    recyclerViewProduct.layoutManager =
                        GridLayoutManager(context, GRIDVIEW_SPAN_COUNT)
                    recyclerViewProduct.adapter = adapterProductGrid
                }
            }

            appBarLayout.btnSort.setOnClickListener {
                val bottomSheetSort = BottomSheetSort(viewModel.statusFilter.value.third)
                bottomSheetSort.show(parentFragmentManager, BottomSheetSort.TAG)
            }


            //Set up Search
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            layoutManager.alignItems = AlignItems.FLEX_START
            historyLayout.recyclerViewHistory.layoutManager = layoutManager

            historyLayout.recyclerViewHistory.adapter = adapterHistoryAdapter

            var list = sharedPref?.getStringSet(HISTORY, null)
            if (list == null) {
                list = emptySet()
            }
            println(list)
            listHistory.addAll(list)

//            adapterHistoryAdapter.submitList(listHistory.toList())

            categoryLayout.recyclerViewCategory.layoutManager =
                GridLayoutManager(context, GRIDVIEW_SPAN_COUNT)
            categoryLayout.recyclerViewCategory.adapter = adapterCategoryAdapter

            categoryLayout.btnViewAll.setOnClickListener {
                adapterCategoryAdapter.submitList(listCategory)
                categoryLayout.btnViewAll.visibility = View.GONE
            }

            searchBar.btnBack.setOnClickListener {
                setDefault()
            }

            searchBar.editTextSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty()) {
                        viewModel.setSearch(s.toString())
                        setInvisibleSearchAdvance()
                    } else {
                        viewModel.setSearch("")
                        setVisibleSearchAdvance()
                    }
                }

            })

            searchBar.editTextSearch.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    if (searchBar.editTextSearch.text.isNotBlank()) {
                        listHistory.add(searchBar.editTextSearch.text.toString())
                        sharedPref?.edit()?.putStringSet(HISTORY, listHistory)?.apply()
                    }
                }
            }

            // Handle Search Bar
            appBarLayout.MaterialToolbar.setOnMenuItemClickListener {
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
            setInvisibleSearchAdvance()
            searchBar.editTextSearch.setText("")
            searchBarLayout.visibility = View.GONE
        }
    }

    fun setSearchView() {
        binding.apply {
            setVisibleSearchAdvance()
            searchBar.editTextSearch.setText("")
            searchBarLayout.visibility = View.VISIBLE
        }
        viewModel.setSearch("")
        adapterCategoryAdapter.submitList(listCategory.subList(0, MAX_CATEGORY))
        setListCategorySearch()
    }

    fun setInvisibleSearchAdvance() {
        binding.apply {
            adapterProduct.submitList(listProduct)
            adapterProductGrid.submitList(listProduct)
            layoutSearchAdvanced.visibility = View.GONE
            historyLayout.mainLayout.visibility = View.GONE
            categoryLayout.mainLayout.visibility = View.GONE
        }
    }

    fun setVisibleSearchAdvance() {
        binding.apply {
            adapterProduct.submitList(emptyList())
            adapterProductGrid.submitList(emptyList())
            layoutSearchAdvanced.visibility = View.VISIBLE
            historyLayout.mainLayout.visibility = View.VISIBLE
            categoryLayout.mainLayout.visibility = View.VISIBLE
        }
    }

    fun setListCategorySearch() {
        if (listCategory.size <= MAX_CATEGORY) {
            binding.categoryLayout.btnViewAll.visibility = View.GONE
        } else {
            binding.categoryLayout.btnViewAll.visibility = View.VISIBLE
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

    override fun onResume() {
        super.onResume()
        viewModel.setSearch("")
        viewModel.setSort(0)
    }

    companion object {
        const val REQUEST_KEY = "request_key"
        const val BUNDLE_KEY_NAME = "bundle_name"
        const val BUNDLE_KEY_POSITION = "bundle_position"
        const val GRIDVIEW_SPAN_COUNT = 2
        const val MAX_CATEGORY = 4
        const val NAME_CATEGORY = "nameCategories"
    }
}