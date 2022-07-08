package com.goldenowl.ecommerceapp.ui.Favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListFavoriteAdapter
import com.goldenowl.ecommerceapp.data.FavoriteAndProduct
import com.goldenowl.ecommerceapp.databinding.FragmentFavoritesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {
    private val viewModel: FavoriteViewModel by viewModels()
    private lateinit var adapterFavorite: ListFavoriteAdapter
    private lateinit var binding: FragmentFavoritesBinding
    private var allFavorites: List<FavoriteAndProduct> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!viewModel.isLogged()) {
            findNavController().navigate(R.id.warningFragment)
        }
        viewModel.fetchFavorites()
        adapterFavorite = ListFavoriteAdapter({
            viewModel.removeFavorite(it.favorite)
        }, {
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToProductDetailFragment(
                idProduct = it.product.id
            )
            findNavController().navigate(action)
        }, { buttonView,favoriteAndProduct->
            viewModel.insertBag(
                favoriteAndProduct.product.id,
                favoriteAndProduct.product.colors[0].color.toString(),
                favoriteAndProduct.favorite.size,
            )
            buttonView.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.btn_bag_active
            )
        }, { view, favorite ->
            viewModel.setButtonBag(requireContext(), view, favorite)
        })
        adapterFavorite.submitList(allFavorites)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        observeSetup()
        bind()
        return binding.root
    }

    private fun bind() {
        binding.apply {
            appBarLayout.topAppBar.title = getString(R.string.favorite)

            recyclerViewProduct.layoutManager = LinearLayoutManager(context)
            recyclerViewProduct.adapter = adapterFavorite

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
                                    adapterFavorite.submitList(allFavorites.filter { favorite ->
                                        favorite.product.title.lowercase()
                                            .contains(newText.lowercase())
                                    })
                                } else {
                                    adapterFavorite.submitList(allFavorites)
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


    private fun observeSetup() {
        viewModel.favoriteAndProducts.observe(viewLifecycleOwner) {
            if (allFavorites != it){
                allFavorites = it
                adapterFavorite.submitList(it)
            }
        }
    }
}