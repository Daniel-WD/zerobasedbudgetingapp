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
 * Adapter for displaying a list of categories.
 * @param categories Containing categories
 * @param mCategoryClickedListener Callback for click event on category
 * @param mContext Context
 * @param lifecycleOwner LifecycleOwner
 */
class CategoriesListAdapter(
    private val mCategories: LiveData<List<Category>>,
    private val mCategoryClickedListener: (Category) -> Unit,
    private val mContext: Context,
    lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<CategoriesListAdapter.CategoryItem>() {

    init {
        mCategories.observe(lifecycleOwner) {
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
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_bottom_sheet, parent, false)

        // Return viewholder
        return CategoryItem(view)
    }

    override fun onBindViewHolder(holder: CategoryItem, position: Int) {
        // Categories available?
        mCategories.value?.let {

            // Get category
            val category = it[position]

            // Set category text to name
            holder.tvCategory.text = category.name

            // Entry click listener
            holder.itemView.setOnClickListener {
                mCategoryClickedListener(category)
            }
        }
    }

    override fun getItemCount(): Int {
        // Return number of categories
        return mCategories.value?.size ?: 0
    }

}