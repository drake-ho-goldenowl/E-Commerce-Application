package com.goldenowl.ecommerceapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.goldenowl.ecommerceapp.EcommerceApplication
import com.goldenowl.ecommerceapp.databinding.FragmentFavoritesBinding
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

    private lateinit var binding: FragmentFavoritesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater,container,false)

        binding.appBarLayout.topAppBar.title = "Favorite"
        return binding.root
    }

    companion object {
    }
}