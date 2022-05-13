package com.goldenowl.ecommerceapp.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.goldenowl.ecommerceapp.FirstIntroFragment
import com.goldenowl.ecommerceapp.FourthIntroFragment
import com.goldenowl.ecommerceapp.SecondIntroFragment
import com.goldenowl.ecommerceapp.ThirdIntroFragment
import java.lang.IndexOutOfBoundsException

const val INTRODUCTION_1 = 0
const val INTRODUCTION_2 = 1
const val INTRODUCTION_3 = 2
const val INTRODUCTION_4 = 3


class TutorialPagerAdapter(fragment: Fragment) :FragmentStateAdapter(fragment){
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        INTRODUCTION_1 to { FirstIntroFragment() },
        INTRODUCTION_2 to { SecondIntroFragment() },
        INTRODUCTION_3 to { ThirdIntroFragment() },
        INTRODUCTION_4 to { FourthIntroFragment() },
    )

    override fun getItemCount(): Int {
        return tabFragmentsCreators.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?:throw IndexOutOfBoundsException()
    }
}