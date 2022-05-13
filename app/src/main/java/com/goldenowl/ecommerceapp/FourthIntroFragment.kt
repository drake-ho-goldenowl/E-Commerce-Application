package com.goldenowl.ecommerceapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.goldenowl.ecommerceapp.databinding.FragmentFourthIntroBinding
class FourthIntroFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFourthIntroBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }
}