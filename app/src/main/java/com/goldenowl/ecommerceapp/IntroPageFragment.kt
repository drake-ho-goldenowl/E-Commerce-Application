package com.goldenowl.ecommerceapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.goldenowl.ecommerceapp.databinding.FragmentIntroPageBinding

class IntroPageFragment(val img: Int,val nameTopic: String) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentIntroPageBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        binding.txtIntro.text = nameTopic
        binding.imgIntro.setImageResource(img)
        return binding.root
    }
}