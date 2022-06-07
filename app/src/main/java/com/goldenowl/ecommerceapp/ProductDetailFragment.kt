package com.goldenowl.ecommerceapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.goldenowl.ecommerceapp.adapters.ImageProductAdapter
import com.goldenowl.ecommerceapp.adapters.ListProductGridAdapter
import com.goldenowl.ecommerceapp.adapters.SpinnerAdapter
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.FragmentProductDetailBinding
import com.goldenowl.ecommerceapp.ui.Shop.BottomSheetSize
import com.goldenowl.ecommerceapp.utilities.NetworkHelper
import com.goldenowl.ecommerceapp.viewmodels.ShopViewModel
import com.goldenowl.ecommerceapp.viewmodels.ShopViewModelFactory

class ProductDetailFragment : Fragment() {
    private val viewModel: ShopViewModel by activityViewModels {
        ShopViewModelFactory(
            (activity?.application as EcommerceApplication).database.productDao()
        )
    }
    private lateinit var binding: FragmentProductDetailBinding
    private lateinit var colors: MutableList<String>
    private lateinit var sizes: MutableList<String>

    private var selectSize: Int? = null
    private var selectColor: Int? = null

    private lateinit var idProduct: String
    private lateinit var product: Product

    private lateinit var adapterRelated: ListProductGridAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            idProduct = it.getString(ID_PRODUCT).toString()
        }

        viewModel.setProduct(idProduct)

        adapterRelated = ListProductGridAdapter(this) {
            viewModel.setProduct(it.id)
            binding.nestedScrollView.apply {
                scrollTo(0, 0)
            }
        }

        observeSetup()
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun changePrice() {
        if (selectColor != null && selectSize != null) {
            binding.txtPrice.text = "\$${product.colors[selectColor!!].sizes[selectSize!!].price}"
        }
    }

    private fun observeSetup() {
        viewModel.product.observe(viewLifecycleOwner) {
            product = it
            colors = viewModel.getAllColor()
            sizes = viewModel.getAllSize()
            colors.add(DEFAULT_COLOR)
            sizes.add(DEFAULT_SIZE)
            viewModel.setCategory(product.categoryName)
            bind()
        }

        viewModel.products.observe(viewLifecycleOwner) {
            adapterRelated.submitList(it)
            binding.txtNumberRelated.text = "${it.size} items"
        }
    }

    fun bind() {
        binding.apply {
            MaterialToolbar.title = product.title

            MaterialToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            //Set Detail
            viewPagerImageProduct.apply {
                if (NetworkHelper.isNetworkAvailable(requireContext())) {
                    val adapterImage =
                        ImageProductAdapter(this@ProductDetailFragment, product.images)
                    adapter = adapterImage
                    setCurrentItem(1, false)
                    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageScrollStateChanged(state: Int) {
                            super.onPageScrollStateChanged(state)
                            if (state == ViewPager2.SCROLL_STATE_IDLE) {
                                when (currentItem) {
                                    adapterImage.itemCount - 1 -> setCurrentItem(1, false)
                                    0 -> setCurrentItem(adapterImage.itemCount - 2, false)
                                }
                            }
                        }
                    })
                } else {
                    val images = listOf(product.images[0])
                    val adapterImage = ImageProductAdapter(this@ProductDetailFragment, images)
                    adapter = adapterImage
                }
            }

            txtBrandName.text = product.brandName
            txtTitle.text = product.title
            txtDescription.text = product.description
            ratingBar.rating = product.reviewStars.toFloat()
            txtNumberVote.text = "(${product.numberReviews})"
            txtPrice.text = "\$${product.colors[0].sizes[0].price}"


            //Spinner Size
            val adapterSize = SpinnerAdapter(requireContext(), sizes)
            spinnerSize.adapter = adapterSize
            spinnerSize.setSelection(adapterSize.count)
            spinnerSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectSize = if (position == adapterSize.count) {
                        null
                    } else {
                        position
                    }

                    changePrice()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }


            //Spinner Color
            val adapterColor = SpinnerAdapter(requireContext(), colors)
            spinnerColor.adapter = adapterColor
            spinnerColor.setSelection(adapterColor.count)
            spinnerColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectColor = if (position == adapterColor.count) {
                        null
                    } else {
                        position
                    }

                    changePrice()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }

            recyclerViewProduct.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL, false
            )
            recyclerViewProduct.adapter = adapterRelated


            //Set Button Favorite
            setButtonFavorite(btnFavorite,product.isFavorite)

            btnFavorite.setOnClickListener {
                val bottomSheetSize = BottomSheetSize(product)
                bottomSheetSize.show(parentFragmentManager, BottomSheetSize.TAG)
            }
        }
    }

    private fun setButtonFavorite(buttonView: View, isFavorite: Boolean){
        if (isFavorite) {
            buttonView.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.btn_favorite_active
            )
        }
        else{
            buttonView.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.btn_favorite_no_active
            )
        }
    }

    companion object {
        const val DEFAULT_SIZE = "Size"
        const val DEFAULT_COLOR = "Color"
        const val ID_PRODUCT = "idProduct"
    }
}