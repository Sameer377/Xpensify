import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.expensemanager.app.App
import com.example.expensemanager.app.R
import com.example.expensemanager.app.database.AppDatabase
import com.example.expensemanager.app.database.Category
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class NewCategory : AppCompatActivity() , IconDialog.Callback{

    private lateinit var editTextCategory: EditText
    private lateinit var buttonAddCategory: Button
    private lateinit var selectedicon: ImageView
    private var selectedIcons: Int = 0 // Store the selected icon id

    @SuppressLint("MissingInflatedId")
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

    private fun initUi() {
        editTextCategory = findViewById(R.id.category_edit_text)
        buttonAddCategory = findViewById(R.id.create_category_btn)
        selectedicon = findViewById(R.id.selected_icon)

        buttonAddCategory.setOnClickListener {
            val categoryName = editTextCategory.text.toString().trim()

            if (categoryName.isNotEmpty()) {
                if (selectedIcons != 0) {
                    // Pass category name and selected icon ID
                    addCategory(categoryName, selectedIcons)
                } else {
                    Toast.makeText(this, "Please select at least one icon", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addCategory(categoryName: String, icon: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val categoryDao = AppDatabase.getDatabase(this@NewCategory).categoryDao()

            // Insert new category
            val newCategory = Category(name = categoryName, icon = icon)
            categoryDao.insert(newCategory)

            // Reload categories
            val categories = categoryDao.getAllCategories()

            withContext(Dispatchers.Main) {
                // Clear the EditText and show a success message
                editTextCategory.text.clear()
                Toast.makeText(this@NewCategory, "Category added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override val iconDialogIconPack: IconPack?
        get() = (application as App).iconPack

    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<Icon>) {
        // Show a toast with the list of selected icon IDs.
        Toast.makeText(this, "Icons selected: ${icons.map { it.id }}", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val ICON_DIALOG_TAG = "icon-dialog"
    }
}
