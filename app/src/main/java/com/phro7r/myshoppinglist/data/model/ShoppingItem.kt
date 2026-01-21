package com.phro7r.myshoppinglist.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val quantity: Int,
    val isBought: Boolean = false,
    val price: Double = 0.0,
    val category: ItemCategory = ItemCategory.OTHER,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
) {
    fun getTotalCost(): Double = price * quantity
    fun isValid(): Boolean = name.isNotBlank() && quantity > 0 && price >= 0
}