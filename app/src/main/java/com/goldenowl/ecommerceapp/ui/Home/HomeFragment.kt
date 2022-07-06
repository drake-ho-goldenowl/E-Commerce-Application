package com.goldenowl.ecommerceapp.ui.Home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ImageHomeAdapter
import com.goldenowl.ecommerceapp.adapters.ListHomeAdapter
import com.goldenowl.ecommerceapp.adapters.ListProductGridAdapter
import com.goldenowl.ecommerceapp.databinding.FragmentHomeBinding
import com.goldenowl.ecommerceapp.ui.Favorite.BottomSheetFavorite
import com.goldenowl.ecommerceapp.utilities.IS_FIRST
import com.goldenowl.ecommerceapp.utilities.NetworkHelper
import com.goldenowl.ecommerceapp.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
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

    private lateinit var adapterSale: ListProductGridAdapter
    private lateinit var adapterNew: ListProductGridAdapter
    private lateinit var adapter: ListHomeAdapter
    private val handlerFragment = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Check Tutorial
        val sharedPref = this.activity?.getPreferences(Context.MODE_PRIVATE)
        val isFirst = sharedPref?.getBoolean(IS_FIRST, true);
        if (isFirst == true) {
            sharedPref.edit().putBoolean(IS_FIRST, false).apply()
            findNavController().navigate(R.id.viewPageTutorialFragment)
        }

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)


//        adapterSale = ListProductGridAdapter({
//            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(
//                idProduct = it.id
//            )
//            findNavController().navigate(action)
//        }, {
//            val bottomSheetSize = BottomSheetFavorite(it, null, null)
//            bottomSheetSize.show(parentFragmentManager, BottomSheetFavorite.TAG)
//        }, { view, product ->
//            viewModel.setButtonFavorite(requireContext(), view, product.id)
//        })
//
//        adapterNew = ListProductGridAdapter({
//            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(
//                idProduct = it.id
//            )
//            findNavController().navigate(action)
//        }, {
//            val bottomSheetSize = BottomSheetFavorite(it, null, null)
//            bottomSheetSize.show(parentFragmentManager, BottomSheetFavorite.TAG)
//        }, { view, product ->
//            viewModel.setButtonFavorite(requireContext(), view, product.id)
//        })

        adapter = ListHomeAdapter { recyclerView, textView, s ->
            val adapterItem = ListProductGridAdapter({
                val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(
                    idProduct = it.id
                )
                findNavController().navigate(action)
            }, {
                val bottomSheetSize = BottomSheetFavorite(it, null, null)
                bottomSheetSize.show(parentFragmentManager, BottomSheetFavorite.TAG)
            }, { view, product ->
                viewModel.setButtonFavorite(requireContext(), view, product.id)
            })
            when (s) {
                SALE -> {
                    viewModel.product.observe(viewLifecycleOwner){
                        adapterItem.submitList(viewModel.filterSale(it))
                    }
                }
                NEW -> {
                    viewModel.product.observe(viewLifecycleOwner){
                        adapterItem.submitList(viewModel.filterNew(it))
                    }
                }
                else -> {

                }
            }

            recyclerView.adapter = adapterItem
            recyclerView.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL, false
            )
        }


//        viewModel.product.observe(viewLifecycleOwner) {
//            adapterSale.submitList(viewModel.filterSale(it))
//            adapterNew.submitList(viewModel.filterNew(it))
//        }
//
//        viewModel.favorites.observe(viewLifecycleOwner) {
//            adapterSale.notifyDataSetChanged()
//            adapterNew.notifyDataSetChanged()
//        }


        bind()
        return binding.root
    }

    private fun bind() {
        binding.apply {
//            recyclerViewSale.adapter = adapterSale
//
//            recyclerViewSale.layoutManager = LinearLayoutManager(
//                context,
//                LinearLayoutManager.HORIZONTAL, false
//            )
//
//            recyclerViewNew.adapter = adapterNew
//
//            recyclerViewNew.layoutManager = LinearLayoutManager(
//                context,
//                LinearLayoutManager.HORIZONTAL, false
//            )
//            recyclerListHome.adapter = adapterSale

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

//            txtViewAllSale.setOnClickListener {
//                val action = HomeFragmentDirections.actionHomeFragmentToCatalogFragment(
//                    nameCategories = ""
//                )
//
//                findNavController().navigate(action)
//            }
//
//            txtViewAllNew.setOnClickListener {
//                val action = HomeFragmentDirections.actionHomeFragmentToCatalogFragment(
//                    nameCategories = ""
//                )
//                findNavController().navigate(action)
//            }

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