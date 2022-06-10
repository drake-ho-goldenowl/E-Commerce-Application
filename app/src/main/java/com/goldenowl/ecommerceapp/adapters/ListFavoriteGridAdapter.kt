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
import com.goldenowl.ecommerceapp.data.Favorite
import com.goldenowl.ecommerceapp.data.FavoriteAndProduct
import com.goldenowl.ecommerceapp.data.Size
import com.goldenowl.ecommerceapp.databinding.ItemProductFavoriteBinding

class ListFavoriteGridAdapter(
    private val fragment: Fragment,
    private val onCloseClicked: (Favorite) -> Unit,
    private val onItemClicked: (FavoriteAndProduct) -> Unit
) :
    ListAdapter<FavoriteAndProduct, ListFavoriteGridAdapter.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(
        private val fragment: Fragment,
        private val onCloseClicked: (Favorite) -> Unit,
        private var binding: ItemProductFavoriteBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favoriteAndProduct: FavoriteAndProduct) {
            val size = filterSize(favoriteAndProduct)
            binding.apply {
                Glide.with(itemView.context)
                    .load(favoriteAndProduct.product.images[0])
                    .error(R.drawable.img_sample_2)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProduct)
                txtName.text = favoriteAndProduct.product.title
                txtBrandName.text = favoriteAndProduct.product.brandName
                ratingBar.rating = favoriteAndProduct.product.reviewStars.toFloat()
                txtColorInput.text = favoriteAndProduct.product.colors[0].color
                txtSizeInput.text = favoriteAndProduct.favorite.size

                txtNumberVote.text = "(${favoriteAndProduct.product.numberReviews})"
                txtPrice.text = "${size?.price}\$"

                if (size?.quantity!! < 1) {
                    grayOutLayout.visibility = View.VISIBLE
                    txtSoldOut.visibility = View.VISIBLE
                } else {
                    grayOutLayout.visibility = View.GONE
                    txtSoldOut.visibility = View.GONE
                }


                if (favoriteAndProduct.product.salePercent != null) {
                    txtPrice.paintFlags = txtPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    txtSalePrice.visibility = View.VISIBLE
                    txtSalePercent.visibility = View.VISIBLE
                    txtSalePercent.text = "-${favoriteAndProduct.product.salePercent}%"
                    txtSalePrice.text =
                        "${size.price * (100 - favoriteAndProduct.product.salePercent) / 100}\$"
                } else {
                    txtPrice.paintFlags = 0
                    txtSalePercent.visibility = View.GONE
                    txtSalePrice.visibility = View.GONE

                }


                setButtonBag(binding.btnFavorite,favoriteAndProduct.favorite.isBag)
                btnFavorite.setOnClickListener {

                }
            }
        }

        private fun filterSize(favoriteAndProduct : FavoriteAndProduct) : Size? {
            for(size in favoriteAndProduct.product.colors[0].sizes){
                if(favoriteAndProduct.favorite.size == size.size){
                    return size
                }
            }
            return null
        }

        private fun setButtonBag(buttonView: View, isBag: Boolean){
            if (isBag) {
                buttonView.background = ContextCompat.getDrawable(
                    fragment.requireContext(),
                    R.drawable.btn_bag_active
                )
            }
            else{
                buttonView.background = ContextCompat.getDrawable(
                    fragment.requireContext(),
                    R.drawable.btn_bag_no_active
                )
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            fragment,
            onCloseClicked,
            ItemProductFavoriteBinding.inflate(
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
        private val DiffCallback = object : DiffUtil.ItemCallback<FavoriteAndProduct>() {
            override fun areItemsTheSame(
                oldProduct: FavoriteAndProduct,
                newProduct: FavoriteAndProduct
            ): Boolean {
                return newProduct === oldProduct
            }

            override fun areContentsTheSame(
                oldProduct: FavoriteAndProduct,
                newProduct: FavoriteAndProduct
            ): Boolean {
                return newProduct == oldProduct
            }
        }
    }

}