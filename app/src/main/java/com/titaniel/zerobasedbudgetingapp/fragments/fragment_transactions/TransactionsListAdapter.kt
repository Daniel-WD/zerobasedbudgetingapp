package com.titaniel.zerobasedbudgetingapp.fragments.fragment_transactions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.utils.Utils

/**
 * [TransactionsListAdapter] in [context] for displaying a list of [transactions].
 * Needs [lifecycleOwner].
 */
class TransactionsListAdapter(
        private val transactions: LiveData<List<Transaction>>,
        private val transactionClickedListener: (Transaction) -> Unit,
        private val context: Context,
        lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<TransactionsListAdapter.TransactionItem>() {

    init {
        // Set observers
        transactions.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
    }

    /**
     * Holder class that contains data for a specific transaction entry.
     */
    class TransactionItem(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * Description available image
         */
        val imgDescriptionAvailable: ImageView = itemView.findViewById(R.id.imgDescriptionAvailable)

        /**
         * Value text
         */
        val tvPay: TextView = itemView.findViewById(R.id.tvPay)

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
        val view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false)

        // Create ViewHolder
        return TransactionItem(view)
    }

    override fun onBindViewHolder(holder: TransactionItem, position: Int) {
        // Transactions available?
        transactions.value?.let {

            // Transaction
            val transaction = it[position]

            // Set image description available visibility
            holder.imgDescriptionAvailable.visibility =
                if (transaction.description.isEmpty()) INVISIBLE else VISIBLE

            // Set value text
            holder.tvPay.text = transaction.pay.toString()

            // Set payee text
            holder.cpPayee.text = transaction.payeeName

            // Set category text
            holder.cpCategory.text =
                if (transaction.categoryName == Category.TO_BE_BUDGETED) context.getString(R.string.activity_add_edit_transaction_to_be_budgeted) else transaction.categoryName

            // Set date text
            holder.tvDate.text = Utils.convertLocalDateToString(transaction.date)

            // Set click listener, item click callback
            holder.itemView.setOnClickListener {
                transactionClickedListener(transaction)
            }
        }

    }

    override fun getItemCount(): Int {
        // Return number transactions
        return transactions.value?.size ?: 0
    }

}