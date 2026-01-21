# Shopping List App - Architecture Documentation

## 🏗️ Clean Architecture with OOP Principles

This project follows **Clean Architecture** principles with proper separation of concerns and OOP design patterns.

## 📁 Project Structure

```
com.phro7r.myshoppinglist/
│
├── data/                          # Data Layer
│   ├── dao/                       # Data Access Objects
│   │   └── ShoppingDao.kt        # Database queries interface
│   │
│   ├── database/                  # Database Configuration
│   │   ├── AppDatabase.kt        # Room database singleton
│   │   └── Converters.kt         # Type converters for Room
│   │
│   ├── model/                     # Data Models (Entities)
│   │   ├── ShoppingItem.kt       # Shopping item entity
│   │   ├── ItemCategory.kt       # Category enum
│   │   ├── ShoppingStatistics.kt # Statistics data model
│   │   └── SortOption.kt         # Sort options enum
│   │
│   └── repository/                # Repository Pattern
│       └── ShoppingRepository.kt  # Single source of truth
│
└── ui/                            # Presentation Layer
    ├── components/                # Reusable UI Components
    │   ├── ShoppingItemAdapter.kt       # RecyclerView adapter
    │   └── SwipeToDeleteCallback.kt     # Swipe gesture handler
    │
    ├── screens/                   # Screen Activities
    │   └── MainActivity.kt        # Main screen
    │
    ├── theme/                     # UI Theming
    │   ├── Color.kt              # Color palette
    │   ├── Dimens.kt             # Dimensions
    │   └── Typography.kt         # Text styles
    │
    └── viewmodel/                 # ViewModels
        └── ShoppingViewModel.kt   # Main screen ViewModel
```

## 🎯 Architecture Layers

### 1. **Data Layer** (data/)

#### a) DAO (Data Access Object)
- **Location:** `data/dao/`
- **Purpose:** Define database operations
- **Pattern:** DAO Pattern
- **Example:** `ShoppingDao.kt`

```kotlin
@Dao
interface ShoppingDao {
    @Query("SELECT * FROM shopping_items")
    fun getAllFlow(): Flow<List<ShoppingItem>>
    
    @Insert
    suspend fun insert(item: ShoppingItem): Long
}
```

**OOP Principles:**
- **Interface Segregation:** Clean interface for database operations
- **Abstraction:** Hides database implementation details

#### b) Database
- **Location:** `data/database/`
- **Purpose:** Configure and manage Room database
- **Pattern:** Singleton Pattern
- **Example:** `AppDatabase.kt`

```kotlin
@Database(entities = [ShoppingItem::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoppingDao(): ShoppingDao
    
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase
    }
}
```

**OOP Principles:**
- **Singleton Pattern:** Ensures single database instance
- **Factory Pattern:** getInstance() creates/returns instance

#### c) Model (Entities)
- **Location:** `data/model/`
- **Purpose:** Define data structures
- **Pattern:** Entity Pattern
- **Examples:**
  - `ShoppingItem.kt` - Main entity
  - `ItemCategory.kt` - Enum for categories
  - `ShoppingStatistics.kt` - Statistics model
  - `SortOption.kt` - Sort options enum

```kotlin
@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val category: ItemCategory,
    // ... other fields
) {
    // Business logic methods
    fun getTotalCost(): Double = price * quantity
    fun isValid(): Boolean = name.isNotBlank()
}
```

**OOP Principles:**
- **Encapsulation:** Data and behavior together
- **Single Responsibility:** Each model has one purpose

#### d) Repository
- **Location:** `data/repository/`
- **Purpose:** Single source of truth for data operations
- **Pattern:** Repository Pattern
- **Example:** `ShoppingRepository.kt`

```kotlin
class ShoppingRepository(private val dao: ShoppingDao) {
    fun getAllItems(): Flow<List<ShoppingItem>>
    suspend fun insertItem(item: ShoppingItem): Long
    suspend fun deleteItem(item: ShoppingItem)
    fun getStatistics(): Flow<ShoppingStatistics>
}
```

**OOP Principles:**
- **Dependency Injection:** DAO injected via constructor
- **Single Responsibility:** Manages all data operations
- **Abstraction:** Hides DAO implementation from upper layers

---

### 2. **UI Layer** (ui/)

#### a) Components (Reusable UI)
- **Location:** `ui/components/`
- **Purpose:** Reusable UI widgets and adapters
- **Pattern:** Adapter Pattern, Callback Pattern
- **Examples:**
  - `ShoppingItemAdapter.kt` - RecyclerView adapter with DiffUtil
  - `SwipeToDeleteCallback.kt` - Swipe gesture handler

```kotlin
class ShoppingItemAdapter(
    private val onToggleBought: (ShoppingItem, Boolean) -> Unit,
    private val onItemClick: (ShoppingItem) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {
    
    fun submitList(newItems: List<ShoppingItem>) {
        // DiffUtil for efficient updates
    }
}
```

**OOP Principles:**
- **Adapter Pattern:** Adapts data to RecyclerView
- **Observer Pattern:** Callbacks for events
- **DiffUtil:** Efficient list updates

#### b) Screens
- **Location:** `ui/screens/`
- **Purpose:** Activity/Fragment implementations
- **Pattern:** MVVM Pattern
- **Example:** `MainActivity.kt`

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ShoppingViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        initializeViews()
        initializeViewModel()
        observeData()
    }
}
```

**OOP Principles:**
- **Single Responsibility:** Only handles UI logic
- **Separation of Concerns:** No business logic in UI
- **Observer Pattern:** Observes ViewModel data

#### c) Theme
- **Location:** `ui/theme/`
- **Purpose:** Centralized styling and theming
- **Pattern:** Constants Pattern
- **Examples:**
  - `Color.kt` - Color palette
  - `Dimens.kt` - Dimensions
  - `Typography.kt` - Text styles

```kotlin
object AppColors {
    val Primary = Color(0xFF4CAF50)
    val Accent = Color(0xFFFF9800)
}

