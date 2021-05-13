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
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetWithCategory
import com.titaniel.zerobasedbudgetingapp.utils.setMoneyValue

/**
 * [BudgetListAdapter] in [context] for displaying [budgetsWithCategoryOfMonth] with the respective [availableMoney]. Notifies [itemClickedListener] when item is clicked.
 * Needs [lifecycleOwner].
 */
class BudgetListAdapter(
    private val budgetsWithCategoryOfMonth: LiveData<List<BudgetWithCategory>>,
    private val availableMoney: LiveData<Map<BudgetWithCategory, Long>>,
    private val itemClickedListener: (Budget) -> Unit,
    private val context: Context,
    lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<BudgetListAdapter.BudgetingItem>() {

    init {
        // Setup observers
        budgetsWithCategoryOfMonth.observe(lifecycleOwner) { budgetsWithCategory ->
            notifyDataSetChanged()
        }
        availableMoney.observe(lifecycleOwner) {
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
         * Available amount text
         */
        val tvAvailable: TextView = itemView.findViewById(R.id.tvAvailable)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetingItem {
        // Inflate view
        val view = LayoutInflater.from(context).inflate(R.layout.item_budget, parent, false)

        // Create ViewHolder
        return BudgetingItem(view)
    }

    override fun onBindViewHolder(holder: BudgetingItem, position: Int) {
        val budgetsWithCategory = budgetsWithCategoryOfMonth.value
        val availableMoney = availableMoney.value

        // Budgets and available money present?
        if (budgetsWithCategory != null && availableMoney != null) {

            // Get category
            val bWithCat = budgetsWithCategory[position]

            // Set category text to name
            holder.tvCategory.text = bWithCat.category.name

            // Find budget
            holder.tvBudgeted.setMoneyValue(bWithCat.budget.budgeted)

            // Set available value
            availableMoney[bWithCat]?.let { holder.tvAvailable.setMoneyValue(it) }

            // Entry click listener
            holder.itemView.setOnClickListener {
                itemClickedListener(bWithCat.budget)
            }
        }

    }

    override fun getItemCount(): Int {
        // Return number of entries
        return budgetsWithCategoryOfMonth.value?.size ?: 0
    }

}