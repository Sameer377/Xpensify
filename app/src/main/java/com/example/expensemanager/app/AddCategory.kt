package com.example.expensemanager.app
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.expensemanager.app.database.AppDatabase
import com.example.expensemanager.app.database.Category
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.defaults.colorpicker.ColorPickerPopup

class AddCategory : AppCompatActivity() , IconDialog.Callback{

    private lateinit var editTextCategory: EditText
    private lateinit var buttonAddCategory: Button
    private lateinit var selectedicon: ImageView
    private var selectedIcons: Int = 0 // Store the selected icon id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_category)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val icon_btn: CardView = findViewById(R.id.icon_btn)

        val iconDialog = supportFragmentManager.findFragmentByTag(ICON_DIALOG_TAG) as IconDialog?
            ?: IconDialog.newInstance(IconDialogSettings())

        icon_btn.setOnClickListener {
            // Open icon dialog
            iconDialog.show(supportFragmentManager, ICON_DIALOG_TAG)
        }

        var btn = findViewById<ImageView>(R.id.back_btn)

        btn.setOnClickListener {
            finish()
        }

        initUi()
    }

    val colorList = listOf(
        "#FF5733", "#33FF57", "#5733FF", "#FF33A1", "#33A1FF", "#A1FF33", "#FFD700", "#00FFFF",
        "#8A2BE2", "#7FFF00", "#FF1493", "#1E90FF", "#FFDAB9", "#00FF7F", "#9400D3", "#DC143C",
        "#00CED1", "#F4A460", "#556B2F", "#4682B4", "#FF4500", "#2E8B57", "#6A5ACD", "#00BFFF",
        "#7CFC00", "#FF69B4", "#9932CC", "#FF6347", "#40E0D0", "#EE82EE", "#FFA500", "#ADFF2F",
        "#FF0000", "#8B4513", "#5F9EA0", "#D2691E", "#6B8E23", "#B22222", "#9ACD32", "#20B2AA",
        "#6495ED", "#FF00FF", "#32CD32", "#FF8C00", "#BDB76B", "#FF00FF", "#48D1CC", "#C71585",
        "#4B0082", "#FFD700"
    )

    fun getNextUniqueColor(context: Context): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("ColorPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Retrieve the last used index, default to -1 if not found
        val lastIndex = sharedPreferences.getInt("LastColorIndex", -1)

        // Calculate the next index
        val nextIndex = (lastIndex + 1) % colorList.size

        // Save the new index back to SharedPreferences
        editor.putInt("LastColorIndex", nextIndex)
        editor.apply()

        // Return the color at the next index
        return colorList[nextIndex]
    }

    private var logo_color : String = "#000000"

    private fun initUi() {
        editTextCategory = findViewById<AppCompatEditText>(R.id.category_edit_text)
        buttonAddCategory = findViewById(R.id.create_category_btn)
        selectedicon = findViewById(R.id.selected_icon)

        buttonAddCategory.setOnClickListener {
            val categoryName = editTextCategory.text.toString().trim()

            if (categoryName.isNotEmpty()) {
                if (selectedIcons != 0) {
                    // Pass category name and selected icon ID
                    addCategory(categoryName, selectedIcons,logo_color)
                } else {
                    Toast.makeText(this, "Please select at least one icon", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addCategory(categoryName: String, icon: Int,color:String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val categoryDao = AppDatabase.getDatabase(this@AddCategory).categoryDao()

            // Insert new category
            val newCategory = Category(name = categoryName, icon = icon, color = color)
            categoryDao.insert(newCategory)

            // Reload categories
            val categories = categoryDao.getAllCategories()

            withContext(Dispatchers.Main) {
                // Clear the EditText and show a success message
                editTextCategory.text.clear()
                Toast.makeText(this@AddCategory, "Category added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override val iconDialogIconPack: IconPack?
        get() = (application as App).iconPack

    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<Icon>) {
        val iconPack = iconDialogIconPack
        val loader = IconPackLoader(this)
        selectedIcons = icons[0].id

        if(iconPack!=null){
            val drawable = iconPack.getIconDrawable(selectedIcons,loader.drawableLoader)

            if (drawable != null) {
                // Set the drawable to the ImageView
                selectedicon.setImageDrawable(drawable)
                logo_color = getNextUniqueColor(applicationContext)
                selectedicon.setColorFilter(Color.parseColor(logo_color ?: "#000000"))

                /*val colorPickerObserver = object : ColorPickerPopup.ColorPickerObserver {
                    override fun onColorSelected(color: Int) {
                        // Convert the selected color to Hex code
                        val hexColor = "#${Integer.toHexString(color).uppercase()}"
                        // Show a Toast with the selected hex color
                        Toast.makeText(this@AddCategory, "Selected Color: $hexColor", Toast.LENGTH_SHORT).show()
                    }
                }

                ColorPickerPopup.Builder(this)
                    .initialColor(Color.RED) // Default color
                    .enableBrightness(true) // Enable brightness slider
                    .enableAlpha(true) // Enable alpha slider
                    .okTitle("Choose")
                    .cancelTitle("Cancel")
                    .showIndicator(true)
                    .showValue(true)
                    .build()
                    .show(selectedicon,colorPickerObserver) {
                        // Convert the selected color to Hex code
                        val hexColor = "#${Integer.toHexString(it.toArgb()).uppercase()}"
                        // Show a Toast with the selected hex color
                        Toast.makeText(this, "Selected Color: $hexColor", Toast.LENGTH_SHORT).show()
                    }*/

            }else {
                Toast.makeText(this, "Failed to load the selected icon.", Toast.LENGTH_SHORT).show()
            }
        }else {
            Toast.makeText(this, "Icon pack is null.", Toast.LENGTH_SHORT).show()
        }

    }

    companion object {
        private const val ICON_DIALOG_TAG = "icon-dialog"
    }
}
