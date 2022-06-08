package com.goldenowl.ecommerceapp.ui.Splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.goldenowl.ecommerceapp.databinding.FragmentIntroPageBinding

class IntroPageFragment(private val img: Int,private val nameTopic: String) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentIntroPageBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        binding.txtIntro.text = nameTopic
        binding.imgIntro.setImageResource(img)
        return binding.root
    }
}