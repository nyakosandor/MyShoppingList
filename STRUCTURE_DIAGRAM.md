# 📊 Project Structure Visualization

## 🏗️ Complete Package Structure

```
📦 com.phro7r.myshoppinglist
│
├─── 📂 data (DATA LAYER - Business Logic & Data Management)
│    │
│    ├─── 📂 dao (Data Access Objects - Interface for DB operations)
│    │    └─── 📄 ShoppingDao.kt
│    │         ├─ getAllFlow(): Flow<List<ShoppingItem>>
│    │         ├─ insert(item: ShoppingItem)
│    │         ├─ update(item: ShoppingItem)
│    │         ├─ delete(item: ShoppingItem)
│    │         ├─ searchItems(query: String)
│    │         └─ ... 10+ database operations
│    │
│    ├─── 📂 database (Database Configuration)
│    │    ├─── 📄 AppDatabase.kt (Singleton)
│    │    │     ├─ shoppingDao(): ShoppingDao
│    │    │     └─ getInstance(context): AppDatabase
│    │    │
│    │    └─── 📄 Converters.kt (Type Converters)
│    │          ├─ fromCategory(category): String
│    │          └─ toCategory(name): ItemCategory
│    │
│    ├─── 📂 model (Data Models - Entities & Enums)
│    │    ├─── 📄 ShoppingItem.kt (Entity)
│    │    │     ├─ id: Int
│    │    │     ├─ name: String
│    │    │     ├─ quantity: Int
│    │    │     ├─ price: Double
│    │    │     ├─ category: ItemCategory
│    │    │     ├─ isBought: Boolean
│    │    │     ├─ timestamp: Long
│    │    │     ├─ notes: String
│    │    │     ├─ getTotalCost(): Double
│    │    │     └─ isValid(): Boolean
│    │    │
│    │    ├─── 📄 ItemCategory.kt (Enum)
│    │    │     ├─ FOOD
│    │    │     ├─ DRINKS
│    │    │     ├─ HOUSEHOLD
│    │    │     ├─ PERSONAL
│    │    │     ├─ OTHER
│    │    │     └─ getDisplayNameWithEmoji(): String
│    │    │
│    │    ├─── 📄 ShoppingStatistics.kt (Data Class)
│    │    │     ├─ totalItems: Int
│    │    │     ├─ boughtItems: Int
│    │    │     ├─ pendingItems: Int
│    │    │     ├─ totalCost: Double
│    │    │     ├─ estimatedCost: Double
│    │    │     ├─ getCompletionPercentage(): Int
│    │    │     └─ getRemainingCost(): Double
│    │    │
│    │    └─── 📄 SortOption.kt (Enum)
│    │          ├─ DATE_DESC
│    │          ├─ DATE_ASC
│    │          ├─ NAME_ASC
│    │          ├─ NAME_DESC
│    │          ├─ PRICE_ASC
│    │          ├─ PRICE_DESC
│    │          ├─ PENDING_FIRST
│    │          ├─ BOUGHT_FIRST
│    │          └─ sort(items): List<ShoppingItem>
│    │
│    └─── 📂 repository (Repository Pattern - Single Source of Truth)
│         └─── 📄 ShoppingRepository.kt
│              ├─ dao: ShoppingDao (injected)
│              ├─ getAllItems(): Flow<List<ShoppingItem>>
│              ├─ getFilteredItems(query, category, sort)
│              ├─ insertItem(item): Long
│              ├─ updateItem(item)
│              ├─ deleteItem(item)
│              ├─ toggleBought(item)
│              ├─ getStatistics(): Flow<ShoppingStatistics>
│              └─ getShareableList(): String
│
└─── 📂 ui (UI LAYER - User Interface & Presentation)
     │
     ├─── 📂 components (Reusable UI Components)
     │    ├─── 📄 ShoppingItemAdapter.kt
     │    │     ├─ ViewHolder (inner class)
     │    │     ├─ onCreateViewHolder()
     │    │     ├─ onBindViewHolder()
     │    │     ├─ submitList() (uses DiffUtil)
     │    │     └─ ShoppingDiffCallback (inner class)
     │    │
     │    └─── 📄 SwipeToDeleteCallback.kt
     │          ├─ onMove()
     │          ├─ onSwiped()
     │          ├─ onChildDraw()
     │          └─ drawSwipeBackground()
     │
     ├─── 📂 screens (Screen Activities/Fragments)
     │    └─── 📄 MainActivity.kt
     │         ├─ binding: ActivityMainBinding
     │         ├─ viewModel: ShoppingViewModel
     │         ├─ adapter: ShoppingItemAdapter
     │         │
     │         ├─ Lifecycle Methods:
     │         │   └─ onCreate()
     │         │
     │         ├─ Initialization:
     │         │   ├─ initializeViews()
     │         │   ├─ initializeViewModel()
     │         │   ├─ setupRecyclerView()
     │         │   └─ setupSwipeToDelete()
     │         │
     │         ├─ Event Listeners:
     │         │   ├─ setupButtonListeners()
     │         │   ├─ setupSearchListener()
     │         │   └─ setupCategoryFilters()
     │         │
     │         ├─ Data Observation:
     │         │   ├─ observeItems()
     │         │   └─ observeStatistics()
     │         │
     │         └─ Dialogs:
     │             ├─ showAddItemDialog()
     │             ├─ showEditDeleteDialog()
     │             ├─ showSortDialog()
     │             └─ showMenuDialog()
     │
     ├─── 📂 theme (Theming & Styling)
     │    ├─── 📄 Color.kt
     │    │     ├─ AppColors (object)
     │    │     │   ├─ Primary
     │    │     │   ├─ Accent
     │    │     │   ├─ Success
     │    │     │   └─ ... 15+ colors
     │    │     │
     │    │     └─ ColorResIds (object)
     │    │         └─ XML resource IDs
     │    │
     │    ├─── 📄 Dimens.kt
     │    │     └─ Dimens (object)
     │    │         ├─ SPACING_*
     │    │         ├─ PADDING_*
     │    │         ├─ CARD_*
     │    │         ├─ BUTTON_*
     │    │         └─ TEXT_SIZE_*
     │    │
     │    └─── 📄 Typography.kt
     │          └─ Typography (object)
     │              ├─ TitleStyle
     │              ├─ BodyStyle
     │              ├─ CaptionStyle
     │              └─ ButtonStyle
     │
     └─── 📂 viewmodel (ViewModels - MVVM)
          └─── 📄 ShoppingViewModel.kt
               ├─ repository: ShoppingRepository
               │
               ├─ UI State:
               │   ├─ searchQuery: StateFlow<String>
               │   ├─ sortOption: StateFlow<SortOption>
               │   └─ filterCategory: StateFlow<ItemCategory?>
               │
               ├─ Exposed Data:
               │   ├─ items: StateFlow<List<ShoppingItem>>
               │   └─ statistics: StateFlow<ShoppingStatistics>
               │
               └─ Public Methods:
                   ├─ setSearchQuery(query)
                   ├─ setSortOption(option)
                   ├─ setFilterCategory(category)
                   ├─ insertItem(item)
                   ├─ updateItem(item)
                   ├─ deleteItem(item)
                   ├─ toggleBought(item, isBought)
                   └─ clearAllBought()
```

