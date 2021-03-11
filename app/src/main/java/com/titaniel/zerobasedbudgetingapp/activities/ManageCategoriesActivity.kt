package com.titaniel.zerobasedbudgetingapp.activities

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_add_edit_category.AddEditCategoryFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.fragment_update_budget.UpdateBudgetFragment
import com.titaniel.zerobasedbudgetingapp.utils.provideViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject


/**
 * TODO
 */
@HiltViewModel
class ManageCategoriesViewModel @Inject constructor(
    categoryRepository: CategoryRepository
) : ViewModel() {

    /**
     * All categories
     */
    val categories = categoryRepository.getAllCategories().map { it.toMutableList() }.asLiveData()

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

                    // Get categories
                    val cats = viewModel.categories.value!!

                    // Swap category positions
                    cats[pos1] = cats[pos2].also { cats[pos2] = cats[pos1] }

                    // Notify adapter
                    (recyclerView.adapter as ManageCategoriesListAdapter).notifyItemMoved(pos1, pos2)

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
                    TODO("Add category")
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
            viewModel.categories,
            { category, event ->

                when(event) {
                    // Delete category click
                    ManageCategoriesListAdapter.DELETE_CATEGORY_EVENT -> {
                        // Create and show alert dialog for delete
                        MaterialAlertDialogBuilder(this)
                            .setTitle(getString(R.string.activity_manage_categories_title))
                            .setMessage(getString(R.string.activity_manage_categories_delete_dialog_content, category.name))
                            .setNegativeButton(getString(R.string.activity_manage_categories_delete_dialog_cancel)) { _, _ -> }
                            .setPositiveButton(getString(R.string.activity_manage_categories_delete_dialog_confirm)) { _, _ ->

                                // Get categories, check not null
                                val cats = viewModel.categories.value
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

                        // Create add edit category fragment
                        val addEditCategoryFragment = AddEditCategoryFragment()

                        // Category id as argument
                        addEditCategoryFragment.arguments =
                            bundleOf(
                                AddEditCategoryFragment.CATEGORY_ID_KEY to category.id
                            )

                        // Show add edit category fragment
                        addEditCategoryFragment.show(supportFragmentManager, "AddEditCategoryFragment")
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

}