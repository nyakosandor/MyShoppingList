package com.phro7r.myshoppinglist.data.database

import androidx.room.TypeConverter
import com.phro7r.myshoppinglist.data.model.ItemCategory

class Converters {
    
    @TypeConverter
    fun fromCategory(category: ItemCategory): String = category.name
    
    @TypeConverter
    fun toCategory(categoryName: String): ItemCategory {
        return try {
            ItemCategory.valueOf(categoryName)
        } catch (e: IllegalArgumentException) {
            ItemCategory.OTHER
        }
    }
}
