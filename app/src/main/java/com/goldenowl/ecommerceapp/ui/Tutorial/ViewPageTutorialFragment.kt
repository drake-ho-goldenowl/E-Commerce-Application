package com.goldenowl.ecommerceapp.ui.Tutorial

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.goldenowl.ecommerceapp.adapters.TutorialPagerAdapter
import com.goldenowl.ecommerceapp.databinding.FragmentViewPageTutorialBinding
import com.goldenowl.ecommerceapp.MainActivity
import com.google.android.material.tabs.TabLayoutMediator

class ViewPageTutorialFragment : Fragment() {
    private lateinit var binding: FragmentViewPageTutorialBinding
    private var currentPage = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewPageTutorialBinding.inflate(inflater, container, false)

        binding.viewPager.adapter = TutorialPagerAdapter(this)

        TabLayoutMediator(binding.viewPagerTabs, binding.viewPager) { _, _ -> }.attach()
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        binding.btnNext.visibility = View.VISIBLE
                        binding.btnSkip.visibility = View.VISIBLE
                        binding.btnGetStart.visibility = View.GONE
                        currentPage = 0
                    }
                    1 -> {
                        binding.btnNext.visibility = View.VISIBLE
                        binding.btnSkip.visibility = View.VISIBLE
                        binding.btnGetStart.visibility = View.GONE
                        currentPage = 1
                    }
                    2 -> {
                        binding.btnNext.visibility = View.VISIBLE
                        binding.btnSkip.visibility = View.VISIBLE
                        binding.btnGetStart.visibility = View.GONE
                        currentPage = 2
                    }
                    3 -> {
                        binding.btnNext.visibility = View.GONE
                        binding.btnSkip.visibility = View.GONE
                        binding.btnGetStart.visibility = View.VISIBLE
                        currentPage = 3
                    }
                }
                super.onPageSelected(position)
            }
        })

        binding.btnSkip.setOnClickListener {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }

        binding.btnNext.setOnClickListener {
            if (currentPage < 3) {
                currentPage++
                binding.viewPager.currentItem = currentPage
            }
        }

        binding.btnGetStart.setOnClickListener {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }
        return binding.root
    }


}