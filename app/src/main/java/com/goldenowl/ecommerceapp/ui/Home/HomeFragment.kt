package com.goldenowl.ecommerceapp.ui.Home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ImageHomeAdapter
import com.goldenowl.ecommerceapp.adapters.ListHomeAdapter
import com.goldenowl.ecommerceapp.adapters.ListProductGridAdapter
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.FragmentHomeBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment
import com.goldenowl.ecommerceapp.ui.Favorite.BottomSheetFavorite
import com.goldenowl.ecommerceapp.utilities.IS_FIRST
import com.goldenowl.ecommerceapp.utilities.NetworkHelper
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val listImage = listOf(
        R.drawable.img_home,
        R.drawable.img_home_1,
        R.drawable.img_home_2,
        R.drawable.img_home_3,
        R.drawable.img_home_4
    )
    private val listTitle = listOf(
        "Summer clothes",
        "Street clothes",
        "Sleep clothes",
        "Sport clothes",
        "Inform clothes"
    )
    private lateinit var adapter: ListHomeAdapter
    private var category: List<String> = emptyList()
    private var product: MutableMap<String, List<Product>> = mutableMapOf()
    private var loadMore = false
    private var count = 0
    private val handlerFragment = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Check Tutorial
        val sharedPref = this.activity?.getPreferences(Context.MODE_PRIVATE)
        val isFirst = sharedPref?.getBoolean(IS_FIRST, true);
        if (isFirst == true) {
            sharedPref.edit().putBoolean(IS_FIRST, false).apply()
            findNavController().navigate(R.id.viewPageTutorialFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupAdapter()
        setupObserve()
        setFragmentListener()
        bind()
        return binding.root
    }

    private fun setupAdapter() {
        adapter = ListHomeAdapter { recyclerView, textView, s ->
            val adapterItem = ListProductGridAdapter({
                val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(
                    idProduct = it.id
                )
                findNavController().navigate(action)
            }, { btnFavorite, product ->
                val bottomSheetSize = BottomSheetFavorite(product, null, null)
                bottomSheetSize.show(parentFragmentManager, BottomSheetFavorite.TAG)
                viewModel.btnFavorite.postValue(btnFavorite)
            }, { view, product ->
                viewModel.setButtonFavorite(requireContext(), view, product.id)
            })
            when (s) {
                SALE -> {
                    adapterItem.submitList(product[SALE])
                }
                NEW -> {
                    adapterItem.submitList(product[NEW])
                }
                else -> {
                    loadMore = true
                    adapterItem.submitList(product[s])
                }
            }

            recyclerView.adapter = adapterItem
            recyclerView.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL, false
            )
            textView.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToCatalogFragment(
                    nameCategories = s,
                    nameProduct = null
                )

                findNavController().navigate(action)
            }
        }
    }

    private fun setupObserve() {
        viewModel.apply {
            category.observe(viewLifecycleOwner) {
                this@HomeFragment.category = it
            }

            getSaleProduct().observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    product[SALE] = it
                    loadMore = true
                    adapter.submitList(product.keys.toList())
                    adapter.notifyDataSetChanged()
                }
            }
            getNewProduct().observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    product[NEW] = it
                    loadMore = true
                    adapter.submitList(product.keys.toList())
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun bind() {
        binding.apply {
            recyclerListHome.adapter = adapter
            recyclerListHome.layoutManager = LinearLayoutManager(context)
            adapter.submitList(product.keys.toList())
            progressBar.visibility = View.GONE
            nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
                if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                    if (loadMore) {
                        progressBar.visibility = View.VISIBLE
                        loadMore = false
                        if (count < category.size) {
                            viewModel.getProductWithCategory(category[count])
                                .observe(viewLifecycleOwner) {
                                    if (it.isNotEmpty()) {
                                        product[category[count]] = it
                                        adapter.submitList(product.keys.toList())
                                        adapter.notifyDataSetChanged()
                                        count++
                                    }
                                }
                        } else {
                            progressBar.visibility = View.GONE
                        }
                    }
                }
            })


            //set viewPager
            viewPagerHome.apply {
                val adapterImage: ImageHomeAdapter
                val networkHelper = NetworkHelper()
                if (networkHelper.isNetworkAvailable(requireContext())) {
                    adapterImage =
                        ImageHomeAdapter(this@HomeFragment, listImage, listTitle)
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
        }
    }


    private fun setFragmentListener() {
        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
            val result = bundle.getBoolean(BUNDLE_KEY_IS_FAVORITE, false)
            if (result) {
                viewModel.btnFavorite.value?.let {
                    it.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.btn_favorite_active
                    )
                }
            }
        }
    }


    private fun autoScroll() {
        handlerFragment.removeMessages(0)
        handlerFragment.postDelayed({
            binding.viewPagerHome.setCurrentItem(binding.viewPagerHome.currentItem + 1, true)
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
        const val SALE = "Sale"
        const val NEW = "New"
    }
}