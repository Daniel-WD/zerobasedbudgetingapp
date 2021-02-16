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
 * Adapter for displaying a list of payees.
 * @param mPayees Containing payees
 * @param mPayeeClickedListener Callback for click event on payee
 * @param mContext Context
 * @param lifecycleOwner LifecycleOwner
 */
class PayeesListAdapter(
    private val mPayees: LiveData<List<Payee>>,
    private val mPayeeClickedListener: (Payee) -> Unit,
    private val mContext: Context,
    lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<PayeesListAdapter.PayeeItem>() {

    init {
        // Observe payees
        mPayees.observe(lifecycleOwner) {

            // Reload list
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
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_bottom_sheet, parent, false)

        // Return viewholder
        return PayeeItem(view)
    }

    override fun onBindViewHolder(holder: PayeeItem, position: Int) {
        // Payees available?
        mPayees.value?.let {

            // Get payee
            val payee = it[position]

            // Set payee text
            holder.tvPayee.text = payee.name

            // Entry click listener
            holder.itemView.setOnClickListener {
                mPayeeClickedListener(payee)
            }
        }
    }

    override fun getItemCount(): Int {
        // Return number of payees
        return mPayees.value?.size ?: 0
    }

}