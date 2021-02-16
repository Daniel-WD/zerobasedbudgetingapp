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
import com.titaniel.zerobasedbudgetingapp.database.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.utils.Utils

/**
 * Adapter for displaying list of transactions.
 * @param mTransactions Containing transactions
 * @param mTransactionClickedListener Transaction clicked listener
 * @param mContext Context
 * @param lifecycleOwner LifecycleOwner
 */
class TransactionsListAdapter(
    private val mTransactions: LiveData<List<Transaction>>,
    private val mTransactionClickedListener: (Transaction) -> Unit,
    private val mContext: Context,
    lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<TransactionsListAdapter.TransactionItem>() {

    init {
        // Observe transactions
        mTransactions.observe(lifecycleOwner) {
            // Reload list
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
        val imgDescrAvailable: ImageView = itemView.findViewById(R.id.imgDescrAvailable)

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
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_transaction, parent, false)

        // Create viewholder
        return TransactionItem(view)
    }

    override fun onBindViewHolder(holder: TransactionItem, position: Int) {
        // Transactions available?
        mTransactions.value?.let {

            // Transaction
            val transaction = it[position]

            // Set image description available visibility
            holder.imgDescrAvailable.visibility =
                if (transaction.description.isEmpty()) INVISIBLE else VISIBLE

            // Set value text
            holder.tvPay.text = transaction.pay.toString()

            // Set payee text
            holder.cpPayee.text = transaction.payeeName

            // Set category text
            holder.cpCategory.text =
                if (transaction.categoryName == Category.TO_BE_BUDGETED) mContext.getString(R.string.activity_add_edit_transaction_to_be_budgeted) else transaction.categoryName

            // Set date text
            holder.tvDate.text = Utils.convertLocalDateToString(transaction.date)

            // Set click listener, item click callback
            holder.itemView.setOnClickListener {
                mTransactionClickedListener(transaction)
            }
        }

    }

    override fun getItemCount(): Int {
        // Return number transactions
        return mTransactions.value?.size ?: 0
    }

}