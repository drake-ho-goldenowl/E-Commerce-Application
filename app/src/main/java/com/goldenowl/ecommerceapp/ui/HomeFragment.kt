package com.goldenowl.ecommerceapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldenowl.ecommerceapp.adapters.RecycleListHorizonal
import com.goldenowl.ecommerceapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        val dataSet = arrayListOf("hi","hi","hi","hi","hi")
        binding.recyclerViewSale.layoutManager = LinearLayoutManager(
            this.context,
            LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewSale.adapter = RecycleListHorizonal(dataSet)

        binding.recyclerViewNew.layoutManager = LinearLayoutManager(
            this.context,
            LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewNew.adapter = RecycleListHorizonal(dataSet)
        return binding.root
    }
}