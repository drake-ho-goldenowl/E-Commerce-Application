package com.goldenowl.ecommerceapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.ItemCategoriesBinding


class ListCategoriesAdapter(private val onItemClicked: (String) -> Unit) :
    ListAdapter<String, ListCategoriesAdapter.ItemViewHolder>(DiffCallback) {
    var positionCurrent = -1

    class ItemViewHolder(
        private val context: Context,
        private var binding: ItemCategoriesBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String, positionCurrent: Int, position: Int) {
            binding.apply {
                txtCategory.text = category
                if (position == positionCurrent) {
                    txtCategory.setTextColor(ContextCompat.getColor(context, R.color.black))
                    layoutItemCategory.background =
                        ContextCompat.getDrawable(context, R.drawable.btn_custom5)
                } else {
                    txtCategory.setTextColor(ContextCompat.getColor(context, R.color.white))
                    layoutItemCategory.background =
                        ContextCompat.getDrawable(context, R.drawable.btn_custom4)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            parent.context,
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
            positionCurrent = if (positionCurrent == position) {
                -1
            } else {
                position
            }
            notifyDataSetChanged()
        }

        holder.bind(current, positionCurrent, position)
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