object Dimens {
    const val SPACING_NORMAL = 16
    const val CARD_CORNER_RADIUS = 12
}
```

**OOP Principles:**
- **Encapsulation:** All styling in one place
- **Singleton (Object):** Single instance of constants
- **Maintainability:** Easy to update theme

#### d) ViewModel
- **Location:** `ui/viewmodel/`
- **Purpose:** Manage UI state and business logic
- **Pattern:** MVVM Pattern, Observer Pattern
- **Example:** `ShoppingViewModel.kt`

```kotlin
class ShoppingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ShoppingRepository
    
    val items: StateFlow<List<ShoppingItem>>
    val statistics: StateFlow<ShoppingStatistics>
    
    fun insertItem(item: ShoppingItem)
    fun deleteItem(item: ShoppingItem)
}
```

**OOP Principles:**
- **Separation of Concerns:** Business logic separate from UI
- **Observer Pattern:** StateFlow for reactive updates
- **Dependency Injection:** Repository injected

---

## 🔄 Data Flow

```
┌─────────────┐
│   Screen    │ (MainActivity)
│  (UI Layer) │
└──────┬──────┘
       │ observes StateFlow
       │ calls methods
┌──────▼──────────┐
│   ViewModel     │ (ShoppingViewModel)
│ (Presentation)  │
└──────┬──────────┘
       │ uses
┌──────▼──────────┐
│   Repository    │ (ShoppingRepository)
│  (Data Layer)   │
└──────┬──────────┘
       │ uses
┌──────▼──────────┐
│      DAO        │ (ShoppingDao)
│  (Data Access)  │
└──────┬──────────┘
       │ queries
┌──────▼──────────┐
│    Database     │ (AppDatabase)
│     (Room)      │
└─────────────────┘
```

## 🎨 Design Patterns Used

### 1. **Singleton Pattern**
- **Where:** `AppDatabase`
- **Why:** Single database instance

### 2. **Repository Pattern**
- **Where:** `ShoppingRepository`
- **Why:** Single source of truth, abstraction

### 3. **DAO Pattern**
- **Where:** `ShoppingDao`
- **Why:** Abstract database operations

### 4. **Adapter Pattern**
- **Where:** `ShoppingItemAdapter`
- **Why:** Adapt data to RecyclerView

### 5. **Observer Pattern**
- **Where:** StateFlow, LiveData
- **Why:** Reactive UI updates

### 6. **MVVM Pattern**
- **Where:** Entire architecture
- **Why:** Separation of concerns

### 7. **Factory Pattern**
- **Where:** Database getInstance()
- **Why:** Object creation logic

### 8. **Callback Pattern**
- **Where:** Adapter click listeners, SwipeToDelete
- **Why:** Event handling

## 🔐 SOLID Principles

### S - Single Responsibility Principle
✅ Each class has one reason to change:
- `ShoppingDao` - Only database queries
- `ShoppingRepository` - Only data operations
- `ShoppingViewModel` - Only UI state management
- `MainActivity` - Only UI logic

### O - Open/Closed Principle
✅ Open for extension, closed for modification:
- `ItemCategory` enum can be extended
- `SortOption` enum can add new sorts
- Repository can add new methods without changing existing

### L - Liskov Substitution Principle
✅ Interfaces can be substituted:
- `ShoppingDao` interface can have multiple implementations
- ViewModels follow AndroidViewModel contract

### I - Interface Segregation Principle
✅ Specific interfaces:
- `ShoppingDao` has only necessary methods
- Callbacks are specific (onItemClick, onToggleBought)

### D - Dependency Inversion Principle
✅ Depend on abstractions:
- ViewModel depends on Repository interface
- Repository depends on DAO interface
- High-level modules don't depend on low-level

## 📊 Benefits of This Architecture

### 1. **Testability**
- Repository can be mocked
- ViewModel can be tested without UI
- DAO can be tested independently

### 2. **Maintainability**
- Clear separation of concerns
- Easy to locate and fix bugs
- Changes are isolated

### 3. **Scalability**
- Easy to add new features
- New screens follow same pattern
- Reusable components

### 4. **Readability**
- Clear package structure
- Self-documenting code
- Consistent patterns

### 5. **Reusability**
- Repository can be used by multiple ViewModels
- Components can be used in multiple screens
- Models are shared across layers

## 🚀 How to Add New Features

### Adding a New Screen:
1. Create Activity in `ui/screens/`
2. Create ViewModel in `ui/viewmodel/`
3. Reuse existing Repository
4. Use existing Components

### Adding a New Data Model:
1. Create entity in `data/model/`
2. Add DAO methods in `data/dao/`
3. Add repository methods in `data/repository/`
4. Update ViewModel to use new repository methods

### Adding a New UI Component:
1. Create component in `ui/components/`
2. Follow existing patterns (ViewBinding, callbacks)
3. Use theme constants from `ui/theme/`

## 📚 Key Technologies

- **Kotlin** - Modern, concise language
- **Room** - SQLite abstraction
- **Coroutines** - Async operations
- **StateFlow** - Reactive state management
- **ViewBinding** - Type-safe view access
- **Material Design 3** - Modern UI components

## 🎓 Learning Resources

- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [MVVM Pattern](https://developer.android.com/topic/architecture)
- [Repository Pattern](https://developer.android.com/codelabs/android-room-with-a-view-kotlin)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)

---

**This architecture ensures clean, maintainable, and scalable code following industry best practices!** 🎉

