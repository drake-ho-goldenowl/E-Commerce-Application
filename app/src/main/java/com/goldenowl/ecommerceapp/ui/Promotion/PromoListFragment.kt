package com.goldenowl.ecommerceapp.ui.Promotion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.ListPromotionAdapter
import com.goldenowl.ecommerceapp.data.TypeSort
import com.goldenowl.ecommerceapp.databinding.FragmentPromoListBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment

class PromoListFragment : BaseFragment() {
    private val viewModel: PromotionViewModel by viewModels()
    private lateinit var binding: FragmentPromoListBinding
    private lateinit var adapter: ListPromotionAdapter
    private val txtFilter = listOf("Date ASC", "Date DES", "Percent ASC", "Percent DES")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPromoListBinding.inflate(inflater, container, false)
        adapter = ListPromotionAdapter {
            viewModel.getPromotion(it.id)
            findNavController().popBackStack(R.id.profileLoginFragment,true)
            findNavController().navigate(R.id.bagFragment)
        }
        setupObserve()
        bind()
        return binding.root
    }

    private fun setupObserve() {
        viewModel.apply {
            promotions.observe(viewLifecycleOwner) {
                binding.nestedScrollView.scrollTo(0,0)
                adapter.submitList(it)
            }
        }
    }

    private fun bind() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = getString(R.string.promo_list)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            recyclerViewPromotion.layoutManager = LinearLayoutManager(context)
            recyclerViewPromotion.adapter = adapter

            appBarLayout.btnFilter.setOnClickListener {
                showMenu(it)
            }
        }

    }

    private fun showMenu(v: View) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(R.menu.menu_filter_promo, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.sortDateAsc -> {
                    binding.appBarLayout.txtNameFilter.text = txtFilter[0]
                    viewModel.filterPromotion(FilterPromotion.DATE, TypeSort.ASCENDING)
                    false
                }
                R.id.sortDateDes -> {
                    binding.appBarLayout.txtNameFilter.text = txtFilter[1]
                    viewModel.filterPromotion(FilterPromotion.DATE, TypeSort.DESCENDING)
                    false
                }
                R.id.sortPercentAsc -> {
                    binding.appBarLayout.txtNameFilter.text = txtFilter[2]
                    viewModel.filterPromotion(FilterPromotion.PERCENT, TypeSort.ASCENDING)
                    false
                }
                R.id.sortPercentDes -> {
                    binding.appBarLayout.txtNameFilter.text = txtFilter[3]
                    viewModel.filterPromotion(FilterPromotion.PERCENT, TypeSort.DESCENDING)
                    false
                }
                else -> false
            }
        }

        popup.setOnDismissListener {
        }
        popup.show()
    }
}