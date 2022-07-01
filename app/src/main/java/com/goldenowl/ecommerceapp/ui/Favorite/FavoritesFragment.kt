package com.goldenowl.ecommerceapp.ui.Favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListCategoriesAdater
import com.goldenowl.ecommerceapp.adapters.ListFavoriteAdapter
import com.goldenowl.ecommerceapp.adapters.ListFavoriteGridAdapter
import com.goldenowl.ecommerceapp.databinding.FragmentFavoritesBinding
import com.goldenowl.ecommerceapp.ui.Shop.BottomSheetSort
import com.goldenowl.ecommerceapp.ui.Shop.CatalogFragment
import com.goldenowl.ecommerceapp.viewmodels.FavoriteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {
    private val viewModel: FavoriteViewModel by viewModels()
    private lateinit var adapterFavorite: ListFavoriteAdapter
    private lateinit var adapterFavoriteGrid: ListFavoriteGridAdapter
    private lateinit var adapterCategory: ListCategoriesAdater
    private var isLinearLayoutManager = true
    private var isFilterCategory = true
    private lateinit var binding: FragmentFavoritesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!viewModel.isLogged()) {
            findNavController().navigate(R.id.warningFragment)
        }
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        viewModel.setSearch("")
        viewModel.setSort(0)
        viewModel.setCategory("")

        adapterCategory = ListCategoriesAdater { str ->
            if (viewModel.statusFilter.value.first == str) {
                viewModel.setCategory("")
            } else {
                viewModel.setCategory(str)
            }
        }

        adapterFavorite = ListFavoriteAdapter({
            viewModel.removeFavorite(it.favorite)
        }, {
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToProductDetailFragment(
                idProduct = it.product.id
            )
            findNavController().navigate(action)
        }, {
            viewModel.insertBag(
                it.product.id,
                it.product.colors[0].color.toString(),
                it.favorite.size,
            )
        }, { view, favorite ->
            viewModel.setButtonBag(requireContext(), view, favorite)
        })

        adapterFavoriteGrid = ListFavoriteGridAdapter({
            viewModel.removeFavorite(it.favorite)
        }, {
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToProductDetailFragment(
                idProduct = it.product.id
            )
            findNavController().navigate(action)
        }, {
            viewModel.insertBag(
                it.product.id,
                it.product.colors[0].color.toString(),
                it.favorite.size,
            )
        }, { view, favorite ->
            viewModel.setButtonBag(requireContext(), view, favorite)
        })

        observeSetup()
        bind()
        setFragmentListener()
        return binding.root
    }

    private fun bind() {
        binding.apply {
            appBarLayout.topAppBar.title = "Favorite"


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
            recyclerViewProduct.adapter = adapterFavorite
            appBarLayout.btnChangeView.setOnClickListener {
                isLinearLayoutManager = !isLinearLayoutManager
                if (isLinearLayoutManager) {
                    recyclerViewProduct.layoutManager = LinearLayoutManager(context)
                    recyclerViewProduct.adapter = adapterFavorite
                } else {
                    recyclerViewProduct.layoutManager =
                        GridLayoutManager(context, CatalogFragment.GRIDVIEW_SPAN_COUNT)
                    recyclerViewProduct.adapter = adapterFavoriteGrid
                }
            }

            appBarLayout.btnSort.setOnClickListener {
                val bottomSheetSort = BottomSheetSort(viewModel.statusFilter.value.third)
                bottomSheetSort.show(parentFragmentManager, BottomSheetSort.TAG)
            }

            appBarLayout.btnFilter.setOnClickListener {
                isFilterCategory = !isFilterCategory
                if (isFilterCategory) {
                    appBarLayout.recyclerViewCategories.visibility = View.VISIBLE
                } else {
                    appBarLayout.recyclerViewCategories.visibility = View.GONE
                }
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
                                if (!newText.isNullOrEmpty()) {
                                    viewModel.setSearch(newText)
                                } else {
                                    viewModel.setSearch("")
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


    private fun observeSetup() {
        viewModel.allCategory.observe(viewLifecycleOwner) {
            adapterCategory.submitList(it)
        }

        viewModel.favoriteAndProducts.observe(viewLifecycleOwner) {
            val favorites = viewModel.filterSort(it)
            adapterFavoriteGrid.submitList(favorites)
            adapterFavorite.submitList(favorites)
        }

        viewModel.bags.observe(viewLifecycleOwner) {
            adapterFavorite.notifyDataSetChanged()
            adapterFavoriteGrid.notifyDataSetChanged()
        }
    }

    companion object {
        const val REQUEST_KEY = "request_key"
        const val BUNDLE_KEY_NAME = "bundle_name"
        const val BUNDLE_KEY_POSITION = "bundle_position"
    }
}