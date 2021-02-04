package com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.datamanager.Category

/**
 * Adapter for displaying a list of budgeting items.
 * @param mCategories Containing categories
 * @param mMonthTimestamp Month timestamp
 * @param mItemClickedListener Callback for click event on item
 * @param mContext Context
 */
class BudgetingListAdapter(
    private val mCategories: List<Category>,
    private val mMonthTimestamp: Long,
    private val mItemClickedListener: (Category) -> Unit,
    private val mContext: Context
) : RecyclerView.Adapter<BudgetingListAdapter.BudgetingItem>() {

    /**
     * Holder class that contains data for a specific category entry.
     */
    class BudgetingItem(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * Category text
         */
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)

        /**
         * Budgeted amount text
         */
        val tvBudgeted: TextView = itemView.findViewById(R.id.tvBudgeted)

        /**
         * Avaialable amount text
         */
        val tvAvailable: TextView = itemView.findViewById(R.id.tvAvailable)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetingItem {
        // Inflate view
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_budget, parent, false)

        // Create viewholder
        return BudgetingItem(view)
    }

    override fun onBindViewHolder(holder: BudgetingItem, position: Int) {
        val category = mCategories[position]

        // Set category text to name
        holder.tvCategory.text = category.name

        // Set budgeted value
        holder.tvBudgeted.text = (category.manualBudgetedMoney[mMonthTimestamp] ?: 0).toString()

        // Set available value
        holder.tvAvailable.text = category.realBudgetedValue(mMonthTimestamp).toString()

        // Entry click listener
        holder.itemView.setOnClickListener {
            mItemClickedListener(mCategories[position])
        }
    }

    override fun getItemCount(): Int {
        // Return number of entries
        return mCategories.size
    }

}