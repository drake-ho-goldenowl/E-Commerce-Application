package com.goldenowl.ecommerceapp.ui.Shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.ItemViewPagerImageProductBinding

class ViewPageImageProduct(private val url: String) : Fragment() {
    private lateinit var binding: ItemViewPagerImageProductBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ItemViewPagerImageProductBinding.inflate(inflater, container, false)

        Glide.with(requireContext())
            .load(url)
            .centerCrop()
            .error(R.drawable.img_default)
            .into(binding.imgProductDetail)

        return binding.root
    }
}