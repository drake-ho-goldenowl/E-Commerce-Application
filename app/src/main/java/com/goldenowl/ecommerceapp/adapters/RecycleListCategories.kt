package com.goldenowl.ecommerceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldenowl.ecommerceapp.databinding.ItemCategoriesBinding

class RecycleListCategories(private val onItemClicked: (String) -> Unit) :
    ListAdapter<String, RecycleListCategories.ItemViewHolder>(DiffCallback) {
    var positionCurrent = -1

    class ItemViewHolder(private var binding: ItemCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: String, position: Int, currentPosition: Int) {
            binding.apply {
                txtCategory.text = category
//                if (currentPosition == position) {
//                    txtCategory.setTextColor(R.color.white)
//                    layoutItemCategory.setBackgroundResource(R.drawable.btn_custom5)
//                } else {
//                    txtCategory.setTextColor(R.color.white)
//                    layoutItemCategory.setBackgroundResource(R.drawable.btn_custom4)
//                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemCategoriesBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current.toString())
            positionCurrent = position
            println(positionCurrent)
        }

        holder.bind(current, position, positionCurrent)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }


}