package com.goldenowl.ecommerceapp.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.BagAndProduct
import com.goldenowl.ecommerceapp.databinding.ItemBagBinding
import com.goldenowl.ecommerceapp.utilities.GlideDefault

class ListBagAdapter(
    private val onItemClicked: (BagAndProduct) -> Unit,
    private val onAddClicked: (BagAndProduct) -> Unit,
    private val onDeleteClicked: (BagAndProduct) -> Unit,
    private val onPlusQuantityClicked: (BagAndProduct,TextView) -> Unit,
    private val onMinusQuantityClicked: (BagAndProduct,TextView) -> Unit,
) :
    ListAdapter<BagAndProduct, ListBagAdapter.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(private val context: Context,
                         private val onAddClicked: (BagAndProduct) -> Unit,
                         private val onDeleteClicked: (BagAndProduct) -> Unit,
                         private val onPlusQuantityClicked: (BagAndProduct,TextView) -> Unit,
                         private val onMinusQuantityClicked: (BagAndProduct,TextView) -> Unit,
                         private var binding: ItemBagBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bagAndProduct: BagAndProduct) {
            binding.apply {
                GlideDefault.show(itemView.context,bagAndProduct.product.images[0],imgProduct,true)
                txtName.text = bagAndProduct.product.title
                txtColorInput.text = bagAndProduct.bag.color
                txtSizeInput.text = bagAndProduct.bag.size
                txtQuantity.text = bagAndProduct.bag.quantity.toString()

                val size = bagAndProduct.product.getColorAndSize(bagAndProduct.bag.color,bagAndProduct.bag.size)
                if (bagAndProduct.product.salePercent != null && size != null) {
                    txtPrice.paintFlags = txtPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    txtSalePrice.visibility = View.VISIBLE
                    txtSalePercent.visibility = View.VISIBLE
                    txtSalePercent.text = "-${bagAndProduct.product.salePercent}%"
                    txtSalePrice.text =
                        "${size.price * (100 - bagAndProduct.product.salePercent) / 100}\$"
                } else {
                    txtPrice.paintFlags = 0
                    txtSalePercent.visibility = View.GONE
                    txtSalePrice.visibility = View.GONE

                }

                btnMinus.setOnClickListener {
                    onMinusQuantityClicked(bagAndProduct,txtQuantity)
                }

                btnPlus.setOnClickListener {
                    onPlusQuantityClicked(bagAndProduct,txtQuantity)
                }

                btnMore.setOnClickListener { view ->
                    val popup = PopupMenu(context, view)
                    popup.menuInflater.inflate(R.menu.more_bag, popup.menu)

                    popup.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.menu_add_to_favorites -> {
                                onAddClicked(bagAndProduct)
                                true
                            }

                            R.id.menu_delete -> {
                                onDeleteClicked(bagAndProduct)
                                true
                            }
                            else -> false
                        }

                    }
                    popup.show()
                }

            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            parent.context,
            onAddClicked,
            onDeleteClicked,
            onPlusQuantityClicked,
            onMinusQuantityClicked,
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
            override fun areItemsTheSame(oldItem: BagAndProduct, newItem: BagAndProduct): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: BagAndProduct,
                newItem: BagAndProduct
            ): Boolean {
                return oldItem == newItem
            }
        }
    }


}