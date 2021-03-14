package com.titaniel.zerobasedbudgetingapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category

/**
 * [ManageCategoriesListAdapter] in [context] for displaying [categories] that can be edited, deleted and rearranged.
 * Notifies [itemEventListener] when an action is performed on the item.
 */
class ManageCategoriesListAdapter(
    private val categories: MutableLiveData<MutableList<Category>>,
    private val itemEventListener: (Category, Int) -> Unit,
    private val itemTouchHelper: ItemTouchHelper,
    private val context: Context,
    lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<ManageCategoriesListAdapter.ManageCategoriesItem>() {

    companion object {
        /**
         * Event for edit item click
         */
        const val EDIT_CATEGORY_EVENT = 0

        /**
         * Event for delete item click
         */
        const val DELETE_CATEGORY_EVENT = 1
    }

    init {
        // Setup observers
        categories.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
    }

    /**
     * Represents category, on which various actions can be performed
     */
    @SuppressLint("ClickableViewAccessibility")
    class ManageCategoriesItem(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * Drag handle
         */
        val ivDragHandle: ImageView = itemView.findViewById(R.id.ivDragHandle)

        /**
         * Category text
         */
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategory)

        /**
         * Edit btn
         */
        val ivEdit: ImageView = itemView.findViewById(R.id.ivEdit)

        /**
         * Delete btn
         */
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageCategoriesItem {
        // Inflate view
        val view = LayoutInflater.from(context).inflate(R.layout.item_category_management, parent, false)

        // Create ViewHolder
        val viewHolder = ManageCategoriesItem(view)

        // Setup drag for drag handle
        viewHolder.ivDragHandle.setOnTouchListener { _, event ->

            // Check action down event
            if(event.actionMasked == MotionEvent.ACTION_DOWN) {
                // Start dragging
                itemTouchHelper.startDrag(viewHolder)
            }

            true
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ManageCategoriesItem, position: Int) {

        // Are categories present?
        categories.value?.let { cats ->

            // Get category
            val category = cats[position]

            // Set category text to name
            holder.tvCategoryName.text = category.name

            // Set listener for edit
            holder.ivEdit.setOnClickListener {
                itemEventListener(category, EDIT_CATEGORY_EVENT)
            }

            // Set listener for delete
            holder.ivDelete.setOnClickListener {
                itemEventListener(category, DELETE_CATEGORY_EVENT)
            }
        }

    }

    override fun getItemCount(): Int {
        // Return number of entries
        return categories.value?.size ?: 0
    }

}