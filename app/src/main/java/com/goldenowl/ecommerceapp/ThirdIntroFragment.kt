package com.goldenowl.ecommerceapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.goldenowl.ecommerceapp.databinding.FragmentThirdIntroBinding

class ThirdIntroFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentThirdIntroBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }
}