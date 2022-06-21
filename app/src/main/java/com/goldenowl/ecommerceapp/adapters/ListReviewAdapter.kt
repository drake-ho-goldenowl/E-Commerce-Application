package com.goldenowl.ecommerceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldenowl.ecommerceapp.data.Review
import com.goldenowl.ecommerceapp.databinding.ItemReviewBinding
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

class ListReviewAdapter(
    private val onItemClicked: (Review) -> Unit,
    private val setNameAndAvatar: (TextView, CircleImageView, String) -> Unit,
    private val onHelpfulClicked: (Review, TextView, ImageView, Boolean) -> Unit,
    private val setHelpfulButton: (Review, TextView, ImageView) -> Unit,
    private val setRecyclerView: (RecyclerView, Review) -> Unit,
) :
    ListAdapter<Review, ListReviewAdapter.ItemViewHolder>(DiffCallback) {
    private var isHelpful = false

    class ItemViewHolder(
        private val setNameAndAvatar: (TextView, CircleImageView, String) -> Unit,
        private val onHelpfulClicked: (Review, TextView, ImageView, Boolean) -> Unit,
        private val setHelpfulButton: (Review, TextView, ImageView) -> Unit,
        private val setRecyclerVIew: (RecyclerView, Review) -> Unit,
        private var isHelpful: Boolean,
        private var binding: ItemReviewBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            binding.apply {
                txtDescription.text = review.description
                setNameAndAvatar(txtName, imgAvatar, review.idUser)

                ratingBar.rating = review.star.toFloat()

                val timeCreated = review.createdTimer?.toDate()
                val simpleDate = SimpleDateFormat("MMM d, yyyy")

                timeCreated?.let {
                    txtCreated.text = simpleDate.format(it).toString()
                }

                setHelpfulButton(review, txtHelpful, icLike)
                setRecyclerVIew(recyclerViewImageReview, review)
                btnHelpful.setOnClickListener {
                    onHelpfulClicked(review, txtHelpful, icLike, isHelpful)
                }
            }
        }
    }

    fun setIsHelpful(isHelpful: Boolean) {
        this.isHelpful = isHelpful
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            setNameAndAvatar,
            onHelpfulClicked,
            setHelpfulButton,
            setRecyclerView,
            isHelpful,
            ItemReviewBinding.inflate(
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
        private val DiffCallback = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }
        }
    }


}