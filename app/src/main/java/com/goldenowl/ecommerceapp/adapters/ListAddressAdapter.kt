package com.goldenowl.ecommerceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goldenowl.ecommerceapp.data.ShippingAddress
import com.goldenowl.ecommerceapp.databinding.ItemAddressBinding

class ListAddressAdapter(
    private val setDefault: (CheckBox, ShippingAddress) -> Unit,
    private val onDefaultClicked: (CheckBox, ShippingAddress) -> Unit,
    private val onEditClicked: (ShippingAddress) -> Unit,
    private val onRemoveClicked: (ShippingAddress) -> Unit
) :
    ListAdapter<ShippingAddress, ListAddressAdapter.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(
        private val setDefault: (CheckBox, ShippingAddress) -> Unit,
        private val onDefaultClicked: (CheckBox, ShippingAddress) -> Unit,
        private val onEditClicked: (ShippingAddress) -> Unit,
        private val onRemoveClicked: (ShippingAddress) -> Unit,
        private var binding: ItemAddressBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(shippingAddress: ShippingAddress) {
            binding.apply {
                shippingAddress.apply {
                    txtName.text = fullName
                    txtAddress.text = "$address\n$city, $state $zipCode, $country"
                    setDefault(checkboxDefault, shippingAddress)
                    checkboxDefault.setOnClickListener {
                        onDefaultClicked(checkboxDefault, shippingAddress)
                    }
                    txtEdit.setOnClickListener {
                        onEditClicked(shippingAddress)
                    }
                    btnRemoveAddress.setOnClickListener {
                        onRemoveClicked(shippingAddress)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            setDefault,
            onDefaultClicked,
            onEditClicked,
            onRemoveClicked,
            ItemAddressBinding.inflate(
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
        private val DiffCallback = object : DiffUtil.ItemCallback<ShippingAddress>() {
            override fun areItemsTheSame(
                oldItem: ShippingAddress,
                newItem: ShippingAddress
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ShippingAddress,
                newItem: ShippingAddress
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}