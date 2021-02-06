package com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.datamanager.Category

/**
 * Adapter for displaying a list of categories.
 * @param mCategories Containing categories
 * @param mCategoryClickedListener Callback for click event on category
 * @param mContext Context
 */
class CategoriesListAdapter(
    private val mCategories: List<Category>,
    private val mCategoryClickedListener: (String) -> Unit,
    private val mContext: Context
) : RecyclerView.Adapter<CategoriesListAdapter.CategoryItem>() {

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
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_bottom_sheet, parent, false)

        // Create viewholder
        val viewHolder = CategoryItem(view)

        // Entry click listener
        view.setOnClickListener {
            mCategoryClickedListener(viewHolder.tvCategory.text as String)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: CategoryItem, position: Int) {
        // Set category text to name
        holder.tvCategory.text = mCategories[position].name
    }

    override fun getItemCount(): Int {
        // Return number of categories
        return mCategories.size
    }

}