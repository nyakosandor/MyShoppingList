package com.phro7r.myshoppinglist.data.model

import com.phro7r.myshoppinglist.R

enum class ItemCategory(
    val displayName: String,
    val colorResId: Int,
    val emoji: String
) {
    FOOD("Food", R.color.category_food, "🍎"),
    DRINKS("Drinks", R.color.category_drinks, "🥤"),
    HOUSEHOLD("Household", R.color.category_household, "🏠"),
    PERSONAL("Personal", R.color.category_personal, "💄"),
    OTHER("Other", R.color.category_other, "📦");
    
    fun getDisplayNameWithEmoji(): String = "$emoji $displayName"
}
