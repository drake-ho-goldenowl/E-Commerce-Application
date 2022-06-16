package com.goldenowl.ecommerceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.goldenowl.ecommerceapp.data.Promotion
import com.goldenowl.ecommerceapp.databinding.ItemPromotionBinding
import java.util.*
import java.util.concurrent.TimeUnit

class ListPromotionAdapter(private val onApplyClicked: (Promotion) -> Unit) :
    ListAdapter<Promotion, ListPromotionAdapter.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(
        private val onApplyClicked: (Promotion) -> Unit,
        private var binding: ItemPromotionBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(promotion: Promotion) {
            binding.apply {
                txtName.text = promotion.name
                txtIdPromotion.text = promotion.id

                Glide.with(itemView.context)
                    .load(promotion.backgroundImage)
//                        .error(R.drawable.img_sample_2)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgPromotion)

                btnApply.setOnClickListener {
                    onApplyClicked(promotion)
                }

                promotion.endDate?.let {
                    val diff: Long = promotion.endDate.time - Date().time
                    txtTimeRemaining.text =
                        "${TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)} days remaining"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            onApplyClicked,
            ItemPromotionBinding.inflate(
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
        private val DiffCallback = object : DiffUtil.ItemCallback<Promotion>() {
            override fun areItemsTheSame(oldItem: Promotion, newItem: Promotion): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Promotion, newItem: Promotion): Boolean {
                return oldItem == newItem
            }
        }
    }


}