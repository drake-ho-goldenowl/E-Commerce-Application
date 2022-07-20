package com.goldenowl.ecommerceapp.ui.Promotion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.adapters.ListPromotionAdapter
import com.goldenowl.ecommerceapp.databinding.FragmentPromoListBinding
import com.goldenowl.ecommerceapp.ui.BaseFragment

class PromoListFragment : BaseFragment() {
    private val viewModel: PromotionViewModel by viewModels()
    private lateinit var binding: FragmentPromoListBinding
    private lateinit var adapter: ListPromotionAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPromoListBinding.inflate(inflater, container, false)
        adapter = ListPromotionAdapter {}
        setupObserve()
        bind()
        return binding.root
    }

    private fun setupObserve() {
        viewModel.apply {
            promotions.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
        }
    }

    private fun bind() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = "Promo List"
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            recyclerViewPromotion.layoutManager = LinearLayoutManager(context)
            recyclerViewPromotion.adapter = adapter
        }

    }
}