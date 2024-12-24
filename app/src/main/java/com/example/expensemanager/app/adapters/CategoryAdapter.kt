package com.example.expensemanager.app.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.expensemanager.app.App
import com.example.expensemanager.app.R
import com.example.expensemanager.app.database.AppDatabase
import com.example.expensemanager.app.database.Category
import com.example.expensemanager.app.database.CategoryData
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CategoryAdapter(
    private val context: Context,
    private val categories: List<CategoryData>,
    private val totalExpenses:Double
) : BaseAdapter()  {


//    val iconDialogIconPack: IconPack?
//        get() = (context as App).iconPack

    override fun getCount(): Int = categories.size

    override fun getItem(position: Int): CategoryData = categories[position]

    override fun getItemId(position: Int): Long = categories[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_category, parent, false
        )

        val category = getItem(position)

        // Bind data to views
        val nameTextView = view.findViewById<TextView>(R.id.item_name)
        val amountTextView = view.findViewById<TextView>(R.id.item_amount)
        val item_percentage = view.findViewById<TextView>(R.id.item_percentage)
        val iconImageView = view.findViewById<ImageView>(R.id.iconImageView)
        val progress = view.findViewById<LinearProgressIndicator>(R.id.progress_category)
        var percent : Double = 0.0
        nameTextView.text = category.category_name
        // Set a placeholder icon or fetch from `category.icon` if available

        val iconPack = iconDialogIconPack
        if (iconPack != null) {
            try {
                val loader = IconPackLoader(context)
                val drawable = iconPack.getIconDrawable(category.categoryIcon,loader.drawableLoader )
                if (drawable != null) {
                    iconImageView.setColorFilter(Color.parseColor(category.categoryColor ?: "#000000"))
                    iconImageView.setImageDrawable(drawable)
                } else {
                    Log.w("CategoryAdapter", "Icon not found for ID: ${category.categoryIcon}")
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

                    amountTextView.text = category.amount.toString()
                    percent = category.amount.toDouble() / (totalExpenses / 100)
                    progress.setIndicatorColor(Color.parseColor(category.categoryColor))
                    progress.setProgress(percent.toInt())
                    item_percentage.text = percent.toInt().toString()+"%"

        return view
    }

     val iconDialogIconPack: IconPack?
        get() = (context.applicationContext as App).iconPack



}