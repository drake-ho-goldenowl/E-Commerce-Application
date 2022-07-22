package com.goldenowl.ecommerceapp.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.databinding.ItemButtonAddImageBinding
import com.goldenowl.ecommerceapp.databinding.ItemImageReviewBinding
import com.goldenowl.ecommerceapp.utilities.GlideDefault


class ListImageReview(
    private val isUri: Boolean,
    private val onItemClicked: (Int) -> Unit,
    private val onItemAddImageClicked: () -> Unit,
) :
    RecyclerView.Adapter<ListImageReviewViewHolder>() {
    var dataSet: List<String> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListImageReviewViewHolder {
        return when (viewType) {
            R.layout.item_image_review -> ListImageReviewViewHolder.ItemViewHolder(
                isUri,
                ItemImageReviewBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ),
                    parent,
                    false
                )
            )
            R.layout.item_button_add_image -> ListImageReviewViewHolder.AddImageViewHolder(
                ItemButtonAddImageBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ),
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Invalid ViewType Provided")
        }
    }

    override fun onBindViewHolder(holder: ListImageReviewViewHolder, position: Int) {
        when (holder) {
            is ListImageReviewViewHolder.ItemViewHolder -> {
                holder.bind(dataSet[position])
                holder.itemView.setOnClickListener {
                    onItemClicked(position)
                }
            }
            is ListImageReviewViewHolder.AddImageViewHolder -> {
                holder.itemView.setOnClickListener {
                    onItemAddImageClicked()
                }
                if (dataSet.size == 5) {
                    holder.itemView.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (isUri) {
            dataSet.size + 1
        } else {
            dataSet.size
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (isUri && position == itemCount - 1) {
            R.layout.item_button_add_image
        } else {
            R.layout.item_image_review
        }
    }
}


sealed class ListImageReviewViewHolder(binding: ViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    class ItemViewHolder(
        private val isUri: Boolean,
        private var binding: ItemImageReviewBinding
    ) :
        ListImageReviewViewHolder(binding) {
        fun bind(uri: String) {
            binding.apply {
                if (isUri) {
                    imgReview.setImageURI(Uri.parse(uri))
                } else {
                    GlideDefault.show(itemView.context, uri, imgReview)
                }
            }
        }
    }

    class AddImageViewHolder(
        private val binding: ItemButtonAddImageBinding
    ) : ListImageReviewViewHolder(binding)
}