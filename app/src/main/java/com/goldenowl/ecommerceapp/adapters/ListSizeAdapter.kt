package com.goldenowl.ecommerceapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.ItemSizeBinding

class ListSizeAdapter(private val onItemClicked: (String) -> Unit) :
    ListAdapter<String, ListSizeAdapter.ItemViewHolder>(DiffCallback) {
    var positionCurrent = -1

    class ItemViewHolder(
        private var context: Context, private var binding: ItemSizeBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String, positionCurrent: Int, position: Int) {
            binding.apply {
                txtSize.text = category

                if (position == positionCurrent) {
                    txtSize.setTextColor(ContextCompat.getColor(context, R.color.white))
                    txtSize.background =
                        ContextCompat.getDrawable(context, R.drawable.btn_size_custom_1)
                } else {
                    txtSize.setTextColor(ContextCompat.getColor(context, R.color.black))
                    txtSize.background =
                        ContextCompat.getDrawable(context, R.drawable.btn_size_custom_2)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            parent.context,
            ItemSizeBinding.inflate(
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