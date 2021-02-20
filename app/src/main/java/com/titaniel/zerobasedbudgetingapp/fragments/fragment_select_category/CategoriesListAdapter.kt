package com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category

/**
 * [CategoriesListAdapter] in [context] for displaying a list of [categories].
 * Needs [lifecycleOwner].
 */
class CategoriesListAdapter(
        private val categories: LiveData<List<Category>>,
        private val categoryClickedListener: (Category) -> Unit,
        private val context: Context,
        lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<CategoriesListAdapter.CategoryItem>() {

    init {
        // Setup observer
        categories.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
    }

    /**
     * Holder class that contains data for a specific category entry.
     */
    class CategoryItem(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * Category text
         */
        val tvCategory: TextView = itemView.findViewById(R.id.tvText)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItem {
        // Inflate view
        val view = LayoutInflater.from(context).inflate(R.layout.item_bottom_sheet, parent, false)

        // Return viewholder
        return CategoryItem(view)
    }

    override fun onBindViewHolder(holder: CategoryItem, position: Int) {
        // Categories available?
        categories.value?.let {

            // Get category
            val category = it[position]

            // Set category text to name
            holder.tvCategory.text = category.name

            // Entry click listener
            holder.itemView.setOnClickListener {
                categoryClickedListener(category)
            }
        }
    }

    override fun getItemCount(): Int {
        // Return number of categories
        return categories.value?.size ?: 0
    }

}