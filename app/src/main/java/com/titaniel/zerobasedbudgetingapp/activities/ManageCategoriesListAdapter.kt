package com.titaniel.zerobasedbudgetingapp.activities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category

/**
 * [ManageCategoriesListAdapter] in [context] for displaying [categories] that can be edited, deleted and rearranged.
 * Notifies [itemEventListener] when an action is performed on the item.
 */
class ManageCategoriesListAdapter(
    private val categories: LiveData<List<Category>>,
    private val itemEventListener: (Category, Int) -> Unit,
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageCategoriesItem {
        // Inflate view
        val view = LayoutInflater.from(context).inflate(R.layout.item_category_management, parent, false)

        // Create ViewHolder
        return ManageCategoriesItem(view)
    }

    override fun onBindViewHolder(holder: ManageCategoriesItem, position: Int) {

        // Are categories present?
        categories.value?.let { categories ->

            // Get category
            val category = categories[position]

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