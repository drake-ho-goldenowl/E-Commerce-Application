package com.goldenowl.ecommerceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.goldenowl.ecommerceapp.databinding.ItemListHomeBinding

class ListHomeAdapter(private val setAdapter: (RecyclerView, TextView, String) -> Unit) :
    RecyclerView.Adapter<ListHomeAdapter.ItemViewHolder>() {
    private var dataSet: List<String> = emptyList()
    fun submitList(list: List<String>) {
        dataSet = list
    }

    class ItemViewHolder(
        private val setAdapter: (RecyclerView, TextView, String) -> Unit,
        private val binding: ItemListHomeBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String) {
            binding.apply {
                txtNameCategory.text = category
                when (category) {
                    SALE -> txtSub.text = SUB_SALE
                    NEW -> txtSub.text = SUB_NEW
                    else -> txtSub.text = ""
                }
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
        when (position) {
            0 -> holder.bind(SALE)
            1 -> holder.bind(NEW)
            else -> holder.bind(dataSet[position])
        }

    }

    override fun getItemCount(): Int {
        return dataSet.size + 2
    }

    companion object {
        const val SALE = "Sale"
        const val NEW = "New"
        const val SUB_SALE = "Super summer sale"
        const val SUB_NEW = "You’ve never seen it before!"
    }
}