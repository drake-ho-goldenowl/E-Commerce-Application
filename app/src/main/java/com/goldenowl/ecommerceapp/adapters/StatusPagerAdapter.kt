package com.goldenowl.ecommerceapp.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.goldenowl.ecommerceapp.ui.Order.ItemListOrder


class StatusPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        DELIVERED to { ItemListOrder(DELIVERED) },
        PROCESSING to { ItemListOrder(PROCESSING) },
        CANCELLED to { ItemListOrder(CANCELLED) },
    )

    override fun getItemCount(): Int {
        return tabFragmentsCreators.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

    companion object {
        const val DELIVERED = 0
        const val PROCESSING = 1
        const val CANCELLED = 2
    }
}