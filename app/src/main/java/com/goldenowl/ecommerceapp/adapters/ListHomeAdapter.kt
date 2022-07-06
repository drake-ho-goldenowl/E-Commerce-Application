package com.goldenowl.ecommerceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldenowl.ecommerceapp.databinding.ItemListHomeBinding

class ListHomeAdapter(private val setAdapter: (RecyclerView, TextView, String) -> Unit) :
    ListAdapter<String, ListHomeAdapter.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(
        private val setAdapter: (RecyclerView, TextView, String) -> Unit,
        private val binding: ItemListHomeBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String, position: Int) {
            binding.apply {
                txtNameCategory.text = category
                when (category) {
                    SALE -> txtSub.text = SUB_SALE
                    NEW -> txtSub.text = SUB_NEW
                    else -> txtSub.text = ""
                }
                setAdapter(recyclerView, txtViewAll, category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            setAdapter,
            ItemListHomeBinding.inflate(
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
        holder.bind(current, position)
    }

    companion object {
        const val SALE = "Sale"
        const val NEW = "New"
        const val SUB_SALE = "Super summer sale"
        const val SUB_NEW = "You’ve never seen it before!"
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