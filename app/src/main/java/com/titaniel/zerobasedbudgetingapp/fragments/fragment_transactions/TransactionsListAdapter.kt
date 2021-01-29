package com.titaniel.zerobasedbudgetingapp.fragments.fragment_transactions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.datamanager.Transaction
import com.titaniel.zerobasedbudgetingapp.utils.Utils

/**
 * Adapter for displaying list of transactions.
 * @param mTransactions Containing transactions
 * @param mContext Context
 */
class TransactionsListAdapter(
    private val mTransactions: List<Transaction>,
    private val mTransactionClickedListener: (Transaction) -> Unit,
    private val mContext: Context
) : RecyclerView.Adapter<TransactionsListAdapter.TransactionItem>() {

    /**
     * Holder class that contains data for a specific transaction entry.
     */
    class TransactionItem(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * Description available image
         */
        val imgDescrAvailable: ImageView = itemView.findViewById(R.id.imgDescrAvailable)

        /**
         * Value text
         */
        val tvValue: TextView = itemView.findViewById(R.id.tvValue)

        /**
         * Date text
         */
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        /**
         * Payee chip
         */
        val cpPayee: Chip = itemView.findViewById(R.id.cpPayee)

        /**
         * Category chip
         */
        val cpCategory: Chip = itemView.findViewById(R.id.cpCategory)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionItem {
        // Inflate view
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_transaction, parent, false)

        // Create viewholder
        return TransactionItem(view)
    }

    override fun onBindViewHolder(holder: TransactionItem, position: Int) {
        // Transaction
        val transaction = mTransactions[position]

        // Set image description available visibility
        holder.imgDescrAvailable.visibility =
            if (transaction.description.isEmpty()) INVISIBLE else VISIBLE

        // Set value text
        holder.tvValue.text = transaction.value.toString()

        // Set payee text
        holder.cpPayee.text = transaction.payee

        // Set category text
        holder.cpCategory.text = transaction.category

        // Set date text
        holder.tvDate.text = Utils.convertUtcToString(transaction.utcTimestamp)

        // Set click listener
        holder.itemView.setOnClickListener {
            mTransactionClickedListener(transaction)
        }

    }

    override fun getItemCount(): Int {
        // Return number transactions
        return mTransactions.size
    }

}