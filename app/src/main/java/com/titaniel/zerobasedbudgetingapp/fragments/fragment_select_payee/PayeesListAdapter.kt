package com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.titaniel.zerobasedbudgetingapp.R

/**
 * Adapter for displaying a list of payees.
 */
class PayeesListAdapter(
    private val payees: List<String>,
    private val context: Context
): RecyclerView.Adapter<PayeesListAdapter.PayeeItem>() {

    /**
     * Holder class that contains data for a specific payee entry.
     */
    class PayeeItem(itemView: View): RecyclerView.ViewHolder(itemView) {

        val tvPayee: TextView = itemView.findViewById(R.id.tvText)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayeeItem {
        // Inflate view
        val view = LayoutInflater.from(context).inflate(R.layout.item_bottom_sheet, parent, false)
        return PayeeItem(view)
    }

    override fun onBindViewHolder(holder: PayeeItem, position: Int) {
        holder.tvPayee.text = payees[position]
    }

    override fun getItemCount(): Int {
        return payees.size
    }

}