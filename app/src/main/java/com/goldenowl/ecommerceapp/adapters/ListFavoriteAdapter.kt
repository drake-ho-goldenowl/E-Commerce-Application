package com.goldenowl.ecommerceapp.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.Favorite
import com.goldenowl.ecommerceapp.databinding.ItemProductFavorite2Binding

class ListFavoriteAdapter(
    private val fragment: Fragment,
    private val onCloseClicked: (Favorite) -> Unit,
    private val onItemClicked: (Favorite) -> Unit
) :
    ListAdapter<Favorite, ListFavoriteAdapter.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(
        private val fragment: Fragment,
        private val onCloseClicked: (Favorite) -> Unit,
        private var binding: ItemProductFavorite2Binding
    ) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(favorite: Favorite) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(favorite.images)
                    .error(R.drawable.img_sample_2)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProduct)
                txtName.text = favorite.title
                txtBrandName.text = favorite.brandName
                ratingBar.rating = favorite.reviewStars.toFloat()
                txtColorInput.text = favorite.color
                txtSizeInput.text = favorite.size
                txtNumberVote.text = "(${favorite.numberReviews})"
                txtPrice.text = "${favorite.price}\$"

                if(favorite.quantity > 1){
                    grayOutLayout.visibility = View.GONE
                    txtSoldOut.visibility = View.GONE
                }
                else{
                    grayOutLayout.visibility = View.VISIBLE
                    txtSoldOut.visibility = View.VISIBLE
                }

                if(favorite.salePercent != null){
                    txtPrice.paintFlags = txtPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    txtSalePrice.visibility = View.VISIBLE
                    txtSalePercent.visibility = View.VISIBLE
                    txtSalePercent.text = "-${favorite.salePercent}%"
                    txtSalePrice.text = "${favorite.price * (100 - favorite.salePercent)/100}\$"
                }
                else{
                    txtPrice.paintFlags = 0
                    txtSalePercent.visibility = View.GONE
                    txtSalePrice.visibility = View.GONE
                }


                btnRemoveFavorite.setOnClickListener {
                    onCloseClicked(favorite)
                }
//                setButtonFavorite(binding.btnFavorite,product.isFavorite)
//
//                btnFavorite.setOnClickListener {
//                    val bottomSheetSize = BottomSheetSize(product)
//                    bottomSheetSize.show(fragment.parentFragmentManager, BottomSheetSize.TAG)
//                }
            }
        }


//        private fun setButtonFavorite(buttonView: View, isFavorite: Boolean){
//            if (isFavorite) {
//                buttonView.background = ContextCompat.getDrawable(
//                    fragment.requireContext(),
//                    R.drawable.btn_favorite_active
//                )
//            }
//            else{
//                buttonView.background = ContextCompat.getDrawable(
//                    fragment.requireContext(),
//                    R.drawable.btn_favorite_no_active
//                )
//            }
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            fragment,
            onCloseClicked,
            ItemProductFavorite2Binding.inflate(
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
        private val DiffCallback = object : DiffUtil.ItemCallback<Favorite>() {
            override fun areItemsTheSame(oldProduct: Favorite, newProduct: Favorite): Boolean {
                return newProduct === oldProduct
            }

            override fun areContentsTheSame(oldProduct: Favorite, newProduct: Favorite): Boolean {
                return newProduct == oldProduct
            }
        }
    }

}