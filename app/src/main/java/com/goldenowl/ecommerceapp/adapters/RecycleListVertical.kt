package com.goldenowl.ecommerceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.ItemProduct2Binding

class RecycleListVertical(private val onItemClicked: (Product) -> Unit) :
    ListAdapter<Product, RecycleListVertical.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(private var binding: ItemProduct2Binding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(product.images[0])
                    .error(R.drawable.img_sample_2)
                    .into(imgProduct)
                txtName.text = product.title
                txtBrandName.text = product.brandName
                ratingBar.numStars = product.reviewStars
                txtNumberVote.text = "(${product.numberReviews})"
                txtOldPrice.text = "${product.colors[0].sizes[0].price}\$"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemProduct2Binding.inflate(
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
        private val DiffCallback = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldProduct: Product, newProduct: Product): Boolean {
                return newProduct === oldProduct
            }

            override fun areContentsTheSame(oldProduct: Product, newProduct: Product): Boolean {
                return newProduct.id == oldProduct.id
            }
        }
    }

}