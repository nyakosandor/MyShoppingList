package com.phro7r.myshoppinglist.data.repository

import com.phro7r.myshoppinglist.data.dao.ShoppingDao
import com.phro7r.myshoppinglist.data.model.ItemCategory
import com.phro7r.myshoppinglist.data.model.ShoppingItem
import com.phro7r.myshoppinglist.data.model.ShoppingStatistics
import com.phro7r.myshoppinglist.data.model.SortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShoppingRepository(private val dao: ShoppingDao) {

    fun getAllItems(): Flow<List<ShoppingItem>> = dao.getAllFlow()

    fun getStatistics(): Flow<ShoppingStatistics> {
        return dao.getAllFlow().map { items ->
            ShoppingStatistics(
                totalItems = items.size,
                boughtItems = items.count { it.isBought },
                pendingItems = items.count { !it.isBought },
                totalCost = items.filter { it.isBought }.sumOf { it.getTotalCost() },
                estimatedCost = items.sumOf { it.getTotalCost() }
            )
        }
    }

    suspend fun getItemById(id: Int): ShoppingItem? = dao.getById(id)

    suspend fun insertItem(item: ShoppingItem): Long = dao.insert(item)

    suspend fun insertItems(items: List<ShoppingItem>) = dao.insertAll(items)

    suspend fun updateItem(item: ShoppingItem) = dao.update(item)

    suspend fun deleteItem(item: ShoppingItem) = dao.delete(item)

    suspend fun deleteAllBought() = dao.deleteAllBought()

    suspend fun getShareableList(): String {
        val items = dao.getAll()
        if (items.isEmpty()) return "My Shopping List is empty"

        val builder = StringBuilder("📋 My Shopping List\n\n")
        val pending = items.filter { !it.isBought }
        val bought = items.filter { it.isBought }

        if (pending.isNotEmpty()) {
            builder.append("🛒 To Buy (${pending.size}):\n")
            pending.forEachIndexed { index, item ->
                builder.append("${index + 1}. ${item.name} (x${item.quantity}) - $%.2f\n".format(item.price))
            }
            builder.append("\n")
        }

        if (bought.isNotEmpty()) {
            builder.append("✅ Bought (${bought.size}):\n")
            bought.forEachIndexed { index, item ->
                builder.append("${index + 1}. ${item.name} (x${item.quantity}) - $%.2f\n".format(item.price))
            }
            builder.append("\n")
        }

        val totalCost = bought.sumOf { it.getTotalCost() }
        val estimatedTotal = items.sumOf { it.getTotalCost() }
        
        builder.append("💰 Spent: $%.2f\n".format(totalCost))
        builder.append("📊 Estimated Total: $%.2f".format(estimatedTotal))

        return builder.toString()
    }
}
