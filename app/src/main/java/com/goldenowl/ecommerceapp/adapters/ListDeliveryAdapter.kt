package com.goldenowl.ecommerceapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.Delivery
import com.goldenowl.ecommerceapp.databinding.ItemDeliveryBinding

class ListDeliveryAdapter(private val onItemClicked: (Delivery) -> Unit) :
    ListAdapter<Delivery, ListDeliveryAdapter.ItemViewHolder>(DiffCallback) {
    var positionCurrent = -1

    class ItemViewHolder(
        private val context: Context,
        private var binding: ItemDeliveryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(delivery: Delivery,positionCurrent: Int, position: Int) {
            binding.apply {
                imgLogo.setImageResource(delivery.logo)
                if (position == positionCurrent){
                    layoutItem.background = ContextCompat.getDrawable(context, R.drawable.round_corner)
                }
                else{
                    layoutItem.background = null
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(parent.context,
            ItemDeliveryBinding.inflate(
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
            onItemClicked(current)
            positionCurrent = if(positionCurrent == position){
                -1
            } else{
                position
            }
            notifyDataSetChanged()
        }
        holder.bind(current,positionCurrent,position)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Delivery>() {
            override fun areItemsTheSame(oldItem: Delivery, newItem: Delivery): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Delivery, newItem: Delivery): Boolean {
                return oldItem == newItem
            }
        }
    }
}