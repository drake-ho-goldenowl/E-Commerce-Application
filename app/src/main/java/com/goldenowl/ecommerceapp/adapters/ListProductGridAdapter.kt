package com.goldenowl.ecommerceapp.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.Product
import com.goldenowl.ecommerceapp.databinding.ItemProductBinding
import com.goldenowl.ecommerceapp.ui.Favorite.BottomSheetFavorite

class ListProductGridAdapter(
    private val fragment: Fragment,
    private val onItemClicked: (Product) -> Unit
) :
    ListAdapter<Product, ListProductGridAdapter.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(private val fragment: Fragment, private var binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(product.images[0])
                    .error(R.drawable.img_sample_2)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProduct)
                txtName.text = product.title
                txtBrandName.text = product.brandName
                ratingBar.rating = product.reviewStars.toFloat()
                txtNumberVote.text = "(${product.numberReviews})"
                txtPrice.text = "${product.colors[0].sizes[0].price}\$"

                if(product.salePercent != null){
                    txtPrice.paintFlags = txtPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    txtSalePrice.visibility = View.VISIBLE
                    txtSalePercent.visibility = View.VISIBLE
                    txtSalePercent.text = "-${product.salePercent}%"
                    txtSalePrice.text = "${product.colors[0].sizes[0].price * (100 - product.salePercent)/100}\$"
                }
                else{
                    txtPrice.paintFlags = 0
                    txtSalePercent.visibility = View.GONE
                    txtSalePrice.visibility = View.GONE

                }


                setButtonFavorite(binding.btnFavorite,product.isFavorite)


                btnFavorite.setOnClickListener {
                    val bottomSheetSize = BottomSheetFavorite(product)
                    bottomSheetSize.show(fragment.parentFragmentManager, BottomSheetFavorite.TAG)
                }
            }
        }

        private fun setButtonFavorite(buttonView: View, isFavorite: Boolean){
            if (isFavorite) {
                buttonView.background = ContextCompat.getDrawable(
                    fragment.requireContext(),
                    R.drawable.btn_favorite_active
                )
            }
            else{
                buttonView.background = ContextCompat.getDrawable(
                    fragment.requireContext(),
                    R.drawable.btn_favorite_no_active
                )
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            fragment,
            ItemProductBinding.inflate(
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