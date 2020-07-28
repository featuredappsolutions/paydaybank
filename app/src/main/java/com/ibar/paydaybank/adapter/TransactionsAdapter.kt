package com.ibar.paydaybank.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ibar.paydaybank.R
import com.ibar.paydaybank.data.Transaction
import com.ibar.paydaybank.util.Constants

class TransactionsAdapter(private val transactionList: List<Transaction>) : RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.row_transactions, parent, false)
        return TransactionsViewHolder(inflatedView)
    }

    override fun getItemCount() = transactionList.size

    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) {
        val transaction = transactionList.get(position)
        holder.bindTransaction(transaction)
    }


    class TransactionsViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var tvTransactionCategory: TextView? = null
        private var tvTransactionAmount: TextView? = null
        private var tvTransactionDate: TextView? = null

        init {
            tvTransactionCategory = itemView.findViewById(R.id.tvTransactionCategory)
            tvTransactionAmount = itemView.findViewById(R.id.tvTransactionAmount)
            tvTransactionDate = itemView.findViewById(R.id.tvTransactionDate)
        }

        fun bindTransaction(transaction: Transaction) {
            tvTransactionCategory?.text = transaction.category
            tvTransactionAmount?.text = transaction.amount.plus(" $")
            tvTransactionDate?.text = Constants.DATETIME_DATE_FORMAT.format(Constants.ZULU_DATE_FORMAT.parse(transaction.date))
        }
    }
}


