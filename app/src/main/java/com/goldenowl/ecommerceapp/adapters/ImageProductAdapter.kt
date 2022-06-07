package com.goldenowl.ecommerceapp.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.goldenowl.ecommerceapp.ViewPageImageProduct

class ImageProductAdapter(fragment: Fragment,listImage: List<String>) :
    FragmentStateAdapter(fragment) {
    private val listImageNew = if(listImage.size == 1){
        listImage
    }
    else{
        listOf(listImage.last()) + listImage + listOf(listImage.first())
    }

    override fun getItemCount(): Int {
        return listImageNew.size
    }

    override fun createFragment(position: Int): Fragment {
        return ViewPageImageProduct(listImageNew[position])
    }


}