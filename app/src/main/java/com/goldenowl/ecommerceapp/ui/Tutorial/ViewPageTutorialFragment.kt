package com.goldenowl.ecommerceapp.ui.Tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.goldenowl.ecommerceapp.adapters.TutorialPagerAdapter
import com.goldenowl.ecommerceapp.databinding.FragmentViewPageTutorialBinding
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
        binding.apply {
            TabLayoutMediator(viewPagerTabs, viewPager) { _, _ -> }.attach()
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    when (position) {
                        0 -> {
                            btnNext.visibility = View.VISIBLE
                            btnSkip.visibility = View.VISIBLE
                            btnGetStart.visibility = View.GONE
                            currentPage = 0
                        }
                        1 -> {
                            btnNext.visibility = View.VISIBLE
                            btnSkip.visibility = View.VISIBLE
                            btnGetStart.visibility = View.GONE
                            currentPage = 1
                        }
                        2 -> {
                            btnNext.visibility = View.VISIBLE
                            btnSkip.visibility = View.VISIBLE
                            btnGetStart.visibility = View.GONE
                            currentPage = 2
                        }
                        3 -> {
                            btnNext.visibility = View.GONE
                            btnSkip.visibility = View.GONE
                            btnGetStart.visibility = View.VISIBLE
                            currentPage = 3
                        }
                    }
                    super.onPageSelected(position)
                }
            })

            btnSkip.setOnClickListener {
                findNavController().popBackStack()
            }

            btnNext.setOnClickListener {
                if (currentPage < 3) {
                    currentPage++
                    binding.viewPager.currentItem = currentPage
                }
            }

            btnGetStart.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        return binding.root
    }


}