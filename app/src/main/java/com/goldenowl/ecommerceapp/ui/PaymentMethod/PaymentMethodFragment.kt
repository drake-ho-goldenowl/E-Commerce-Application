package com.goldenowl.ecommerceapp.ui.PaymentMethod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListCardAdapter
import com.goldenowl.ecommerceapp.databinding.FragmentPaymentMethodBinding
import com.goldenowl.ecommerceapp.ui.General.ConfirmDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMethodFragment : Fragment() {
    private lateinit var binding: FragmentPaymentMethodBinding
    private lateinit var adapter: ListCardAdapter
    private val viewModel: PaymentViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentMethodBinding.inflate(inflater, container, false)
        viewModel.fetchData()
        adapter = ListCardAdapter({ checkBox, card ->
            if (checkBox.isChecked) {
                viewModel.setDefaultPayment(card.id)
                adapter.notifyDataSetChanged()
            } else {
                viewModel.removeDefaultPayment()
                adapter.notifyDataSetChanged()
            }
        }, { checkBox, card ->
            checkBox.isChecked = viewModel.checkDefaultCard(card.id)
        }, {
            ConfirmDialog(this) {
                viewModel.deleteCard(it)
            }.show()
        })

        setupObserve()
        bind()
        return binding.root
    }

    private fun setupObserve() {
        viewModel.apply {
            cards.observe(viewLifecycleOwner) {
                adapter.dataSet = it
                adapter.notifyDataSetChanged()
            }
        }

    }

    private fun bind() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = getString(R.string.payment_methods)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            recyclerViewPaymentCard.adapter = adapter
            recyclerViewPaymentCard.layoutManager = LinearLayoutManager(context)

            btnAddShippingAddress.setOnClickListener {
                val bottomAddPayment = BottomAddPayment()
                bottomAddPayment.show(parentFragmentManager, BottomAddPayment.TAG)
            }
        }
    }
}