package com.goldenowl.ecommerceapp.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.goldenowl.ecommerceapp.ui.Shop.ViewPageImageProduct

class ImageProductAdapter(
    private val fragment: Fragment,
    private val listImage: List<String>,
    private val onClickImage: (Int) -> Unit
) :
    FragmentStateAdapter(fragment) {
    private val listImageNew = if (listImage.size == 1) {
        listImage
    } else {
        listOf(listImage.last()) + listImage + listOf(listImage.first())
    }

    override fun getItemCount(): Int {
        return listImageNew.size
    }

    override fun createFragment(position: Int): Fragment {
        return ViewPageImageProduct(listImageNew[position], position - 1, onClickImage)
    }
}