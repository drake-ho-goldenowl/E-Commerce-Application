package com.goldenowl.ecommerceapp.ui.ShippingAddress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListAddressAdapter
import com.goldenowl.ecommerceapp.databinding.FragmentShippingAddressBinding
import com.goldenowl.ecommerceapp.ui.General.ConfirmDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShippingAddressFragment : Fragment() {
    private lateinit var binding: FragmentShippingAddressBinding
    private lateinit var listAddressAdapter: ListAddressAdapter
    private val viewModel: ShippingAddressViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchAddress()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShippingAddressBinding.inflate(inflater, container, false)

        listAddressAdapter = ListAddressAdapter({ checkBox, shippingAddress ->
            checkBox.isChecked =
                viewModel.checkDefaultShippingAddress(shippingAddress.id.toString())
        }, { checkBox, shippingAddress ->
            if (checkBox.isChecked) {
                viewModel.setDefaultAddress(shippingAddress.id.toString())
                listAddressAdapter.notifyDataSetChanged()
            } else {
                viewModel.removeDefaultAddress()
                listAddressAdapter.notifyDataSetChanged()
            }
        }, {
            val action =
                ShippingAddressFragmentDirections.actionShippingAddressFragmentToAddShippingAddressFragment(
                    idAddress = it.id.toString()
                )
            findNavController().navigate(action)
        }, {
            ConfirmDialog(this) {
                viewModel.deleteShippingAddress(it)
            }.show()
        })

        setupObserve()
        bind()
        return binding.root
    }

    private fun setupObserve() {
        viewModel.listAddress.observe(viewLifecycleOwner) {
            listAddressAdapter.submitList(it)
        }
    }

    private fun bind() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = getString(R.string.shipping_addresses)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            recyclerViewAddress.adapter = listAddressAdapter
            recyclerViewAddress.layoutManager = LinearLayoutManager(context)

            btnAddShippingAddress.setOnClickListener {
                findNavController().navigate(R.id.addShippingAddressFragment)
            }
        }
    }
}