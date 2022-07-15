package com.goldenowl.ecommerceapp.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.goldenowl.ecommerceapp.ui.Home.ViewPageImageHome

class ImageHomeAdapter(fragment: Fragment, listImage: List<String>, title: List<String>) :
    FragmentStateAdapter(fragment) {
    private val listImageNew = listOf(listImage.last()) + listImage + listOf(listImage.first())

    private val listTitle = listOf(title.last()) + title + listOf(title.first())

    override fun getItemCount(): Int {
        return listImageNew.size
    }

    override fun createFragment(position: Int): Fragment {
        return ViewPageImageHome(listImageNew[position], listTitle[position])
    }
}