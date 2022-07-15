package com.goldenowl.ecommerceapp.ui.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.databinding.ItemViewPagerHomeBinding
import com.goldenowl.ecommerceapp.utilities.GlideDefault

class ViewPageImageHome(private val url: String, private val title: String) : Fragment() {
    private lateinit var binding: ItemViewPagerHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ItemViewPagerHomeBinding.inflate(inflater, container, false)

        binding.apply {
            GlideDefault.showHome(requireContext(), url, imgHome)
            txtTitle.text = title
            imgHome.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToCatalogFragment(
                    nameCategories = "",
                    nameProduct = null
                )
                findNavController().navigate(action)
            }
        }
        return binding.root
    }
}