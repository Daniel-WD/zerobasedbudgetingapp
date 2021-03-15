package com.titaniel.zerobasedbudgetingapp.activities

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.TransactionRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_add_edit_category.AddEditCategoryFragment
import com.titaniel.zerobasedbudgetingapp.utils.provideViewModel
import com.titaniel.zerobasedbudgetingapp.utils.reEmit
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * TODO
 */
@HiltViewModel
class ManageCategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    /**
     * All categories
     */
    private val categories =
        categoryRepository.getAllCategories().map { list -> list.sortedBy { it.index } }
            .asLiveData()

    /**
     * New categories, copy of first [categories] value with changes by the user. NOTE: Needed so that changes don't get discarded when [categories] gets changed.
     */
    val newCategories: MutableLiveData<MutableList<Category>> = MutableLiveData()

    /**
     * Observer for [categories]
     */
    private val categoriesObserver: Observer<List<Category>> by lazy {

        // Create observer
        Observer { categories ->

            // Check not null
            categories?.let { cats ->

                // Set newCategories
                newCategories.value = cats.map { it.copy() }.toMutableList()

                // Remove this observer
                this.categories.removeObserver(categoriesObserver)
            }

        }
    }

    init {
        // Set categoriesObserver
        categories.observeForever(categoriesObserver)
    }

    override fun onCleared() {
        super.onCleared()

        // Make sure categoriesObserver has been detached
        categories.removeObserver(categoriesObserver)
    }

    /**
     * Returns if [newCategories] is different from [categories]
     */
    fun hasChanges() =
        categories.value != newCategories.value

    /**
     * Give category with [categoryId] new [name]. Returns true if name is valid, false otherwise
     */
    fun addEditCategory(
        categoryId: Long?,
        name: String
    ): Boolean {

        // Check name valid
        if (name.isBlank() || newCategories.value!!.filter { it.id != categoryId }
                .find { it.name == name } != null) {
            return false
        }

        // Get cats
        val cats = newCategories.value
        requireNotNull(cats)

        if (categoryId == null) {
            // Create new category
            val newCat = Category(name, cats.size)

            // Add new category to cats
            cats.add(newCat)

        } else {
            // Find category
            val category = cats.find { it.id == categoryId }
            requireNotNull(category)

            // Set newName
            category.name = name

        }

        // Re emit newCategories value to notify recycler view
        newCategories.reEmit()

        return true

    }

    /**
     * Writes new category list to database and changes transactions to use [Category.TO_BE_BUDGETED] if their category got deleted. Deletes budgets that depend upon deleted categories.
     */
    fun saveNewCategories() {

        // Get cats list that should be saved as new cats list
        val saveCats = newCategories.value
        requireNotNull(saveCats)

        // Apply indexes to new category order
        saveCats.forEachIndexed { i, category -> category.index = i }

        // Find categories that should be deleted
        val delCats =
            categories.value!!.filter { category -> saveCats.find { it.id == category.id } == null }

        viewModelScope.launch {

            // Update transactions that had a category that should be deleted to use Category.TO_BE_BUDGETED instead
            val updatedTransactions = transactionRepository.getAllTransactions().first().map { transaction ->
                // Check if category is contained in delCats
                delCats.find { it.id == transaction.categoryId }?.let {
                    // Set category to TO_BE_BUDGETED
                    transaction.categoryId = Category.TO_BE_BUDGETED.id
                }
                transaction
            }

            // Update transactions
            transactionRepository.updateTransactions(*updatedTransactions.toTypedArray())

            // Delete old categories (dependent budgets get deleted by foreign key)
            categoryRepository.deleteCategories(*delCats.toTypedArray())

            // Add new categories
            categoryRepository.addCategories(*saveCats.toTypedArray())
        }

    }

}

/**
 * Activity to create or edit a transaction
 */
@AndroidEntryPoint
class ManageCategoriesActivity : AppCompatActivity() {

    /**
     * Toolbar
     */
    private lateinit var toolbar: MaterialToolbar

    /**
     * Category list
     */
    private lateinit var listCategories: RecyclerView

    /**
     * Confirm button
     */
    private lateinit var fabConfirm: FloatingActionButton

    /**
     * ItemTouchHelper used for reordering items in the list
     */
    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(UP or DOWN, 0) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    // Get positions to swap
                    val pos1 = viewHolder.adapterPosition
                    val pos2 = target.adapterPosition

