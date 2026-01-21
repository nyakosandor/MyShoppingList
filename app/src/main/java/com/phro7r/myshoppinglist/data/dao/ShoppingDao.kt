package com.phro7r.myshoppinglist.data.dao

import androidx.room.*
import com.phro7r.myshoppinglist.data.model.ItemCategory
import com.phro7r.myshoppinglist.data.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {
    
    @Query("SELECT * FROM shopping_items ORDER BY isBought ASC, timestamp DESC")
    fun getAllFlow(): Flow<List<ShoppingItem>>
    
    @Query("SELECT * FROM shopping_items ORDER BY isBought ASC, timestamp DESC")
    suspend fun getAll(): List<ShoppingItem>
    
    @Query("SELECT * FROM shopping_items WHERE id = :id")
    suspend fun getById(id: Int): ShoppingItem?
    
    @Query("SELECT * FROM shopping_items WHERE category = :category ORDER BY timestamp DESC")
    fun getByCategory(category: ItemCategory): Flow<List<ShoppingItem>>
    
    @Query("SELECT * FROM shopping_items WHERE isBought = 1")
    suspend fun getAllBought(): List<ShoppingItem>
    
    @Query("SELECT * FROM shopping_items WHERE isBought = 0")
    suspend fun getAllPending(): List<ShoppingItem>
    
    @Query("SELECT * FROM shopping_items WHERE name LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%'")
    fun searchItems(query: String): Flow<List<ShoppingItem>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShoppingItem): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ShoppingItem>)
    
    @Update
    suspend fun update(item: ShoppingItem)
    
    @Delete
    suspend fun delete(item: ShoppingItem)
    
    @Query("DELETE FROM shopping_items WHERE isBought = 1")
    suspend fun deleteAllBought()
    
    @Query("DELETE FROM shopping_items")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM shopping_items")
    suspend fun getCount(): Int
    
    @Query("SELECT COUNT(*) FROM shopping_items WHERE isBought = 1")
    suspend fun getBoughtCount(): Int
}
