package com.example.expensemanager.app.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.app.App
import com.example.expensemanager.app.R
import com.example.expensemanager.app.database.Transaction
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader


class TransactionAdapter(private val context: Context, private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {


    val iconDialogIconPack: IconPack?
        get() = (context.applicationContext as App).iconPack

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

        holder.sourceOrCategoryText.text = transaction.sourceOrCategory
        holder.dateText.text = "${transaction.date} ${transaction.time.substring(0,5)}"

        val iconPack = iconDialogIconPack
        if (iconPack != null) {
            try {
                val loader = IconPackLoader(context)
                val drawable = iconPack.getIconDrawable(transaction.categoryIcon,loader.drawableLoader )
                if (drawable != null) {
                    holder.iconImageView.setImageDrawable(drawable)
                } else {
                    Log.w("CategoryAdapter", "Icon not found for ID: ${transaction.categoryIcon}")
                    holder.iconImageView.setImageResource(R.drawable.ic_home)
                }
            } catch (e: Exception) {
                Log.e("CategoryAdapter", "Error loading icon: ${e.message}")
               holder. iconImageView.setImageResource(R.drawable.ic_home)
            }
        } else {
            Log.e("CategoryAdapter", "IconPack is null!")
            holder.iconImageView.setImageResource(R.drawable.ic_home)
        }


        if (transaction.type == "Expense") {
            holder.iconImageView.setColorFilter(Color.parseColor(transaction.categoryColor ?: "#000000"))
            holder.amountText.text = "-"+transaction.amount.toString()
            holder.amountText.setTextColor(ContextCompat.getColor(context, R.color.expense_amount))
        } else {

            holder.iconImageView.setImageResource(R.drawable.salary_rupee)
            holder.iconImageView.setColorFilter(Color.parseColor(transaction.categoryColor ?: "#59954D"))
            holder.amountText.text = "+"+transaction.amount.toString()
            holder.amountText.setTextColor(ContextCompat.getColor(context, R.color.income_ammount))
        }
    }

    override fun getItemCount(): Int = transactions.size

    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
