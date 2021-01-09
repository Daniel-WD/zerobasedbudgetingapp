package com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.budget.Category

/**
 * Adapter for displaying a list of payees.
 */
class CategoriesListAdapter(
    private val categories: List<Category>,
    private val categoryClickedListener: (String) -> Unit,
    private val context: Context
): RecyclerView.Adapter<CategoriesListAdapter.CategoryItem>() {

    /**
     * Holder class that contains data for a specific payee entry.
     */
    class CategoryItem(itemView: View): RecyclerView.ViewHolder(itemView) {

        val tvCategory: TextView = itemView.findViewById(R.id.tvText)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItem {
        // Inflate view
        val view = LayoutInflater.from(context).inflate(R.layout.item_bottom_sheet, parent, false)
        val viewHolder = CategoryItem(view)
        view.setOnClickListener {
            categoryClickedListener(viewHolder.tvCategory.text as String)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: CategoryItem, position: Int) {
        holder.tvCategory.text = categories[position].name
    }

    override fun getItemCount(): Int {
        return categories.size
    }

}