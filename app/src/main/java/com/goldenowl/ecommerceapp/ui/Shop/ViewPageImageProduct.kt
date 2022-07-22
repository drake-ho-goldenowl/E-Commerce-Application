package com.goldenowl.ecommerceapp.ui.Shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.goldenowl.ecommerceapp.databinding.ItemViewPagerImageProductBinding
import com.goldenowl.ecommerceapp.utilities.GlideDefault

class ViewPageImageProduct(
    private val url: String,
    private val position: Int,
    private val onImageClick: (Int) -> Unit
    ) : Fragment() {
    private lateinit var binding: ItemViewPagerImageProductBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ItemViewPagerImageProductBinding.inflate(inflater, container, false)
        GlideDefault.show(requireContext(), url, binding.imgProductDetail)
        binding.imgProductDetail.setOnClickListener {
            onImageClick(position)
        }
        return binding.root
    }
}