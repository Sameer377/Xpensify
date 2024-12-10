package com.example.expensemanager.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.expensemanager.app.R
import com.example.expensemanager.app.database.Category


class OnlyCategoryAdapter(
    private val context: Context,
    private val categories: List<Category>
) : BaseAdapter() {

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
        val iconImageView = view.findViewById<ImageView>(R.id.item_icons)

        nameTextView.text = category.name
        // Set a placeholder icon or fetch from `category.icon` if available
        iconImageView.setImageResource(R.drawable.ic_home)

        return view
    }
}