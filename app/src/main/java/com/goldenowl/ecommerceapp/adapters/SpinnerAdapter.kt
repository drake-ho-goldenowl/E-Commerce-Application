package com.goldenowl.ecommerceapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.goldenowl.ecommerceapp.databinding.ItemSpinnerBinding

class SpinnerAdapter(context: Context, private val list: List<String>) :
    ArrayAdapter<String>(context, 0, list) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemSpinnerBinding.inflate(
            LayoutInflater.from(
                parent.context
            ),
            parent,
            false
        )
        binding.txtNameItem.text = list[position]
        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemSpinnerBinding.inflate(
            LayoutInflater.from(
                parent.context
            ),
            parent,
            false
        )

        binding.txtNameItem.text = list[position]
        return binding.root
    }

    override fun getCount(): Int {
        return list.size
    }
}
