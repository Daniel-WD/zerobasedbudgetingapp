package com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category

/**
 * Adapter for displaying a list of budgeting items.
 * @param mCategories Containing categories
 * @param mMonthTimestamp Month timestamp
 * @param mItemClickedListener Callback for click event on item
 * @param mContext Context
 * @param lifecycleOwner LifecycleOwner
 */
class BudgetingListAdapter(
    private val mCategories: LiveData<List<Category>>,
    private val mBudgetsOfMonth: LiveData<List<Budget>>,
    private val mAvailableMoney: LiveData<Map<Category, Long>>,
    private val mItemClickedListener: (Budget) -> Unit,
    private val mContext: Context,
    lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<BudgetingListAdapter.BudgetingItem>() {

    init {
        mCategories.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
        mBudgetsOfMonth.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
        mAvailableMoney.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
    }

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
        val categories = mCategories.value
        val budgets = mBudgetsOfMonth.value
        val availableMoney = mAvailableMoney.value

        // Categories and budgets present?
        if(categories != null && budgets != null && availableMoney != null && categories.size == budgets.size && categories.size == availableMoney.size) {

            // Get category
            val category = categories[position]

            // Set category text to name
            holder.tvCategory.text = category.name

            // Find budget
            val budget = budgets.find { budget -> budget.categoryName == category.name }!!
            holder.tvBudgeted.text = (budget.budgeted).toString()

            // Set available value
            holder.tvAvailable.text = availableMoney[category].toString()

            // Entry click listener
            holder.itemView.setOnClickListener {
                mItemClickedListener(budget)
            }
        }

    }

    override fun getItemCount(): Int {
        // Return number of entries
        return mCategories.value?.size ?: 0
    }

}