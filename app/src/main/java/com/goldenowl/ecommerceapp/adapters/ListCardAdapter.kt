package com.goldenowl.ecommerceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.data.Card
import com.goldenowl.ecommerceapp.databinding.ItemMastercardBinding
import com.goldenowl.ecommerceapp.databinding.ItemVisacardBinding

class ListCardAdapter(
    private val onDefaultClicked: (CheckBox, Card) -> Unit,
    private val setDefaultButton: (CheckBox, Card) -> Unit,
    private val onRemoveClicked: (Card) -> Unit,
) :
    RecyclerView.Adapter<ListCardAdapter.ListCardViewHolder>() {
    var dataSet: List<Card> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCardViewHolder {
        return when (viewType) {
            R.layout.item_visacard -> ListCardViewHolder.VisaItemViewHolder(
                onDefaultClicked,
                setDefaultButton,
                onRemoveClicked,
                ItemVisacardBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ),
                    parent,
                    false
                )
            )
            R.layout.item_mastercard -> ListCardViewHolder.MasterCardItemViewHolder(
                onDefaultClicked,
                setDefaultButton,
                onRemoveClicked,
                ItemMastercardBinding.inflate(
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

    override fun onBindViewHolder(holder: ListCardViewHolder, position: Int) {
        when (holder) {
            is ListCardViewHolder.VisaItemViewHolder -> {
                holder.bind(dataSet[position])
            }
            is ListCardViewHolder.MasterCardItemViewHolder -> {
                holder.bind(dataSet[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataSet[position].number[0] == '4') {
            R.layout.item_visacard
        } else {
            R.layout.item_mastercard
        }
    }

    sealed class ListCardViewHolder(binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {


        class VisaItemViewHolder(
            private val onDefaultClicked: (CheckBox, Card) -> Unit,
            private val setDefaultButton: (CheckBox, Card) -> Unit,
            private val onRemoveClicked: (Card) -> Unit,
            private var binding: ItemVisacardBinding
        ) :
            ListCardViewHolder(binding) {
            fun bind(card: Card) {
                binding.apply {
                    txtNumberCard.text =
                        "* * * *  * * * *  * * * *  ${card.number.substring(card.number.length - 4)}"
                    txtExpiryDate.text = card.expireDate
                    txtHolderName.text = card.name
                    setDefaultButton(checkboxDefault, card)
                    checkboxDefault.setOnClickListener {
                        onDefaultClicked(checkboxDefault, card)
                    }
                    btnRemoveCard.setOnClickListener {
                        onRemoveClicked(card)
                    }
                }
            }
        }

        class MasterCardItemViewHolder(
            private val onDefaultClicked: (CheckBox, Card) -> Unit,
            private val setDefaultButton: (CheckBox, Card) -> Unit,
            private val onRemoveClicked: (Card) -> Unit,
            private var binding: ItemMastercardBinding
        ) :
            ListCardViewHolder(binding) {
            fun bind(card: Card) {
                binding.apply {
                    txtNumberCard.text =
                        "* * * *  * * * *  * * * *  ${card.number.substring(card.number.length - 4)}"
                    txtExpiryDate.text = card.expireDate
                    txtHolderName.text = card.name
                    setDefaultButton(checkboxDefault, card)
                    checkboxDefault.setOnClickListener {
                        onDefaultClicked(checkboxDefault, card)
                    }
                    btnRemoveCard.setOnClickListener {
                        onRemoveClicked(card)
                    }
                }
            }
        }
    }
}