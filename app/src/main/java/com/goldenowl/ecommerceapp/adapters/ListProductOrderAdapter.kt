package com.goldenowl.ecommerceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.ProductOrder
import com.goldenowl.ecommerceapp.databinding.ItemProductOrderBinding

class ListProductOrderAdapter :
    ListAdapter<ProductOrder, ListProductOrderAdapter.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(private var binding: ItemProductOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(product: ProductOrder) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(product.image)
                    .error(R.drawable.img_sample_2)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProduct)
                txtName.text = product.title
                txtBrandName.text = product.brandName
                txtColorInput.text = product.color
                txtSizeInput.text = product.size
                txtPrice.text = "${product.price}\$"
                txtUnit.text = product.units.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemProductOrderBinding.inflate(
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
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ProductOrder>() {
            override fun areItemsTheSame(
                oldProduct: ProductOrder,
                newProduct: ProductOrder
            ): Boolean {
                return newProduct === oldProduct
            }

            override fun areContentsTheSame(
                oldProduct: ProductOrder,
                newProduct: ProductOrder
            ): Boolean {
                return newProduct == oldProduct
            }
        }
    }

}