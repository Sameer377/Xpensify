package com.example.expensemanager.app.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.app.App
import com.example.expensemanager.app.R
import com.example.expensemanager.app.database.Category
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader

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

    val iconDialogIconPack: IconPack?
        get() = (context.applicationContext as App).iconPack

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.text.text = item.name
        var flag : Int = 0
        holder.itemView.setOnClickListener {
            onItemClick(position) // Pass the position to the callback
            if(flag == 0){
                holder.lay.setBackgroundResource(R.drawable.category_item_selected)
                flag=1
            }
            else{
                flag=0
                holder.lay.setBackgroundResource(R.drawable.category_item_unselected)
            }

        }

        val iconPack = iconDialogIconPack
        if (iconPack != null) {
            try {
                val loader = IconPackLoader(context)
                val drawable = iconPack.getIconDrawable(item.icon, loader.drawableLoader)
                if (drawable != null) {
                    holder.icon.setColorFilter(Color.parseColor(item.color ?: "#000000"))
                    holder.icon.setImageDrawable(drawable)
                } else {
                    Log.w("CategoryAdapter", "Icon not found for ID: ${item.icon}")
                    holder.icon.setImageResource(R.drawable.ic_home)
                }
            } catch (e: Exception) {
                Log.e("CategoryAdapter", "Error loading icon: ${e.message}")
                holder.icon.setImageResource(R.drawable.ic_home)
            }

        }
    }

    override fun getItemCount(): Int = items.size
}
