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
import com.goldenowl.ecommerceapp.adapters.ListProductOrderAdapter
import com.goldenowl.ecommerceapp.data.Order
import com.goldenowl.ecommerceapp.data.ProductOrder
import com.goldenowl.ecommerceapp.databinding.FragmentOrderDetailBinding
import com.goldenowl.ecommerceapp.ui.LoadingDialog
import com.goldenowl.ecommerceapp.viewmodels.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

@AndroidEntryPoint
class OrderDetailFragment : Fragment() {
    private val viewModel: OrderViewModel by activityViewModels()
    private lateinit var binding: FragmentOrderDetailBinding
    private val adapter = ListProductOrderAdapter()
    private lateinit var listProductOrder: List<ProductOrder>
    private val loadingDialog = LoadingDialog(this)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        setupObserve()
        bind()
        return binding.root
    }

    private fun setupObserve() {
        viewModel.apply {
            order.observe(viewLifecycleOwner) {
                listProductOrder = it.products
                setupUI(it)
            }
            dismiss.observe(viewLifecycleOwner) {
                if (it) {
                    loadingDialog.dismiss()
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun bind() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = getString(R.string.order_details)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            btnReOrder.setOnClickListener {
                loadingDialog.startLoading()
                viewModel.reOrder(listProductOrder)
            }
        }
    }

    private fun setupUI(order: Order) {
        binding.apply {
            order.apply {
                txtIdOrder.text = "Order №$id"
                txtTrackingNumber.text = trackingNumber
                val simpleDate = SimpleDateFormat("MM-dd-yyyy")
                timeCreated.let {
                    txtTimeCreated.text = simpleDate.format(it).toString()
                }

                viewModel.setUIStatus(requireContext(), txtStatus, status)
                txtNumberProduct.text = "${products.size} items"
                adapter.submitList(products)
                recyclerViewProduct.adapter = adapter
                recyclerViewProduct.layoutManager = LinearLayoutManager(context)

                txtShippingAddress.text = shippingAddress

                txtNumberCard.text = "**** **** **** $payment"
                if (isTypePayment == 0) {
                    imgLogoCard.setImageResource(R.drawable.ic_mastercard)
                } else {
                    imgLogoCard.setImageResource(R.drawable.ic_visa2)
                }

                txtDeliveryMethod.text = "${delivery?.name}, 3 days, ${delivery?.price}\$"

                viewModel.setUIStatus(requireContext(), txtStatus, status)
                if (promotion == "0") {
                    txtDiscountMethod.text = ""
                } else {
                    txtDiscountMethod.text = "$promotion%, Personal promo code"
                }
                txtTotalAmount.text = "$total\$"
            }
        }

    }
}