---

## 🔄 Data Flow Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                         UI LAYER                              │
│  ┌────────────────────────────────────────────────────────┐  │
│  │                    MainActivity                         │  │
│  │  ┌──────────────┐  ┌────────────────────────────────┐  │  │
│  │  │   Binding    │  │  ShoppingItemAdapter           │  │  │
│  │  │  (Views)     │  │  + SwipeToDeleteCallback       │  │  │
│  │  └──────────────┘  └────────────────────────────────┘  │  │
│  └─────────────────────────┬────────────────────────────────┘  │
│                            │ observes StateFlow                │
│                            │ calls methods                     │
│  ┌─────────────────────────▼────────────────────────────────┐  │
│  │              ShoppingViewModel                           │  │
│  │  ┌──────────┐  ┌─────────────────┐  ┌───────────────┐  │  │
│  │  │  Search  │  │  Sort & Filter  │  │  Statistics   │  │  │
│  │  │  State   │  │      State      │  │     State     │  │  │
│  │  └──────────┘  └─────────────────┘  └───────────────┘  │  │
│  └─────────────────────────┬────────────────────────────────┘  │
└────────────────────────────┼───────────────────────────────────┘
                             │ uses
┌────────────────────────────▼───────────────────────────────────┐
│                        DATA LAYER                              │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │              ShoppingRepository                          │  │
│  │  - Single Source of Truth                               │  │
│  │  - Abstracts data sources                               │  │
│  │  - Transforms data                                       │  │
│  └─────────────────────────┬───────────────────────────────┘  │
│                            │ uses                              │
│  ┌─────────────────────────▼───────────────────────────────┐  │
│  │                 ShoppingDao                              │  │
│  │  - Database operations interface                        │  │
│  │  - Returns Flow for reactive updates                    │  │
│  └─────────────────────────┬───────────────────────────────┘  │
│                            │ queries                           │
│  ┌─────────────────────────▼───────────────────────────────┐  │
│  │               AppDatabase (Room)                         │  │
│  │  ┌────────────────────────────────────────────────────┐ │  │
│  │  │           shopping_items table                     │ │  │
│  │  │  ┌──────────────────────────────────────────────┐  │ │  │
│  │  │  │ id | name | qty | price | category | ...    │  │ │  │
│  │  │  └──────────────────────────────────────────────┘  │ │  │
│  │  └────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────┘
```

---

## 🎯 Dependency Graph

```
MainActivity
    ↓ depends on
