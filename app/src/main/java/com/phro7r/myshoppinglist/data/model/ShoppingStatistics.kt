package com.phro7r.myshoppinglist.data.model

data class ShoppingStatistics(
    val totalItems: Int = 0,
    val boughtItems: Int = 0,
    val pendingItems: Int = 0,
    val totalCost: Double = 0.0,
    val estimatedCost: Double = 0.0
) {
    fun getCompletionPercentage(): Int {
        return if (totalItems > 0) (boughtItems * 100) / totalItems else 0
    }
    
    fun getRemainingCost(): Double = estimatedCost - totalCost
    fun isComplete(): Boolean = totalItems > 0 && boughtItems == totalItems
}
