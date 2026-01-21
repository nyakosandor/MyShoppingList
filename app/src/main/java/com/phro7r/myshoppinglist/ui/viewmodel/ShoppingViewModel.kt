package com.phro7r.myshoppinglist.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.phro7r.myshoppinglist.data.database.AppDatabase
import com.phro7r.myshoppinglist.data.model.ItemCategory
import com.phro7r.myshoppinglist.data.model.ShoppingItem
import com.phro7r.myshoppinglist.data.model.ShoppingStatistics
import com.phro7r.myshoppinglist.data.model.SortOption
import com.phro7r.myshoppinglist.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ShoppingViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ShoppingRepository
    
    init {
        val dao = AppDatabase.getInstance(application).shoppingDao()
        repository = ShoppingRepository(dao)
    }
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _sortOption = MutableStateFlow(SortOption.DATE_DESC)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()
    
    private val _filterCategory = MutableStateFlow<ItemCategory?>(null)
    val filterCategory: StateFlow<ItemCategory?> = _filterCategory.asStateFlow()
    
    val items: StateFlow<List<ShoppingItem>> = combine(
        repository.getAllItems(),
        _searchQuery,
        _sortOption,
        _filterCategory
    ) { items, query, sort, category ->
        var filtered = items
        
        if (category != null) {
            filtered = filtered.filter { it.category == category }
        }
        
        if (query.isNotBlank()) {
            filtered = filtered.filter { 
                it.name.contains(query, ignoreCase = true) ||
                it.notes.contains(query, ignoreCase = true)
            }
        }
        
        sort.sort(filtered)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val statistics: StateFlow<ShoppingStatistics> = repository.getStatistics()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ShoppingStatistics()
        )
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }
    
    fun setFilterCategory(category: ItemCategory?) {
        _filterCategory.value = category
    }
    
    fun insertItem(item: ShoppingItem) {
        if (!item.isValid()) return
        viewModelScope.launch { repository.insertItem(item) }
    }
    
    fun updateItem(item: ShoppingItem) {
        if (!item.isValid()) return
        viewModelScope.launch { repository.updateItem(item) }
    }
    
    fun deleteItem(item: ShoppingItem) {
        viewModelScope.launch { repository.deleteItem(item) }
    }
    
    fun toggleBought(item: ShoppingItem, isBought: Boolean) {
        viewModelScope.launch {
            repository.updateItem(item.copy(isBought = isBought))
        }
    }
    
    fun clearAllBought() {
        viewModelScope.launch { repository.deleteAllBought() }
    }
    
    suspend fun getShareableList(): String {
        return repository.getShareableList()
    }
    
    suspend fun getItemById(id: Int): ShoppingItem? {
        return repository.getItemById(id)
    }
}