ShoppingViewModel
    ↓ depends on
ShoppingRepository
    ↓ depends on
ShoppingDao
    ↓ depends on
AppDatabase

MainActivity
    ↓ uses
ShoppingItemAdapter
    ↓ uses
ItemShoppingBinding (View)

MainActivity
    ↓ uses
SwipeToDeleteCallback
```

---

## 📦 Model Relationships

```
┌─────────────────────────┐
│    ShoppingItem         │
│  ┌───────────────────┐  │
│  │ - id              │  │
│  │ - name            │  │
│  │ - quantity        │  │
│  │ - price           │  │
│  │ - isBought        │  │
│  │ - timestamp       │  │
│  │ - notes           │  │
│  │ - category ───────┼──┼─────┐
│  └───────────────────┘  │      │
│                         │      │
│  Methods:               │      │
│  + getTotalCost()       │      │
│  + isValid()            │      │
└─────────────────────────┘      │
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │   ItemCategory (Enum)  │
                    ├────────────────────────┤
                    │ - FOOD                 │
                    │ - DRINKS               │
                    │ - HOUSEHOLD            │
                    │ - PERSONAL             │
                    │ - OTHER                │
                    ├────────────────────────┤
                    │ + displayName: String  │
                    │ + colorResId: Int      │
                    │ + emoji: String        │
                    └────────────────────────┘

┌─────────────────────────────────┐
│   ShoppingStatistics            │
├─────────────────────────────────┤
│ - totalItems: Int               │
│ - boughtItems: Int              │
│ - pendingItems: Int             │
│ - totalCost: Double             │
│ - estimatedCost: Double         │
├─────────────────────────────────┤
│ + getCompletionPercentage()    │
│ + getRemainingCost()            │
│ + isComplete()                  │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│   SortOption (Enum)             │
├─────────────────────────────────┤
│ - DATE_DESC                     │
│ - DATE_ASC                      │
│ - NAME_ASC                      │
│ - NAME_DESC                     │
│ - PRICE_ASC                     │
│ - PRICE_DESC                    │
│ - PENDING_FIRST                 │
│ - BOUGHT_FIRST                  │
├─────────────────────────────────┤
│ + sort(items): List<Item>      │
└─────────────────────────────────┘
```

---

## 🔀 Communication Flow

### User Action → Database Update

```
1. User taps "Add Item"
   ↓
2. MainActivity.showAddItemDialog()
   ↓
3. User fills form and taps "Save"
   ↓
4. MainActivity.handleSaveItem()
   ↓
5. viewModel.insertItem(item)
   ↓
6. repository.insertItem(item)
   ↓
7. dao.insert(item)
   ↓
8. Room inserts into database
```

### Database Update → UI Update

```
1. Database changes
   ↓
2. dao.getAllFlow() emits new list
   ↓
3. repository transforms data
   ↓
4. viewModel.items StateFlow updates
   ↓
5. MainActivity observes change
   ↓
6. adapter.submitList(newItems)
   ↓
7. DiffUtil calculates diff
   ↓
8. RecyclerView animates changes
   ↓
9. UI updates automatically
```

---

## 🎨 Theme System

```
ui/theme/
│
├─── Color.kt
│    └─── AppColors
│         ├─── Primary Colors
│         ├─── Accent Colors  
│         ├─── Background Colors
│         ├─── Text Colors
│         ├─── Status Colors
│         └─── Category Colors
│
├─── Dimens.kt
│    └─── Dimens
│         ├─── Spacing (4dp - 32dp)
│         ├─── Padding (8dp - 24dp)
│         ├─── Card (radius, elevation, margin)
│         ├─── Button (height, radius)
│         ├─── Icon (16dp - 32dp)
│         └─── Text (10sp - 24sp)
│
└─── Typography.kt
     └─── Typography
          ├─── TitleStyle
          ├─── BodyStyle
          ├─── CaptionStyle
          └─── ButtonStyle
```

---

## 📊 Class Diagram Summary

```
┌──────────────────────┐
│   MainActivity       │
│   (Screen)           │
└──────────┬───────────┘
           │ has
           ▼
┌──────────────────────┐
│  ShoppingViewModel   │
│  (ViewModel)         │
└──────────┬───────────┘
           │ uses
           ▼
┌──────────────────────┐
│ ShoppingRepository   │
│  (Repository)        │
└──────────┬───────────┘
           │ uses
           ▼
┌──────────────────────┐
│   ShoppingDao        │
│   (Interface)        │
└──────────┬───────────┘
           │ implemented by
           ▼
┌──────────────────────┐
│   AppDatabase        │
│   (Room Database)    │
└──────────────────────┘
```

---

**This structure ensures clean, maintainable, and scalable code! 🚀**

