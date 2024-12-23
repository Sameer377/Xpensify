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
    private val categories: List<Category>,
    private val totalExpenses:Double
) : BaseAdapter()  {


//    val iconDialogIconPack: IconPack?
//        get() = (context as App).iconPack

    override fun getCount(): Int = categories.size

    override fun getItem(position: Int): Category = categories[position]

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



        CoroutineScope(Dispatchers.IO).launch {
            val totalExpense = context?.let { AppDatabase.getDatabase(it).expenseDao().getTotalExpenseForCategory(category.id) }
            var percent : Double = 0.0

            CoroutineScope(Dispatchers.Main).launch {
                if (totalExpense != null) {

                    amountTextView.text = totalExpense.toString()
                    percent = totalExpense.toDouble() / (totalExpenses / 100)
                    if(category.color!=null){
                        progress.setIndicatorColor(Color.parseColor(category.color))
                    }
                    progress.setProgress(percent.toInt())
                    item_percentage.text = percent.toInt().toString()+"%"
                }else{

                }
            }

        }

        return view
    }

     val iconDialogIconPack: IconPack?
        get() = (context.applicationContext as App).iconPack



}