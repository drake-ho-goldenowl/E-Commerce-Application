package com.goldenowl.ecommerceapp.ui.Checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListDeliveryAdapter
import com.goldenowl.ecommerceapp.data.*
import com.goldenowl.ecommerceapp.databinding.FragmentCheckoutBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment
import com.goldenowl.ecommerceapp.utilities.Notification
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class CheckoutFragment : BaseFragment() {
    private lateinit var binding: FragmentCheckoutBinding
    private lateinit var deliveryAdapter: ListDeliveryAdapter
    private val listDelivery: MutableList<Delivery> = mutableListOf()
    private val viewModel: CheckoutViewModel by viewModels()
    private var promotion: Promotion? = null
    private var bags: List<BagAndProduct> = emptyList()
    private var totalOrder: Int = 0
    private var summary: Int = 0
    private var delivery: Delivery? = null
    private var address: ShippingAddress? = null
    private var card: Card? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // add data Delivery
        listDelivery.add(Delivery(R.drawable.ic_fedex, "FedEx", 15F))
        listDelivery.add(Delivery(R.drawable.ic_usps, "USPS", 10F))
        listDelivery.add(Delivery(R.drawable.ic_dhl, "DHL", 50F))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let { it ->
            val idPromotion = it.getString(ID_PROMOTION).toString()
            viewModel.getPromotion(idPromotion).observe(viewLifecycleOwner) { promo ->
                promotion = promo
                totalOrder = viewModel.calculatorTotalOrder(bags, promo.salePercent)
                setPrice()
            }
        }
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        setupObserve()
        bind()
        return binding.root
    }

    private fun setupObserve() {
        viewModel.apply {
            setIdAddressDefault()
            setIdPaymentDefault()

            bag.observe(viewLifecycleOwner) {
                bags = it
                totalOrder = viewModel.calculatorTotalOrder(it)
                promotion.value?.let { promo ->
                    totalOrder = viewModel.calculatorTotalOrder(it, promo.salePercent)
                }
                setPrice()
            }

            shippingAddress.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it.address.isBlank()) {
                        layoutShippingAddress.visibility = View.INVISIBLE
                        btnAddShippingAddress.visibility = View.VISIBLE
                    } else {
                        layoutShippingAddress.visibility = View.VISIBLE
                        btnAddShippingAddress.visibility = View.GONE
                        setupShippingAddress(it)
                    }
                }
                address = it
            }

            payment.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it == null || it.id.isBlank()) {
                        itemPayment.visibility = View.INVISIBLE
                        btnAddPayment.visibility = View.VISIBLE
                    } else if (it.number[0] == '4') {
                        itemPayment.visibility = View.VISIBLE
                        btnAddPayment.visibility = View.GONE
                        imgLogoCard.setImageResource(R.drawable.ic_mastercard)
                        txtNumberCard.text =
                            "* * * *  * * * *  * * * *  ${it.number.substring(it.number.length - 4)}"
                        card = it
                    } else if (it.number[0] == '5') {
                        itemPayment.visibility = View.VISIBLE
                        btnAddPayment.visibility = View.GONE
                        imgLogoCard.setImageResource(R.drawable.ic_visa2)
                        txtNumberCard.text =
                            "* * * *  * * * *  * * * *  ${it.number.substring(it.number.length - 4)}"
                        card = it
                    }
                }
            }

            toastMessage.observe(viewLifecycleOwner) {
                toastMessage(it)
                toastMessage.postValue("")
            }
            isLoading.observe(viewLifecycleOwner){
                setLoading(it)
            }
        }
    }


    fun bind() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = getString(R.string.checkout)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            deliveryAdapter = ListDeliveryAdapter {
                delivery = if (delivery == it) {
                    null
                } else {
                    it
                }
                setPrice()
            }
            deliveryAdapter.submitList(listDelivery)
            recyclerViewDelivery.adapter = deliveryAdapter
            recyclerViewDelivery.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL, false
            )


            btnSubmitOrder.setOnClickListener {
                viewModel.submitOrder(bags, address, card, summary, delivery, promotion)
                    .observe(viewLifecycleOwner){
                        if (it) {
                            Notification(requireContext()).notify(
                                getString(R.string.notification), getString(
                                    R.string.order_success
                                )
                            )
                            viewModel.removeAllBag()
                            viewModel.isLoading.postValue(false)
                            findNavController().navigate(R.id.successFragment)
                        }
                    }
            }

            txtChangeAddress.setOnClickListener {
                findNavController().navigate(R.id.shippingAddressFragment)
            }

            btnAddShippingAddress.setOnClickListener {
                findNavController().navigate(R.id.shippingAddressFragment)
            }

            txtChangePayment.setOnClickListener {
                findNavController().navigate(R.id.paymentMethodFragment)
            }

            btnAddPayment.setOnClickListener {
                findNavController().navigate(R.id.paymentMethodFragment)
            }

        }
    }

    private fun setPrice() {
        binding.apply {
            var priceDelivery = 0
            delivery?.let {
                priceDelivery = it.price.roundToInt()
            }
            summary = totalOrder + priceDelivery
            txtOrderPrice.text = "${totalOrder}\$"
            txtDeliveryPrice.text = "${priceDelivery}\$"
            txtPriceTotal.text = "${summary}\$"
        }
    }

    private fun setupShippingAddress(shippingAddress: ShippingAddress) {
        binding.apply {
            shippingAddress.apply {
                txtName.text = fullName
                txtAddress.text = "$address\n$city, $state $zipCode, $country"
            }
        }
    }
}