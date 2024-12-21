package com.example.expensemanager.app.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.app.R
import com.example.expensemanager.app.database.Transaction


class TransactionAdapter(private val context: Context, private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amountText: TextView = itemView.findViewById(R.id.amountText)
        val sourceOrCategoryText: TextView = itemView.findViewById(R.id.sourceOrCategoryText)
        val dateText: TextView = itemView.findViewById(R.id.dateText)
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.amountText.text = transaction.amount.toString()
        holder.sourceOrCategoryText.text = transaction.sourceOrCategory
        holder.dateText.text = "${transaction.date} ${transaction.time.substring(0,5)}"

        if (transaction.type == "Expense") {
            holder.iconImageView.setImageResource(transaction.categoryIcon ?: R.drawable.ic_home)
            holder.iconImageView.setColorFilter(Color.parseColor(transaction.categoryColor ?: "#000000"))
            holder.amountText.setTextColor(ContextCompat.getColor(context, R.color.expense_amount))
        } else {
            holder.iconImageView.setImageResource(R.drawable.ic_home)
            holder.amountText.setTextColor(ContextCompat.getColor(context, R.color.income_ammount))
        }
    }

    override fun getItemCount(): Int = transactions.size

    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
