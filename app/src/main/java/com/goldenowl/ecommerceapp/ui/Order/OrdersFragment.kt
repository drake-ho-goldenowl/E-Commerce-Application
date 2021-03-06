package com.goldenowl.ecommerceapp.ui.Order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.adapters.StatusPagerAdapter
import com.goldenowl.ecommerceapp.databinding.FragmentOrdersBinding
import com.goldenowl.ecommerceapp.ui.BaseViewModel.Companion.statuses
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrdersFragment : Fragment() {
    private lateinit var binding: FragmentOrdersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        bind()
        return binding.root
    }

    private fun bind() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = getString(R.string.my_orders)
            viewPager.adapter = StatusPagerAdapter(this@OrdersFragment)
            TabLayoutMediator(viewPagerTabs, viewPager) { tab, position ->
                tab.text = statuses[position]
            }.attach()

            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}