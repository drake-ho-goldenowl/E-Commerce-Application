package com.goldenowl.ecommerceapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.EcommerceApplication
import com.goldenowl.ecommerceapp.adapters.ListProductGridAdapter
import com.goldenowl.ecommerceapp.databinding.FragmentHomeBinding
import com.goldenowl.ecommerceapp.viewmodels.HomeViewModel
import com.goldenowl.ecommerceapp.viewmodels.HomeViewModelFactory


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by activityViewModels {
        HomeViewModelFactory(
            (activity?.application as EcommerceApplication).database.productDao(),
            (activity?.application as EcommerceApplication).database.favoriteDao(),
            (activity?.application as EcommerceApplication).database.bagDao(),
            (activity?.application as EcommerceApplication).userManager
        )
    }

    private lateinit var adapterSale: ListProductGridAdapter
    private lateinit var adapterNew: ListProductGridAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)


        adapterSale = ListProductGridAdapter (this) {
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(
                idProduct = it.id
            )
            findNavController().navigate(action)
        }

        adapterNew = ListProductGridAdapter(this){
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(
                idProduct = it.id
            )
            findNavController().navigate(action)
        }


        viewModel.product.observe(viewLifecycleOwner){
            adapterSale.submitList(viewModel.filterSale(it))
            adapterNew.submitList(viewModel.filterNew(it))
        }


        bind()
        return binding.root
    }

    private fun bind(){
        binding.apply {
            recyclerViewSale.adapter = adapterSale

            recyclerViewSale.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL, false
            )

            recyclerViewNew.adapter = adapterNew

            recyclerViewNew.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL, false
            )

            txtViewAllSale.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToCatalogFragment(
                    nameCategories = ""
                )

                findNavController().navigate(action)
            }

            txtViewAllNew.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToCatalogFragment(
                    nameCategories = ""
                )
                findNavController().navigate(action)
            }

        }
    }
}