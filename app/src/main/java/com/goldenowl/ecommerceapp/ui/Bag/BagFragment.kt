package com.goldenowl.ecommerceapp.ui.Bag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListBagAdapter
import com.goldenowl.ecommerceapp.data.BagAndProduct
import com.goldenowl.ecommerceapp.databinding.FragmentBagBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment
import com.goldenowl.ecommerceapp.ui.Promotion.BottomPromotion
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BagFragment : BaseFragment() {
    private lateinit var binding: FragmentBagBinding
    private val viewModel: BagViewModel by viewModels()

    private lateinit var adapterBag: ListBagAdapter
    private var allBag: List<BagAndProduct> = emptyList()
    private var idPromotion: String = ""
    private var isButtonRemove = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!viewModel.isLogged()) {
            findNavController().navigate(R.id.action_bagFragment_to_warningFragment)
        }
        else{
            viewModel.isLoading.postValue(true)
        }
        viewModel.fetchBag()
        adapterBag = ListBagAdapter({
            val action = BagFragmentDirections.actionBagFragmentToProductDetailFragment(
                idProduct = it.product.id
            )
            findNavController().navigate(action)
        }, {
            viewModel.insertFavorite(it.product.id, it.bag.size, it.bag.color)
        }, {
            viewModel.removeBagFirebase(it.bag)
        }, { list, textview ->
            viewModel.plusQuantity(list, textview)
        }, { list, textview ->
            viewModel.minusQuantity(list, textview)
        })
        adapterBag.submitList(allBag)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBagBinding.inflate(inflater, container, false)

        observeSetup()
        bind()
        setFragmentListener()
        return binding.root
    }

    private fun observeSetup() {
        viewModel.apply {
            bagAndProduct.observe(viewLifecycleOwner) {
                calculatorTotal(it, sale.value ?: 0)
                adapterBag.submitList(it)
                allBag = it
                isLoading.postValue(false)
            }

            toastMessage.observe(viewLifecycleOwner) {
                toastMessage(it)
                toastMessage.postValue("")
            }

            totalPrice.observe(viewLifecycleOwner) {
                binding.txtPriceTotal.text = "${it}\$"
            }

            sale.observe(viewLifecycleOwner) {
                viewModel.fetchBag()
            }

            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }
        }
    }

    fun bind() {
        binding.apply {
            appBarLayout.topAppBar.title = getString(R.string.bag)

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
                                if (!newText.isNullOrEmpty()) {
                                    adapterBag.submitList(allBag.filter { bag ->
                                        bag.product.title.lowercase().contains(newText.lowercase())
                                    })
                                } else {
                                    adapterBag.submitList(allBag)
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
                bottomPromotion.show(parentFragmentManager, BottomPromotion.TAG)
            }

            btnRemove.setOnClickListener {
                if (isButtonRemove) {
                    isButtonRemove = !isButtonRemove
                    binding.editPromoCode.setText("")
                    idPromotion = ""
                    viewModel.sale.postValue(0)
                    btnRemove.setBackgroundResource(R.drawable.btn_arrow_forward)
                }
            }

            btnCheckOut.setOnClickListener {
                if (allBag.isEmpty()) {
                    viewModel.toastMessage.postValue(ALERT_CHECKOUT)
                } else {
                    val action = BagFragmentDirections.actionBagFragmentToCheckoutFragment(
                        idPromotion = idPromotion
                    )
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun setFragmentListener() {
        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
            val id = bundle.getString(BUNDLE_KEY_NAME_PROMOTION)
            val sale = bundle.getLong(BUNDLE_KEY_SALE)
            idPromotion = id ?: ""
            viewModel.sale.postValue(sale)

            binding.editPromoCode.setText(idPromotion)
            binding.btnRemove.setBackgroundResource(R.drawable.ic_close)
            isButtonRemove = true
        }
    }

    companion object {
        const val ALERT_CHECKOUT = "Please choose one product"
    }
}