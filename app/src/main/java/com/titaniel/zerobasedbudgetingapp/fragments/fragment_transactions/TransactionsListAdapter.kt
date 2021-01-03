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
import com.titaniel.zerobasedbudgetingapp.transaction.Transaction
import java.text.SimpleDateFormat
import java.util.*

class TransactionsListAdapter(
    private val transactions: List<Transaction>,
    private val context: Context
) : RecyclerView.Adapter<TransactionsListAdapter.TransactionItem>() {

    class TransactionItem(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgDescription: ImageView = itemView.findViewById(R.id.imgDescription)
        val tvValue: TextView = itemView.findViewById(R.id.tvValue)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val cpPayee: Chip = itemView.findViewById(R.id.cpPayee)
        val cpCategory: Chip = itemView.findViewById(R.id.cpCategory)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionItem {
        val view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false)
        return TransactionItem(view)
    }

    override fun onBindViewHolder(holder: TransactionItem, position: Int) {
        val transaction = transactions[position];
        holder.imgDescription.visibility =
            if (transaction.description.isEmpty()) INVISIBLE else VISIBLE
        holder.tvValue.text = transaction.value.toString()

        val dateFormat = SimpleDateFormat.getDateInstance();
        holder.tvDate.text = dateFormat.format(transaction.date)

        holder.cpPayee.text = transaction.payee
        holder.cpCategory.text = transaction.category.name
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

}