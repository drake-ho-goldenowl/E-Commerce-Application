package com.goldenowl.ecommerceapp.ui.Bag

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.EcommerceApplication
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.FragmentBagBinding
import com.goldenowl.ecommerceapp.viewmodels.BagViewModel
import com.goldenowl.ecommerceapp.viewmodels.BagViewModelFactory

class BagFragment : Fragment() {
    private lateinit var binding: FragmentBagBinding
    private val viewModel: BagViewModel by activityViewModels {
        BagViewModelFactory(
            (activity?.application as EcommerceApplication).database.productDao(),
            (activity?.application as EcommerceApplication).database.bagDao(),
            (activity?.application as EcommerceApplication).database.favoriteDao(),
            (activity?.application as EcommerceApplication).userManager
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(!viewModel.userManager.isLogged()){
            findNavController().navigate(R.id.warningFragment)
        }
        binding = FragmentBagBinding.inflate(inflater,container,false)
        binding.appBarLayout.topAppBar.title = "Bag"

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar, menu)
        val searchItem = menu.findItem(R.id.ic_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                println("Hello")
                return true
            }
        })
    }


    companion object {

    }
}