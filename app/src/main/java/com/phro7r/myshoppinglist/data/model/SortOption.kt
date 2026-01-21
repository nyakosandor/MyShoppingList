package com.phro7r.myshoppinglist.data.model

enum class SortOption(val displayName: String) {
    DATE_DESC("Newest First"),
    DATE_ASC("Oldest First"),
    NAME_ASC("Name A-Z"),
    NAME_DESC("Name Z-A"),
    PRICE_ASC("Price Low-High"),
    PRICE_DESC("Price High-Low"),
    PENDING_FIRST("Pending First"),
    BOUGHT_FIRST("Bought First");
    
    fun sort(items: List<ShoppingItem>): List<ShoppingItem> {
        return when (this) {
            NAME_ASC -> items.sortedBy { it.name.lowercase() }
            NAME_DESC -> items.sortedByDescending { it.name.lowercase() }
            PRICE_ASC -> items.sortedBy { it.price }
            PRICE_DESC -> items.sortedByDescending { it.price }
            DATE_ASC -> items.sortedBy { it.timestamp }
            DATE_DESC -> items.sortedByDescending { it.timestamp }
            BOUGHT_FIRST -> items.sortedByDescending { it.isBought }
            PENDING_FIRST -> items.sortedBy { it.isBought }
        }
    }
}
