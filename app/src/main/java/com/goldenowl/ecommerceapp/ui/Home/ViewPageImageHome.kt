package com.goldenowl.ecommerceapp.ui.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.goldenowl.ecommerceapp.databinding.ItemViewPagerHomeBinding

class ViewPageImageHome (private val idImage: Int, private val title: String) : Fragment(){
    private lateinit var binding: ItemViewPagerHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ItemViewPagerHomeBinding.inflate(inflater,container,false)

        binding.imgHome.setBackgroundResource(idImage)
        binding.txtTitle.text = title
        return binding.root
    }
}