package com.goldenowl.ecommerceapp.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goldenowl.ecommerceapp.EcommerceApplication
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.RecycleListCategories
import com.goldenowl.ecommerceapp.adapters.RecycleListHorizontal
import com.goldenowl.ecommerceapp.adapters.RecycleListVertical
import com.goldenowl.ecommerceapp.databinding.FragmentShopBinding
import com.goldenowl.ecommerceapp.viewmodels.ShopViewModel
import com.goldenowl.ecommerceapp.viewmodels.ShopViewModelFactory


class ShopFragment : Fragment() {
    private val viewModel: ShopViewModel by activityViewModels {
        ShopViewModelFactory(
            (activity?.application as EcommerceApplication).database.productDao()
        )
    }

    private lateinit var binding: FragmentShopBinding

    private lateinit var recyclerView: RecyclerView

    private var isLinearLayoutManager = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopBinding.inflate(inflater, container, false)
        val adapterProduct = RecycleListVertical {
        }

        val adapterProductGrid = RecycleListHorizontal {
        }
        binding.topAppBar.title = "All Product"
        viewModel.statusQuery.value = ""
        viewModel.statusSearch.value = ""

        val adapterCategory = RecycleListCategories { str ->
            viewModel.filterByCategory(str)
            binding.topAppBar.title = str
        }

        viewModel.allCategory.observe(this.viewLifecycleOwner) {
            adapterCategory.submitList(it)
        }

        viewModel.products.observe(this.viewLifecycleOwner) {
            adapterProductGrid.submitList(it)
            adapterProduct.submitList(it)
        }

        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(
            this.context,
            LinearLayoutManager.HORIZONTAL, false
        )

        binding.recyclerViewCategories.adapter = adapterCategory

        recyclerView = binding.recyclerViewProduct

        binding.btnChangeView.background =
            if (isLinearLayoutManager)
                ContextCompat.getDrawable(this.requireContext(), com.goldenowl.ecommerceapp.R.drawable.ic_view_module)
            else ContextCompat.getDrawable(this.requireContext(), com.goldenowl.ecommerceapp.R.drawable.ic_view_list)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapterProduct
        binding.btnChangeView.setOnClickListener {
            isLinearLayoutManager = !isLinearLayoutManager
            if (isLinearLayoutManager) {
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = adapterProduct
            } else {
                recyclerView.layoutManager = GridLayoutManager(context, GRIDVIEW_SPAN_COUNT)
                recyclerView.adapter = adapterProductGrid
            }
        }

        binding.btnSort.setOnClickListener {
            val bottomSheetSort = BottomSheetSort(viewModel.statusSort.value)
            bottomSheetSort.show(parentFragmentManager, BottomSheetSort.TAG)
        }


        binding.MaterialToolbar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.ic_search ->{
                    val searchView = it.actionView as SearchView
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            if(newText!!.isNotEmpty())
                                println(viewModel.statusSearch.value)
                                viewModel.statusSearch.value = newText
                            return true
                        }
                    })

                    true
                }
                else -> false
            }
        }
        setFragmentListener()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
//        inflater.inflate(R.menu.top_app_bar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    private fun setFragmentListener() {
        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
            val result = bundle.getString(BUNDLE_KEY_NAME)
            val position = bundle.getInt(BUNDLE_KEY_POSITION)
            binding.btnSort.text = result
            viewModel.statusSort.value = position
        }
    }


    companion object {
        const val REQUEST_KEY = "request_key"
        const val BUNDLE_KEY_NAME = "bundle_name"
        const val BUNDLE_KEY_POSITION = "bundle_position"
        const val GRIDVIEW_SPAN_COUNT = 2
        const val POPULAR = 0
        const val NEWEST = 1
        const val CUSTOMER_REVIEW = 2
        const val PRICE_LOWEST_TO_HIGH = 3
        const val PRICE_HIGHEST_TO_LOW = 4
    }

}