package com.goldenowl.ecommerceapp.ui.LargeImage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.goldenowl.ecommerceapp.databinding.ItemViewPagerImageTouchBinding
import com.goldenowl.ecommerceapp.utilities.GlideDefault

class ViewPageImageTouch(private val url: String) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ItemViewPagerImageTouchBinding.inflate(inflater, container, false)
        GlideDefault.show(requireContext(), url, binding.touchImageView)
        return binding.root
    }
}