                    // Get categories as mutable list
                    val cats = viewModel.newCategories.value
                    requireNotNull(cats)

                    // Swap category positions
                    cats[pos1] = cats[pos2].also { cats[pos2] = cats[pos1] }

                    // Notify adapter
                    (recyclerView.adapter as ManageCategoriesListAdapter).notifyItemMoved(
                        pos1,
                        pos2
                    )

                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

                override fun isLongPressDragEnabled(): Boolean {
                    // Disable long press
                    return false
                }
            }

        ItemTouchHelper(simpleItemTouchCallback)
    }


    /**
     * View model
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val viewModel: ManageCategoriesViewModel by provideViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_categories)

        // Init views
        toolbar = findViewById(R.id.toolbar)
        listCategories = findViewById(R.id.listManageCategories)
        fabConfirm = findViewById(R.id.fabConfirm)

        // Setup item add listener
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.addCategory -> {
                    addEditCategory()
                    true
                }
                else -> false
            }
        }

        // Setup close listener
        toolbar.setNavigationOnClickListener {
            close()
        }

        // Setup confirmation fab
        fabConfirm.setOnClickListener {
            viewModel.saveNewCategories()
            finish()
        }

        // Setup listCategories
        // Set LayoutManager
        listCategories.layoutManager = LinearLayoutManager(this)

        // Set adapter
        listCategories.adapter = ManageCategoriesListAdapter(
            viewModel.newCategories,
            { category, event ->

                when (event) {
                    // Delete category click
                    ManageCategoriesListAdapter.DELETE_CATEGORY_EVENT -> {
                        // Create and show alert dialog for delete
                        MaterialAlertDialogBuilder(this)
                            .setTitle(getString(R.string.activity_manage_categories_delete_dialog_title))
                            .setMessage(
                                getString(
                                    R.string.activity_manage_categories_delete_dialog_content,
                                    category.name
                                )
                            )
                            .setNegativeButton(getString(R.string.activity_manage_categories_dialog_cancel)) { _, _ -> }
                            .setPositiveButton(getString(R.string.activity_manage_categories_dialog_confirm)) { _, _ ->

                                // Get categories, check not null
                                val cats = viewModel.newCategories.value
                                requireNotNull(cats)

                                // Calc index of category to remove
                                val removeIndex = cats.indexOf(category)

                                // Remove category
                                cats.removeAt(removeIndex)

                                // Notify adapter
                                listCategories.adapter!!.notifyItemRemoved(removeIndex)
                            }
                            .show()
                    }
                    // Edit category click
                    ManageCategoriesListAdapter.EDIT_CATEGORY_EVENT -> {
                        addEditCategory(category.id)
                    }
                }
            },
            itemTouchHelper,
            this,
            this
        )

        // Add horizontal dividers, if not already there
        if (listCategories.itemDecorationCount == 0) {
            listCategories.addItemDecoration(
                DividerItemDecoration(
                    this,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        // Set itemTouchHelper
        itemTouchHelper.attachToRecyclerView(listCategories)

    }

    /**
     * Opens AddEditCategoryFragment to edit category with [categoryId]. Adds new category when [categoryId] is -1 or none is provided.
     */
    private fun addEditCategory(categoryId: Long = -1) {

        // Create add edit category fragment
        val addEditCategoryFragment = AddEditCategoryFragment()

        // Category id as argument
        addEditCategoryFragment.arguments =
            bundleOf(
                AddEditCategoryFragment.CATEGORY_ID_KEY to categoryId
            )

        // Show add edit category fragment
        addEditCategoryFragment.show(
            supportFragmentManager,
            "AddEditCategoryFragment"
        )
    }

    /**
     * Asks to discard changes and finishes
     */
    private fun close() {
        // Create and show alert dialog for discard changes, if changes have been made
        if (viewModel.hasChanges()) {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.activity_manage_categories_discard_dialog_title))
                .setMessage(getString(R.string.activity_manage_categories_discard_dialog_content))
                .setNegativeButton(getString(R.string.activity_manage_categories_dialog_cancel)) { _, _ -> }
                .setPositiveButton(getString(R.string.activity_manage_categories_dialog_confirm)) { _, _ ->
                    finish()
                }
                .show()
        } else { // Finish otherwise
            finish()
        }
    }

    override fun onBackPressed() {
        close()
    }

}