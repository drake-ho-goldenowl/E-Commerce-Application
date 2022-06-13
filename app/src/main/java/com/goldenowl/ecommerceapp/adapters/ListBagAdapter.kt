package com.goldenowl.ecommerceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.BagAndProduct
import com.goldenowl.ecommerceapp.databinding.ItemBagBinding

class ListBagAdapter(
    private val onItemClicked: (BagAndProduct) -> Unit
) :
    ListAdapter<BagAndProduct, ListBagAdapter.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(private var binding: ItemBagBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bagAndProduct: BagAndProduct) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(bagAndProduct.product.images[0])
                    .error(R.drawable.img_sample_2)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProduct)
                txtName.text = bagAndProduct.product.title
                txtQuantity.text = bagAndProduct.bag.quantity.toString()

            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemBagBinding.inflate(
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
        }
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<BagAndProduct>() {
            override fun areItemsTheSame(
                oldProduct: BagAndProduct,
                newProduct: BagAndProduct
            ): Boolean {
                return newProduct === oldProduct
            }

            override fun areContentsTheSame(
                oldProduct: BagAndProduct,
                newProduct: BagAndProduct
            ): Boolean {
                return newProduct == oldProduct
            }
        }
    }


}