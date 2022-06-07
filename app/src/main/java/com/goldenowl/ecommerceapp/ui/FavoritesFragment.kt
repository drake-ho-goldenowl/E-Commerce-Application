package com.goldenowl.ecommerceapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.EcommerceApplication
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListCategoriesAdater
import com.goldenowl.ecommerceapp.adapters.ListFavoriteAdapter
import com.goldenowl.ecommerceapp.adapters.ListFavoriteGridAdapter
import com.goldenowl.ecommerceapp.databinding.FragmentFavoritesBinding
import com.goldenowl.ecommerceapp.ui.Shop.CatalogFragment
import com.goldenowl.ecommerceapp.viewmodels.FavoriteViewModel
import com.goldenowl.ecommerceapp.viewmodels.FavoriteViewModelFactory

class FavoritesFragment : Fragment() {
    private val viewModel: FavoriteViewModel by activityViewModels {
        FavoriteViewModelFactory(
            (activity?.application as EcommerceApplication).database.productDao(),
            (activity?.application as EcommerceApplication).database.favoriteDao(),
            (activity?.application as EcommerceApplication).userManager
        )
    }

    private lateinit var adapterFavorite: ListFavoriteAdapter
    private lateinit var adapterFavoriteGrid: ListFavoriteGridAdapter
    private lateinit var adapterCategory: ListCategoriesAdater
    private var isLinearLayoutManager = true
    private lateinit var binding: FragmentFavoritesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        adapterCategory = ListCategoriesAdater { str ->
//            if(binding.appBarLayout.topAppBar.title == str){
//                viewModel.setCategory("")
//                binding.appBarLayout.topAppBar.title = "All product"
//            }
//            else{
//                viewModel.setCategory(str)
//                binding.appBarLayout.topAppBar.title = str
//            }
        }


        adapterFavorite = ListFavoriteAdapter(this, {
            viewModel.removeFavorite(it)
        }, {

        })

        adapterFavoriteGrid = ListFavoriteGridAdapter(this, {
            viewModel.removeFavorite(it)
        }, {
        })



        observeSetup()
        bind()
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
        }
    }


    private fun observeSetup() {
        viewModel.allCategory.observe(this.viewLifecycleOwner) {
            adapterCategory.submitList(it)
        }

        viewModel.favorites.observe(this.viewLifecycleOwner) {
//            val product = viewModel.filterSort(it)
            viewModel.addFavorite(it)
            adapterFavoriteGrid.submitList(it)
            adapterFavorite.submitList(it)
        }
    }

    companion object {
    }
}