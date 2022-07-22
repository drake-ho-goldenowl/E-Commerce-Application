package com.goldenowl.ecommerceapp.ui.Favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListFavoriteAdapter
import com.goldenowl.ecommerceapp.databinding.FragmentFavoritesBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : BaseFragment() {
    private val viewModel: FavoriteViewModel by activityViewModels()
    private lateinit var adapterFavorite: ListFavoriteAdapter
    private lateinit var binding: FragmentFavoritesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!viewModel.isLogged()) {
            findNavController().navigate(R.id.action_favoritesFragment_to_warningFragment)
        }
        adapterFavorite = ListFavoriteAdapter({
            viewModel.removeFavorite(it.favorite)
        }, {
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToProductDetailFragment(
                idProduct = it.product.id
            )
            findNavController().navigate(action)
        }, { buttonView, favoriteAndProduct ->
            viewModel.insertBag(
                favoriteAndProduct.product.id,
                favoriteAndProduct.favorite.color,
                favoriteAndProduct.favorite.size,
            )
            buttonView.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.btn_bag_active
            )
        }, { view, favorite ->
            viewModel.setButtonBag(requireContext(), view, favorite)
        })
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
                                    viewModel.favoriteAndProducts.value?.let { list ->
                                        adapterFavorite.submitList(list.filter { favorite ->
                                            favorite.product.title.lowercase()
                                                .contains(newText.lowercase())
                                        })
                                    }
                                } else {
                                    adapterFavorite.submitList(
                                        viewModel.favoriteAndProducts.value ?: emptyList()
                                    )
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
        viewModel.apply {
            favoriteAndProducts.observe(viewLifecycleOwner) {
                adapterFavorite.submitList(it)
            }

            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }

            isSuccess.observe(viewLifecycleOwner){
                isLoading.postValue(!it)
            }
        }

    }
}