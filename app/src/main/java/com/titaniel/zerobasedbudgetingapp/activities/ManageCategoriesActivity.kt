package com.titaniel.zerobasedbudgetingapp.activities

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
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
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_add_edit_category.AddEditCategoryFragment
import com.titaniel.zerobasedbudgetingapp.utils.provideViewModel
import com.titaniel.zerobasedbudgetingapp.utils.reEmit
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


/**
 * TODO
 */
@HiltViewModel
class ManageCategoriesViewModel @Inject constructor(
    categoryRepository: CategoryRepository
) : ViewModel() {

    /**
     * Observer for [categories]
     */
    private val categoriesObserver: Observer<List<Category>> by lazy {

        // Create observer
        Observer {

            // Check not null
            it?.let {

                // Set newCategories
                newCategories.value = it.toMutableList()

                // Remove this observer
                categories.removeObserver(categoriesObserver)
            }

        }
    }

    /**
     * All categories
     */
    private val categories = categoryRepository.getAllCategories().asLiveData()

    /**
     * New categories, copy of first [categories] value with changes by the user.
     */
    val newCategories: MutableLiveData<MutableList<Category>> = MutableLiveData()

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
     * Give category with [categoryId] new [name]. Returns true if name is valid, false otherwise
     */
    fun addEditCategory(
        categoryId: Long?,
        name: String
    ): Boolean {

        // Check name valid
        if (name.isBlank() || newCategories.value!!.filter { it.id != categoryId }.find { it.name == name } != null) {
            return false
        }

        // Get cats
        val cats = newCategories.value
        requireNotNull(cats)

        if(categoryId == null) {
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
            // TODO Discard changes
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
                            .setTitle(getString(R.string.activity_manage_categories_title))
                            .setMessage(
                                getString(
                                    R.string.activity_manage_categories_delete_dialog_content,
                                    category.name
                                )
                            )
                            .setNegativeButton(getString(R.string.activity_manage_categories_delete_dialog_cancel)) { _, _ -> }
                            .setPositiveButton(getString(R.string.activity_manage_categories_delete_dialog_confirm)) { _, _ ->

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

}