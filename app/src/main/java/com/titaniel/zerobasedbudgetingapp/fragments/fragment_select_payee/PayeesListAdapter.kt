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
 * @param mPayees Containing payees
 * @param mPayeeClickedListener Callback for click event on payee
 * @param mContext Context
 */
class PayeesListAdapter(
    private val mPayees: List<String>,
    private val mPayeeClickedListener: (String) -> Unit,
    private val mContext: Context
) : RecyclerView.Adapter<PayeesListAdapter.PayeeItem>() {

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
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_bottom_sheet, parent, false)

        // Create viewholder
        val viewHolder = PayeeItem(view)

        // Entry click listener
        view.setOnClickListener {
            mPayeeClickedListener(viewHolder.tvPayee.text as String)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PayeeItem, position: Int) {
        // Set payee text
        holder.tvPayee.text = mPayees[position]
    }

    override fun getItemCount(): Int {
        // Return number of payees
        return mPayees.size
    }

}