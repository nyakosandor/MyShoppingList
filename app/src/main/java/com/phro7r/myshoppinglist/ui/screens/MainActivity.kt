package com.phro7r.myshoppinglist.ui.screens

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.phro7r.myshoppinglist.R
import com.phro7r.myshoppinglist.data.model.ItemCategory
import com.phro7r.myshoppinglist.data.model.ShoppingItem
import com.phro7r.myshoppinglist.data.model.SortOption
import com.phro7r.myshoppinglist.databinding.ActivityMainBinding
import com.phro7r.myshoppinglist.databinding.DialogAddItemBinding
import com.phro7r.myshoppinglist.ui.components.ShoppingItemAdapter
import com.phro7r.myshoppinglist.ui.components.SwipeToDeleteCallback
import com.phro7r.myshoppinglist.ui.viewmodel.ShoppingViewModel
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ShoppingViewModel
    private lateinit var adapter: ShoppingItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViews()
        initializeViewModel()
        setupRecyclerView()
        setupSwipeToDelete()
        setupEventListeners()
        observeData()
    }

    private fun initializeViews() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
    }

    private fun initializeViewModel() {
        viewModel = ViewModelProvider(this)[ShoppingViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = ShoppingItemAdapter(
            onToggleBought = { item, checked -> viewModel.toggleBought(item, checked) },
            onItemClick = { item -> showEditDeleteDialog(item) }
        )

        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            setHasFixedSize(true)
        }
    }
    
    private fun setupSwipeToDelete() {
        val swipeCallback = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (position == androidx.recyclerview.widget.RecyclerView.NO_POSITION) return
                
                val item = adapter.getItemAt(position)
                viewModel.deleteItem(item)
                
                Snackbar.make(binding.root, "${item.name} deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") { viewModel.insertItem(item) }
                    .setAnchorView(binding.btnAdd)
                    .show()
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.rvItems)
    }

    private fun setupEventListeners() {
        setupButtonListeners()
        setupSearchListener()
        setupCategoryFilters()
    }

    private fun setupButtonListeners() {
        binding.btnAdd.setOnClickListener { showAddItemDialog() }
        binding.btnSort.setOnClickListener { showSortDialog() }
        binding.btnMenu.setOnClickListener { showMenuDialog() }
    }

    private fun setupSearchListener() {
        binding.etSearch.addTextChangedListener { text ->
            viewModel.setSearchQuery(text?.toString() ?: "")
        }
    }

    private fun setupCategoryFilters() {
        val chipMap = mapOf(
            binding.chipAll to null,
            binding.chipFood to ItemCategory.FOOD,
            binding.chipDrinks to ItemCategory.DRINKS,
            binding.chipHousehold to ItemCategory.HOUSEHOLD,
            binding.chipPersonal to ItemCategory.PERSONAL,
            binding.chipOther to ItemCategory.OTHER
        )

        chipMap.forEach { (chip, category) ->
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.setFilterCategory(category)
                    chipMap.keys.filter { it != chip }.forEach { it.isChecked = false }
                } else if (chipMap.keys.none { it.isChecked }) {
                    binding.chipAll.isChecked = true
                }
            }
        }
    }

    private fun observeData() {
        observeItems()
        observeStatistics()
    }

    private fun observeItems() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.items.collect { items ->
                    adapter.submitList(items)
                    updateEmptyState(items.isEmpty())
                }
            }
        }
    }

    private fun observeStatistics() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.statistics.collect { updateStatisticsUI(it) }
            }
        }
    }

    private fun updateStatisticsUI(stats: com.phro7r.myshoppinglist.data.model.ShoppingStatistics) {
        with(binding) {
            tvItemCount.text = stats.totalItems.toString()
            tvPendingCount.text = stats.pendingItems.toString()
            tvBoughtCount.text = stats.boughtItems.toString()
            tvTotalPrice.text = String.format(Locale.US, "$%.2f", stats.totalCost)
            tvEstimatedPrice.text = String.format(Locale.US, "$%.2f", stats.estimatedCost)
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvItems.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showAddItemDialog(existingItem: ShoppingItem? = null) {
        val dialogBinding = DialogAddItemBinding.inflate(LayoutInflater.from(this))
        
        existingItem?.let { item ->
            with(dialogBinding) {
                etName.setText(item.name)
                etQuantity.setText(item.quantity.toString())
                etPrice.setText(item.price.toString())
                etNotes.setText(item.notes)
            }
            selectCategoryChip(dialogBinding, item.category)
        }

        AlertDialog.Builder(this)
            .setTitle(if (existingItem == null) "Add Item" else "Edit Item")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ -> handleSaveItem(dialogBinding, existingItem) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun selectCategoryChip(dialogBinding: DialogAddItemBinding, category: ItemCategory) {
        val chipId = when (category) {
            ItemCategory.FOOD -> R.id.chipCatFood
            ItemCategory.DRINKS -> R.id.chipCatDrinks
            ItemCategory.HOUSEHOLD -> R.id.chipCatHousehold
            ItemCategory.PERSONAL -> R.id.chipCatPersonal
            ItemCategory.OTHER -> R.id.chipCatOther
        }
        dialogBinding.chipGroupCategory.check(chipId)
    }

    private fun handleSaveItem(dialogBinding: DialogAddItemBinding, existingItem: ShoppingItem?) {
        val name = dialogBinding.etName.text.toString().trim()
        val quantity = dialogBinding.etQuantity.text.toString().toIntOrNull() ?: 1
        val price = dialogBinding.etPrice.text.toString().toDoubleOrNull() ?: 0.0
        val notes = dialogBinding.etNotes.text.toString().trim()
        
        val category = when (dialogBinding.chipGroupCategory.checkedChipId) {
            R.id.chipCatFood -> ItemCategory.FOOD
            R.id.chipCatDrinks -> ItemCategory.DRINKS
            R.id.chipCatHousehold -> ItemCategory.HOUSEHOLD
            R.id.chipCatPersonal -> ItemCategory.PERSONAL
            else -> ItemCategory.OTHER
        }

        if (name.isEmpty()) {
            showSnackbar("Please enter item name")
            return
        }

        val item = existingItem?.copy(
            name = name, quantity = quantity, price = price,
            category = category, notes = notes
        ) ?: ShoppingItem(
            name = name, quantity = quantity, price = price,
            category = category, notes = notes
        )

        if (existingItem == null) viewModel.insertItem(item) else viewModel.updateItem(item)
    }

    private fun showEditDeleteDialog(item: ShoppingItem) {
        AlertDialog.Builder(this)
            .setTitle(item.name)
            .setItems(arrayOf("Edit", "Delete")) { _, which ->
                if (which == 0) showAddItemDialog(item) else handleDeleteItem(item)
            }
            .show()
    }

    private fun handleDeleteItem(item: ShoppingItem) {
        viewModel.deleteItem(item)
        Snackbar.make(binding.root, "${item.name} deleted", Snackbar.LENGTH_LONG)
            .setAction("UNDO") { viewModel.insertItem(item) }
            .setAnchorView(binding.btnAdd)
            .show()
    }

    private fun showSortDialog() {
        val options = SortOption.values()
        AlertDialog.Builder(this)
            .setTitle("Sort By")
            .setItems(options.map { it.displayName }.toTypedArray()) { _, which ->
                viewModel.setSortOption(options[which])
            }
            .show()
    }

    private fun showMenuDialog() {
        AlertDialog.Builder(this)
            .setTitle("Options")
            .setItems(arrayOf("Share List", "Clear All Bought Items")) { _, which ->
                if (which == 0) shareList() else confirmClearBought()
            }
            .show()
    }

    private fun shareList() {
        lifecycleScope.launch {
            val shareText = viewModel.getShareableList()
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "My Shopping List")
                putExtra(Intent.EXTRA_TEXT, shareText)
            }, "Share Shopping List"))
        }
    }

    private fun confirmClearBought() {
        AlertDialog.Builder(this)
            .setTitle("Clear Bought Items")
            .setMessage("Are you sure you want to delete all bought items?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.clearAllBought()
                showSnackbar("Bought items cleared")
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.btnAdd)
            .show()
    }
}

