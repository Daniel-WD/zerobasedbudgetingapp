package com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee


/**
 * [PayeesListAdapter] in [context] for displaying a list of [payees].
 * Needs [lifecycleOwner].
 */
class PayeesListAdapter(
        private val payees: LiveData<List<Payee>>,
        private val payeeClickedListener: (Payee) -> Unit,
        private val context: Context,
        lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<PayeesListAdapter.PayeeItem>() {

    init {
        // Setup observers
        payees.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
    }

    /**
     * Holder class that contains data for a specific payee entry.
     */
    class PayeeItem(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * Payee text
         */
        val tvPayee: TextView = itemView.findViewById(R.id.tvText)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayeeItem {
        // Inflate view
        val view = LayoutInflater.from(context).inflate(R.layout.item_bottom_sheet, parent, false)

        // Return ViewHolder
        return PayeeItem(view)
    }

    override fun onBindViewHolder(holder: PayeeItem, position: Int) {
        // Payees available?
        payees.value?.let {

            // Get payee
            val payee = it[position]

            // Set payee text
            holder.tvPayee.text = payee.name

            // Entry click listener
            holder.itemView.setOnClickListener {
                payeeClickedListener(payee)
            }
        }
    }

    override fun getItemCount(): Int {
        // Return number of payees
        return payees.value?.size ?: 0
    }

}