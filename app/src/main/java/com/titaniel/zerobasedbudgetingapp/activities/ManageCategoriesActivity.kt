package com.titaniel.zerobasedbudgetingapp.activities

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import com.titaniel.zerobasedbudgetingapp.utils.provideViewModel
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
     * All categories
     */
    val categories = categoryRepository.getAllCategories().asLiveData()

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
            when(item.itemId) {
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
            {category, event ->
                TODO("react on delete and edit")
            },
            this,
            this
        )

        // Add horizontal dividers, if not already there
        if(listCategories.itemDecorationCount == 0) {
            listCategories.addItemDecoration(
                DividerItemDecoration(
                    this,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

    }

}