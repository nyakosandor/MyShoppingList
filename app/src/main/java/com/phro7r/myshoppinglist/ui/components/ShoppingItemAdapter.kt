package com.phro7r.myshoppinglist.ui.components

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.phro7r.myshoppinglist.R
import com.phro7r.myshoppinglist.data.model.ItemCategory
import com.phro7r.myshoppinglist.data.model.ShoppingItem
import com.phro7r.myshoppinglist.databinding.ItemShoppingBinding
import java.util.Locale

class ShoppingItemAdapter(
    private val onToggleBought: (ShoppingItem, Boolean) -> Unit,
    private val onItemClick: (ShoppingItem) -> Unit
) : RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder>() {

    private var items: List<ShoppingItem> = emptyList()

    inner class ViewHolder(val binding: ItemShoppingBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: ShoppingItem) {
            with(binding) {
                tvName.text = item.name
                tvQty.text = "Qty: ${item.quantity}"
                tvPrice.text = String.format(Locale.US, "$%.2f", item.price)
                tvTotal.text = String.format(Locale.US, "Total: $%.2f", item.getTotalCost())
                
                tvCategory.text = item.category.displayName.uppercase()
                val categoryColor = getCategoryColor(item.category)
                tvCategory.setBackgroundColor(categoryColor)
                categoryIndicator.setBackgroundColor(categoryColor)
                
                if (item.notes.isNotBlank()) {
                    tvNotes.visibility = View.VISIBLE
                    tvNotes.text = item.notes
                } else {
                    tvNotes.visibility = View.GONE
                }
                
                applyBoughtStateStyle(item.isBought)
                
                cbBought.setOnCheckedChangeListener(null)
                cbBought.isChecked = item.isBought
                cbBought.setOnCheckedChangeListener { _, checked ->
                    onToggleBought(item, checked)
                }
                
                root.setOnClickListener { onItemClick(item) }
                ivDragHandle.setOnClickListener { onItemClick(item) }
            }
        }
        
        private fun applyBoughtStateStyle(isBought: Boolean) {
            with(binding) {
                if (isBought) {
                    tvName.paintFlags = tvName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    tvPrice.paintFlags = tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    tvTotal.paintFlags = tvTotal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    cardView.alpha = 0.7f
                    cardView.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.item_bought_overlay)
                    )
                } else {
                    tvName.paintFlags = tvName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    tvPrice.paintFlags = tvPrice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    tvTotal.paintFlags = tvTotal.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    cardView.alpha = 1.0f
                    cardView.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.item_pending)
                    )
                }
            }
        }
        
        private fun getCategoryColor(category: ItemCategory): Int {
            return ContextCompat.getColor(binding.root.context, category.colorResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShoppingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
    
    fun getItemAt(position: Int): ShoppingItem = items[position]

    fun submitList(newItems: List<ShoppingItem>) {
        val diffCallback = ShoppingDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }
    
    private class ShoppingDiffCallback(
        private val oldList: List<ShoppingItem>,
        private val newList: List<ShoppingItem>
    ) : DiffUtil.Callback() {
        
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }
        
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
