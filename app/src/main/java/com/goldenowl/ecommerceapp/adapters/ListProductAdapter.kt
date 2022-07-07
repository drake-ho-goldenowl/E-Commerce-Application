package com.goldenowl.ecommerceapp.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.ItemProduct2Binding

class ListProductAdapter(
    private val onItemClicked: (Product) -> Unit,
    private val onFavoriteClick: (Product) -> Unit,
    private val setFavoriteButton: (View, Product) -> Unit,
) :
    ListAdapter<Product, ListProductAdapter.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(
        private val onFavoriteClick: (Product) -> Unit,
        private val setFavoriteButton: (View, Product) -> Unit,
        private var binding: ItemProduct2Binding
    ) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(product: Product) {
            binding.apply {
                val circularProgressDrawable = CircularProgressDrawable(itemView.context)
                circularProgressDrawable.strokeWidth = 5f
                circularProgressDrawable.centerRadius = 30f
                circularProgressDrawable.backgroundColor =
                    ContextCompat.getColor(itemView.context, R.color.colorPrimary)
                circularProgressDrawable.start()

                Glide.with(itemView.context)
                    .load(product.images[0])
                    .placeholder(circularProgressDrawable)
                    .error(R.drawable.img_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProduct)
                txtName.text = product.title
                txtBrandName.text = product.brandName
                ratingBar.rating = product.reviewStars.toFloat()
                txtNumberVote.text = "(${product.numberReviews})"
                txtPrice.text = "${product.colors[0].sizes[0].price}\$"

                if (product.salePercent != null) {
                    txtPrice.paintFlags = txtPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    txtSalePrice.visibility = View.VISIBLE
                    txtSalePercent.visibility = View.VISIBLE
                    txtSalePercent.text = "-${product.salePercent}%"
                    txtSalePrice.text =
                        "${product.colors[0].sizes[0].price * (100 - product.salePercent) / 100}\$"
                } else {
                    txtPrice.paintFlags = 0
                    txtSalePercent.visibility = View.GONE
                    txtSalePrice.visibility = View.GONE
                }
                setFavoriteButton(btnFavorite, product)
                btnFavorite.setOnClickListener {
                    onFavoriteClick(product)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            onFavoriteClick,
            setFavoriteButton,
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
                return newProduct == oldProduct
            }
        }
    }

}