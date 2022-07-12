package com.goldenowl.ecommerceapp.ui.Order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListOrderAdapter
import com.goldenowl.ecommerceapp.databinding.ItemViewPagerListOrderBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemListOrder(private val statusType: Int) : Fragment() {
    private val viewModel: OrderViewModel by activityViewModels()
    private lateinit var adapter: ListOrderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ListOrderAdapter({
            viewModel.setIdOrder(it)
            viewModel.order.observe(viewLifecycleOwner) { order ->
                if (it == order.id) {
                    viewModel.dismiss.postValue(false)
                    findNavController().navigate(R.id.orderDetailFragment)
                }
            }
        }, { order, textView ->
            viewModel.setUIStatus(requireContext(), textView, order.status)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ItemViewPagerListOrderBinding.inflate(inflater, container, false)

        viewModel.getOrderStatus(statusType).observe(viewLifecycleOwner) { data ->
            adapter.submitList(data)
        }

        binding.apply {
            recyclerViewOrder.adapter = adapter
            recyclerViewOrder.layoutManager = LinearLayoutManager(context)
        }

        return binding.root
    }
}