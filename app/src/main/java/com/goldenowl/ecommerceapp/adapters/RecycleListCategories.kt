package com.goldenowl.ecommerceapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldenowl.ecommerceapp.databinding.ItemCategoriesBinding


class RecycleListCategories(private val onItemClicked: (String) -> Unit) :
    ListAdapter<String, RecycleListCategories.ItemViewHolder>(DiffCallback) {
    var positionCurrent = -1
    class ItemViewHolder(private val context: Context,private var binding: ItemCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val bind = binding
        fun bind(category: String) {
            binding.apply {
                txtCategory.text = category
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(parent.context,
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
        }
        holder.bind(current)
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