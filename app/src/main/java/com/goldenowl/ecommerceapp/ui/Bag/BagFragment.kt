package com.goldenowl.ecommerceapp.ui.Bag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListBagAdapter
import com.goldenowl.ecommerceapp.databinding.FragmentBagBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment
import com.goldenowl.ecommerceapp.ui.Promotion.BottomPromotion
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BagFragment : BaseFragment() {
    private lateinit var binding: FragmentBagBinding
    private val viewModel: BagViewModel by activityViewModels()
    private lateinit var adapterBag: ListBagAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!viewModel.isLogged()) {
            findNavController().navigate(R.id.action_bagFragment_to_warningFragment)
        } else {
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBagBinding.inflate(inflater, container, false)

        observeSetup()
        bind()
        return binding.root
    }

    private fun observeSetup() {
        viewModel.apply {
            bagAndProduct.observe(viewLifecycleOwner) {
                val sale = promotion.value?.salePercent ?: 0
                calculatorTotal(it, sale)
                adapterBag.submitList(it)
                isLoading.postValue(false)
            }

            toastMessage.observe(viewLifecycleOwner) {
                toastMessage(it)
                toastMessage.postValue("")
            }

            totalPrice.observe(viewLifecycleOwner) {
                binding.txtPriceTotal.text = "${it}\$"
            }

            promotion.observe(viewLifecycleOwner) {
                if (it.id.isNotBlank()) {
                    viewModel.isRemoveButton.postValue(true)
                } else {
                    viewModel.isRemoveButton.postValue(false)
                }
                viewModel.fetchBag()
            }

            isRemoveButton.observe(viewLifecycleOwner) {
                setButtonRemove(it)
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
            appBarLayout.MaterialToolbar.setOnMenuItemClickListener { it ->
                when (it.itemId) {
                    R.id.ic_search -> {
                        val searchView = it.actionView as SearchView
                        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                return true
                            }

                            override fun onQueryTextChange(newText: String?): Boolean {
                                viewModel.bagAndProduct.value?.let { list ->
                                    if (!newText.isNullOrEmpty()) {
                                        val newList = list.filter { bagAndProduct ->
                                            bagAndProduct.product
                                                .title
                                                .lowercase()
                                                .contains(newText.lowercase())
                                        }
                                        adapterBag.submitList(newList)
                                    } else {
                                        adapterBag.submitList(list)
                                    }
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
                val bottomPromotion = BottomPromotion()
                bottomPromotion.show(parentFragmentManager, BottomPromotion.TAG)
            }

            btnRemove.setOnClickListener {
                viewModel.isRemoveButton.value?.let {
                    if (it) {
                        viewModel.getPromotion("")
                    }
                }
            }

            btnCheckOut.setOnClickListener {
                viewModel.bagAndProduct.value?.let { list ->
                    if (list.isEmpty()) {
                        viewModel.toastMessage.postValue(ALERT_CHECKOUT)
                    } else {
                        findNavController().navigate(R.id.checkoutFragment)
                    }
                }
            }
        }
    }

    private fun setButtonRemove(isButtonRemove: Boolean) {
        if (isButtonRemove) {
            binding.editPromoCode.setText(viewModel.promotion.value?.id ?: "")
            binding.btnRemove.setBackgroundResource(R.drawable.ic_close)
        } else {
            binding.editPromoCode.setText("")
            binding.btnRemove.setBackgroundResource(R.drawable.btn_arrow_forward)
        }
    }

    companion object {
        const val ALERT_CHECKOUT = "Please choose one product"
    }
}