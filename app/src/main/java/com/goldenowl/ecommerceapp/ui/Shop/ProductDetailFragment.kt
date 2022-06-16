package com.goldenowl.ecommerceapp.ui.Shop

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.goldenowl.ecommerceapp.adapters.ImageProductAdapter
import com.goldenowl.ecommerceapp.adapters.ListProductGridAdapter
import com.goldenowl.ecommerceapp.adapters.SpinnerAdapter
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.FragmentProductDetailBinding
import com.goldenowl.ecommerceapp.ui.Bag.BottomSheetCart
import com.goldenowl.ecommerceapp.ui.Favorite.BottomSheetFavorite
import com.goldenowl.ecommerceapp.utilities.NetworkHelper
import com.goldenowl.ecommerceapp.viewmodels.ShopViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {
    private val viewModel: ShopViewModel by viewModels()
    private lateinit var binding: FragmentProductDetailBinding
    private lateinit var colors: MutableList<String>
    private lateinit var sizes: MutableList<String>

    private var selectSize: Int? = null
    private var selectColor: Int? = null

    private lateinit var idProduct: String
    private lateinit var product: Product

    private lateinit var adapterRelated: ListProductGridAdapter
    private val handlerFragment = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            idProduct = it.getString(ID_PRODUCT).toString()
        }
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)


        viewModel.setProduct(idProduct)

        adapterRelated = ListProductGridAdapter({
            viewModel.setProduct(it.id)
            binding.nestedScrollView.apply {
                scrollTo(0, 0)
            }
        }, {
            val bottomSheetSize = BottomSheetFavorite(it, null, null)
            bottomSheetSize.show(parentFragmentManager, BottomSheetFavorite.TAG)
        }, { view, product ->
            viewModel.setButtonFavorite(requireContext(), view, product.id)
        })

        observeSetup()
        return binding.root
    }

    fun changePrice() {
        if (selectColor != null && selectSize != null) {
            binding.txtPrice.text = "\$${product.colors[selectColor!!].sizes[selectSize!!].price}"
        }
    }

    private fun observeSetup() {
        viewModel.toastMessage.observe(this.viewLifecycleOwner) { str ->
            Toast.makeText(
                this.context,
                str,
                Toast.LENGTH_SHORT
            ).show()
        }

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

        viewModel.favorites.observe(viewLifecycleOwner) {
            viewModel.setButtonFavorite(requireContext(), binding.btnFavorite, product.id)
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
                val networkHelper = NetworkHelper()
                if (networkHelper.isNetworkAvailable(requireContext())) {
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

                autoScroll()
                //Auto scroll
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        autoScroll()
                    }
                })
            }

            txtBrandName.text = product.brandName
            txtTitle.text = product.title
            txtDescription.text = product.description
            ratingBar.rating = product.reviewStars.toFloat()
            txtNumberVote.text = "(${product.numberReviews})"
            txtPrice.text = "\$${product.colors[0].sizes[0].price}"


            //Spinner Size
            var adapterSize = SpinnerAdapter(requireContext(), sizes)
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
                    if (selectColor != null) {
                        sizes = viewModel.getAllSizeOfColor(selectColor!!)
                        sizes.add(DEFAULT_COLOR)
                        adapterSize = SpinnerAdapter(requireContext(), sizes)
                        spinnerSize.adapter = adapterSize
                        spinnerSize.setSelection(adapterSize.count)
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
            viewModel.setButtonFavorite(requireContext(), btnFavorite, product.id)

            btnFavorite.setOnClickListener {
                val select = selectColor ?: 0
                val bottomSheetSize =
                    BottomSheetFavorite(product, selectSize, product.colors[select].color)
                bottomSheetSize.show(parentFragmentManager, BottomSheetFavorite.TAG)
            }

            btnAddToCart.setOnClickListener {
                if (selectColor != null && selectSize != null) {
                    val bottomSheetCart = BottomSheetCart(product, selectSize!!, selectColor!!)
                    bottomSheetCart.show(parentFragmentManager, BottomSheetCart.TAG)
                } else {
                    if (selectSize == null) {
                        viewModel.toastMessage.postValue("Please select size")
                    } else {
                        viewModel.toastMessage.postValue("Please select color")
                    }
                }
            }
        }
    }


    private fun autoScroll() {
        handlerFragment.removeMessages(0)
        handlerFragment.postDelayed({
            binding.viewPagerImageProduct.setCurrentItem(
                binding.viewPagerImageProduct.currentItem + 1,
                true
            )
        }, 5000)
    }

    override fun onPause() {
        handlerFragment.removeMessages(0)
        super.onPause()
    }

    override fun onResume() {
        autoScroll()
        super.onResume()
    }

    override fun onDestroy() {
        handlerFragment.removeMessages(0)
        super.onDestroy()
    }

    companion object {
        const val DEFAULT_SIZE = "Size"
        const val DEFAULT_COLOR = "Color"
        const val ID_PRODUCT = "idProduct"
    }
}