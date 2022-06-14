package com.goldenowl.ecommerceapp.ui.Bag

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListBagAdapter
import com.goldenowl.ecommerceapp.data.BagAndProduct
import com.goldenowl.ecommerceapp.databinding.FragmentBagBinding
import com.goldenowl.ecommerceapp.viewmodels.BagViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BagFragment : Fragment() {
    private lateinit var binding: FragmentBagBinding
    private val viewModel: BagViewModel by viewModels()

    private lateinit var adapterBag: ListBagAdapter
    private lateinit var promotions: List<BagAndProduct>
    private var salePercent : Long  = 0
    private var idPromotion : String = ""
    private var isButtonRemove = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!viewModel.userManager.isLogged()) {
            findNavController().navigate(R.id.warningFragment)
        }
        binding = FragmentBagBinding.inflate(inflater, container, false)

        adapterBag = ListBagAdapter({
            val action = BagFragmentDirections.actionBagFragmentToProductDetailFragment(
                idProduct = it.product.id
            )
            findNavController().navigate(action)
        },{
            viewModel.insertFavorite(it.product,it.bag.size,it.bag.color)
        },{
            viewModel.removeBag(it.bag)
        },{
            viewModel.plusQuantity(it.bag)
        },{
            viewModel.minusQuantity(it.bag)
        })
        viewModel.setSearch("")

        observeSetup()
        bind()
        setFragmentListener()
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun observeSetup(){
        viewModel.bags.observe(viewLifecycleOwner){
            adapterBag.submitList(it)
        }

        viewModel.allBags.observe(viewLifecycleOwner){
            binding.txtPriceTotal.text = "${viewModel.calculatorTotal(it,salePercent)}\$"
            promotions = it
        }
    }

    fun bind(){
        binding.apply {
            appBarLayout.topAppBar.title = "Bag"

            recyclerViewBag.layoutManager = LinearLayoutManager(context)
            recyclerViewBag.adapter = adapterBag

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
                                if (newText!!.isNotEmpty()) {
                                    viewModel.setSearch(newText)
                                }
                                else{
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

            editPromoCode.setOnClickListener {
                val bottomPromotion = BottomPromotion(idPromotion)
                bottomPromotion.show(parentFragmentManager,BottomPromotion.TAG)
            }

            btnRemove.setOnClickListener{
                if(isButtonRemove){
                    isButtonRemove = !isButtonRemove
                    binding.editPromoCode.setText("")
                    salePercent = 0
                    idPromotion = ""
                    binding.txtPriceTotal.text = "${viewModel.calculatorTotal(promotions,salePercent)}\$"
                    btnRemove.setBackgroundResource(R.drawable.btn_arrow_forward)
                }
            }
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

    private fun setFragmentListener() {
        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
            val id = bundle.getString(BUNDLE_KEY_NAME)
            val sale = bundle.getLong(BUNDLE_KEY_SALE)
            salePercent = sale
            idPromotion = id ?: ""
            binding.editPromoCode.setText(idPromotion)
            binding.txtPriceTotal.text = "${viewModel.calculatorTotal(promotions,salePercent)}\$"
            binding.btnRemove.setBackgroundResource(R.drawable.ic_close)
            isButtonRemove = true
        }
    }

    companion object {
        const val REQUEST_KEY = "request"
        const val BUNDLE_KEY_NAME = "bundle_name_promotion"
        const val BUNDLE_KEY_SALE = "bundle_sale"
    }
}