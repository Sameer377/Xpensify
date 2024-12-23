package com.example.expensemanager.app.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.expensemanager.app.App
import com.example.expensemanager.app.R
import com.example.expensemanager.app.database.Category
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader


class OnlyCategoryAdapter(
    private val context: Context,
    private val categories: List<Category>
) : BaseAdapter() {

    val iconDialogIconPack: IconPack?
        get() = (context.applicationContext as App).iconPack



    override fun getCount(): Int = categories.size

    override fun getItem(position: Int): Category = categories[position]

    override fun getItemId(position: Int): Long = categories[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.only_category_item, parent, false
        )

        val category = getItem(position)

        // Bind data to views
        val nameTextView = view.findViewById<TextView>(R.id.item_name)
        val iconImageView = view.findViewById<ImageView>(R.id.iconImageView)

        nameTextView.text = category.name
        // Set a placeholder icon or fetch from `category.icon` if available
        val iconPack = iconDialogIconPack
        if (iconPack != null) {
            try {
                val loader = IconPackLoader(context)
                val drawable = iconPack.getIconDrawable(category.icon,loader.drawableLoader )
                if (drawable != null) {
                    iconImageView.setColorFilter(Color.parseColor(category.color ?: "#000000"))
                    iconImageView.setImageDrawable(drawable)
                } else {
                    Log.w("CategoryAdapter", "Icon not found for ID: ${category.icon}")
                    iconImageView.setImageResource(R.drawable.ic_home)
                }
            } catch (e: Exception) {
                Log.e("CategoryAdapter", "Error loading icon: ${e.message}")
                iconImageView.setImageResource(R.drawable.ic_home)
            }
        } else {
            Log.e("CategoryAdapter", "IconPack is null!")
            iconImageView.setImageResource(R.drawable.ic_home)
        }

        return view
    }
}