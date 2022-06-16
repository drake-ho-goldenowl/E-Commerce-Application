package com.goldenowl.ecommerceapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.goldenowl.ecommerceapp.databinding.FragmentRatingProductBinding

class RatingProductFragment : Fragment() {
    private lateinit var binding: FragmentRatingProductBinding
    private lateinit var idProduct: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        arguments?.let {
            idProduct = it.getString(ID_PRODUCT).toString()
        }
        binding = FragmentRatingProductBinding.inflate(inflater,container,false)
        return binding.root
    }

    companion object {
        const val ID_PRODUCT = "idProduct"
    }
}