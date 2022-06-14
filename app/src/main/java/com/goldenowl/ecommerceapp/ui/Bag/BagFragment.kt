package com.goldenowl.ecommerceapp.ui.Bag

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListBagAdapter
import com.goldenowl.ecommerceapp.databinding.FragmentBagBinding
import com.goldenowl.ecommerceapp.viewmodels.BagViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BagFragment : Fragment() {
    private lateinit var binding: FragmentBagBinding
    private val viewModel: BagViewModel by viewModels()

    private lateinit var adapterBag: ListBagAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!viewModel.userManager.isLogged()) {
            findNavController().navigate(R.id.warningFragment)
        }
        binding = FragmentBagBinding.inflate(inflater, container, false)

        adapterBag = ListBagAdapter({

        },{
            viewModel.insertFavorite(it.product,it.bag.size,it.bag.color)
        },{
            viewModel.removeBag(it.bag)
        },{
            viewModel.plusQuantity(it.bag)
        },{
            viewModel.minusQuantity(it.bag)
        })

        viewModel.bags.observe(viewLifecycleOwner){
            adapterBag.submitList(it)
            binding.txtPriceTotal.text = "${viewModel.calculatorTotal(it)}\$"
        }


        bind()
        setHasOptionsMenu(true)
        return binding.root
    }

    fun bind(){
        binding.apply {
            appBarLayout.topAppBar.title = "Bag"

            recyclerViewBag.layoutManager = LinearLayoutManager(context)
            recyclerViewBag.adapter = adapterBag
        }
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
                return true
            }
        })
    }


    companion object {

    }
}