package com.goldenowl.ecommerceapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.adapters.RecycleListCategories
import com.goldenowl.ecommerceapp.adapters.RecycleListVertical
import com.goldenowl.ecommerceapp.databinding.FragmentShopBinding

class ShopFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentShopBinding.inflate(inflater,container,false)
        val dataSet = arrayListOf("hi","hi","hi","hi","hi")
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(
            this.context,
            LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewCategories.adapter = RecycleListCategories(dataSet)

        binding.recyclerViewProduct.layoutManager = LinearLayoutManager(
            this.context,
            LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewProduct.adapter = RecycleListVertical(dataSet)

        return binding.root
    }

}