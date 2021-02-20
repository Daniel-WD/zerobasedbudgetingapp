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

/**
 * [BudgetingListAdapter] in [mContext] for displaying [mBudgetsOfMonth] with the respective [mAvailableMoney]. Notifies [mItemClickedListener] when item is clicked.
 * Needs [lifecycleOwner].
 */
class BudgetingListAdapter(
    private val mBudgetsOfMonth: LiveData<List<Budget>>,
    private val mAvailableMoney: LiveData<Map<Budget, Long>>,
    private val mItemClickedListener: (Budget) -> Unit,
    private val mContext: Context,
    lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<BudgetingListAdapter.BudgetingItem>() {

    init {
        // Setup observers
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
        val budgets = mBudgetsOfMonth.value
        val availableMoney = mAvailableMoney.value

        // Categories and budgets present?
        if(budgets != null && availableMoney != null && budgets.size == availableMoney.size) {

            // Get category
            val b = budgets[position]

            // Set category text to name
            holder.tvCategory.text = b.categoryName

            // Find budget
            holder.tvBudgeted.text = (b.budgeted).toString()

            // Set available value
            holder.tvAvailable.text = availableMoney[b].toString()

            // Entry click listener
            holder.itemView.setOnClickListener {
                mItemClickedListener(b)
            }
        }

    }

    override fun getItemCount(): Int {
        // Return number of entries
        return mBudgetsOfMonth.value?.size ?: 0
    }

}