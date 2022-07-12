package com.goldenowl.ecommerceapp.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.ui.Tutorial.IntroPageFragment


const val INTRODUCTION_1 = 0
const val INTRODUCTION_2 = 1
const val INTRODUCTION_3 = 2
const val INTRODUCTION_4 = 3


class TutorialPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        INTRODUCTION_1 to { IntroPageFragment(R.drawable.ic_intro1, "Introduction 1") },
        INTRODUCTION_2 to { IntroPageFragment(R.drawable.ic_intro2, "Introduction 2") },
        INTRODUCTION_3 to { IntroPageFragment(R.drawable.ic_intro3, "Introduction 3") },
        INTRODUCTION_4 to { IntroPageFragment(R.drawable.ic_intro4, "Introduction 4") },
    )

    override fun getItemCount(): Int {
        return tabFragmentsCreators.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}