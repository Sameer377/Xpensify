package com.example.expensemanager.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.app.R
import com.example.expensemanager.app.database.Category

class HorizontalAdapter(
    private val context: Context,
    private val items: List<Category>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<HorizontalAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.itemIcon)
        val text: TextView = view.findViewById(R.id.itemText)
        val lay:LinearLayout =  view.findViewById(R.id.parent_lin_item);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bottom_sheet_category_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.icon.setImageResource(R.drawable.ic_home)
        holder.text.text = item.name

        holder.itemView.setOnClickListener {
            onItemClick(position) // Pass the position to the callback
            holder.lay.setBackgroundResource(R.drawable.category_item_selected)
        }
    }

    override fun getItemCount(): Int = items.size
}
