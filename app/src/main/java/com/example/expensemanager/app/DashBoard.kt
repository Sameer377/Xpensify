package com.example.expensemanager.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.Directory.PACKAGE_NAME
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.expensemanager.app.database.AppDatabase
import com.example.expensemanager.app.dialogs.TransactionBottomSheet
import com.example.expensemanager.app.fragments.Categories
import com.example.expensemanager.app.fragments.Transactions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


interface TransactionBottomSheetListener {
    fun onBottomSheetClosed()
}



class DashBoard : AppCompatActivity() ,TransactionBottomSheetListener  {

    // Declare BottomNavigationView as a global variable
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var categories: AppCompatButton
    private lateinit var transaction: AppCompatButton
    private lateinit var floatingActionButton:  FloatingActionButton

    private  var adflag : Int = 0
    private var TAG: String="UPLOAD"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dash_board)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        initUi()
        initListener()

        findViewById<TextView>(R.id.checkbox_google_drive).setOnClickListener {
            TAG="UPLOAD";
            signIn()
        }
        findViewById<TextView>(R.id.nav_backup_restore).setOnClickListener {
            TAG="RESTORE";
            signIn() }
    }






    private fun initUi() {
        adflag = 0
        categories = findViewById<AppCompatButton>(R.id.tab_categories)
        transaction = findViewById<AppCompatButton>(R.id.tab_transaction)
        floatingActionButton = findViewById<FloatingActionButton>(R.id.dash_floating_btn);

        initCategories()

        changeToTransactions()

    }

    private fun initCategories() {
        AppDatabase.getDatabase(this)
//        fetchAndDisplayCategories()
    }

   /* private fun fetchAndDisplayCategories() {
        // Launch a coroutine on the IO thread to fetch data
        CoroutineScope(Dispatchers.IO).launch {
            val categoryDao = AppDatabase.getDatabase(baseContext ).categoryDao()
            val categories = categoryDao.getAllCategories()

//            // Prepare the data to show in the Toast
//            val categoryNames = categories.joinToString { it.name }
//
//            // Switch to the main thread to show the Toast
//            withContext(Dispatchers.Main) {
//                Toast.makeText(baseContext, "Categories: $categoryNames", Toast.LENGTH_LONG).show()
//            }
        }
    }*/


    private fun initListener() {


        val sharedPreferenceManger = SharedPreferenceManger(this)
        getRewardedCoin(sharedPreferenceManger.totalRewardedAmount)
        val myRewardedAds = MyRewardedAds(this)
        myRewardedAds.loadRewardedAds(R.string.rewarded_ads)

        var flagAd : Int = 0

        categories.setOnClickListener{

            if(flagAd==0){
                flagAd++
                myRewardedAds.showRewardedAds(R.string.rewarded_ads){
                    val rewardedCoin = it.amount
                    sharedPreferenceManger.totalRewardedAmount += rewardedCoin
                    getRewardedCoin(sharedPreferenceManger.totalRewardedAmount)
                }
            }

            changeToCategories()

        }

        transaction.setOnClickListener{
            changeToTransactions()
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)



        navigationView.setNavigationItemSelectedListener { menuItem ->

            drawerLayout.closeDrawers()
            true
        }

        var bmenu = findViewById<ImageView>(R.id.btn_burger_menu)

        bmenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

    }



    public fun changeToCategories(){
       categories.setBackgroundResource(R.drawable.filled_btn_rounded);
        categories .setTextColor(ContextCompat.getColor(this, R.color.white))

        transaction.setBackgroundResource(R.drawable.outline_rounded_btn);
        transaction .setTextColor(ContextCompat.getColor(this, R.color.primary_dark_black_dark_1))

        floatingActionButton.setImageResource(R.drawable.ic_edit_light)

        floatingActionButton.setOnClickListener {
            val intent = Intent(this, EditCategories::class.java)
            startActivity(intent)
        }


        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.dash_frame, Categories())

        fragmentTransaction.addToBackStack(null)

        fragmentTransaction.commit()


//            showAds()

    }

    public fun changeToTransactions(){
        categories.setBackgroundResource(R.drawable.outline_rounded_btn);
        categories .setTextColor(ContextCompat.getColor(this, R.color.primary_dark_black_dark_1))

        transaction.setBackgroundResource(R.drawable.filled_btn_rounded);
        transaction .setTextColor(ContextCompat.getColor(this, R.color.white))

        floatingActionButton.setImageResource(R.drawable.ic_add_light)


        val bottomSheetFragment = TransactionBottomSheet()



        floatingActionButton.setOnClickListener {
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }


        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.dash_frame, Transactions())

        fragmentTransaction.addToBackStack(null)

        fragmentTransaction.commit()
    }


    private fun getRewardedCoin(totalRewardedAmount: Int){
    }

    override fun onBottomSheetClosed() {
        changeToTransactions()
        // Add your logic here
    }

//Google Drive API Code implementation
    //***************************************
private val RC_SIGN_IN = 100
    private var googleDriveService: Drive? = null
    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)

        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAccountCredential.usingOAuth2(
                this, listOf(DriveScopes.DRIVE_FILE)
            )
            credential.selectedAccount = account.account
            googleDriveService = Drive.Builder(
                NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                credential
            ).setApplicationName("Xpensify").build()

            when(TAG){
                "UPLOAD" ->{
                    val googleDriveService1 = googleDriveService
                    if (googleDriveService1 != null) {
                        uploadDatabaseToDrive(googleDriveService1)
                    }
                }
                "RESTORE" ->{
                    val googleDriveService2 = googleDriveService
                    if (googleDriveService2 != null) {
                        restoreDatabaseFromDrive(googleDriveService2)
                    }

                }
            }
        } catch (e: ApiException) {
            Log.e("SignInError", "Error signing in: ${e.statusCode}")
            Toast.makeText(applicationContext,"SignInError : Error signing in: ${e.statusCode}",Toast.LENGTH_SHORT).show()

        }
    }

    private fun getDatabaseFile(): File {
        val DATABASE_NAME = "expense_manager_db"
        val PACKAGE_NAME = "com.example.expensemanager.app"
        val DATABASE_PATH = "/data/data/" + PACKAGE_NAME + "/databases/" + DATABASE_NAME
        val dbName = "expense_manager_db" // Replace with your Room database name
        return File(DATABASE_PATH)
    }

    private fun uploadDatabaseToDrive( googleDriveService:Drive) {
        if (googleDriveService == null) {
            Log.e("GoogleDrive", "Google Drive Service not initialized")
            Toast.makeText(applicationContext,"GoogleDrive : Google Drive Service not initialized",Toast.LENGTH_SHORT).show()
            return
        }

        val dbFile = getDatabaseFile()
        AppDatabase.getDatabase(applicationContext).close()
        if (!dbFile.exists()) {
            Log.e("GoogleDrive", "Database file does not exist")
            Toast.makeText(applicationContext,"GoogleDrive : Database file does not exist",Toast.LENGTH_SHORT).show()

            return
        }

        val fileMetadata = com.google.api.services.drive.model.File()
        fileMetadata.name = "expense_manager_db.db"

        val fileContent = FileContent("application/vnd.sqlite3", dbFile)

        Thread {
            try {
                val file = googleDriveService.files().create(fileMetadata, fileContent)
                    .setFields("id")
                    .execute()
                Log.d("GoogleDrive", "Database uploaded with ID: ${file?.id}")
                Looper.prepare()
                Toast.makeText(applicationContext,"GoogleDrive : Database uploaded with ID: ${file?.id}",Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Log.e("GoogleDrive", "Error uploading database: ${e.message}")
                Looper.prepare()
                Toast.makeText(applicationContext,"GoogleDrive : Error uploading database: ${e.message}",Toast.LENGTH_SHORT).show()

            }
            AppDatabase.getDatabase(applicationContext)
        }.start()
    }

//*****************************************************************
 private fun getFileIdByName(driveService: Drive, fileName: String): String? {
    try {
        // Query Google Drive for the file
        val query = "name = '$fileName' and trashed = false"
        val result: FileList = driveService.files().list()
            .setQ(query)
            .setSpaces("drive")
            .setFields("files(id, name)")
            .execute()

        // Check if any files match the name
        val files = result.files
        if (files.isNullOrEmpty()) {
            Log.d("GoogleDrive", "No files found with the name: $fileName")
            return null
        }

        // Return the file ID of the first match
        return files[0].id.also {
            Log.d("GoogleDrive", "File found with ID: $it")
        }
    } catch (e: Exception) {
        Log.e("GoogleDrive", "Error retrieving file ID", e)
    }
    return null
}

    //Restoring database to internal storage
    private fun searchFileInDrive(fileName: String, googleDriveService2: Drive): String? {
        Looper.prepare()
        if (googleDriveService2 == null) {
            Log.e("GoogleDrive", "Google Drive Service not initialized")
            Toast.makeText(applicationContext,"GoogleDrive : Google Drive Service not initialized",Toast.LENGTH_SHORT).show()

            return null
        }

        val query = "name = '$fileName' and mimeType != 'application/vnd.google-apps.folder'"

        return try {
            val result = googleDriveService2.files().list()
                .setQ(query)
                .setSpaces("drive")
                .setFields("files(id, name)")
                .execute()

            if (!result.files.isNullOrEmpty()) {
                if (result != null) {
                    Log.d("GoogleDrive", "File found: ${result.files[0].name}")
                    Toast.makeText(applicationContext,"GoogleDrive : File found: ${result.files[0].name}",Toast.LENGTH_SHORT).show()

                }
                result.files.get(0).id // Return the file ID
            } else {
                Log.e("GoogleDrive", "File not found")
                Toast.makeText(applicationContext,"GoogleDrive : File not found",Toast.LENGTH_SHORT).show()

                null
            }
        } catch (e: Exception) {
            Log.e("GoogleDrive", "Error searching file: ${e.message}")
            Toast.makeText(applicationContext,"GoogleDrive :Error searching file: ${e.message}",Toast.LENGTH_SHORT).show()

            null
        }
    }

    private fun downloadFileFromDrive(fileId: String, destinationFile: File, googleDriveService2: Drive,dbName:String) {
        if (googleDriveService2 == null) {
            Log.e("GoogleDrive", "Google Drive Service not initialized")
            Looper.prepare()
            Toast.makeText(applicationContext,"GoogleDrive : Google Drive Service not initialized",Toast.LENGTH_SHORT).show()

            return
        }
        val walPath = File(destinationFile.parent, "$dbName-wal")
        val shmPath = File(destinationFile.parent, "$dbName-shm")
        AppDatabase.getDatabase(applicationContext).close()
        // Delete existing database files
        if (destinationFile.exists()) destinationFile.delete()
        if (walPath.exists()) walPath.delete()
        if (shmPath.exists()) shmPath.delete()

        Thread {
            synchronized(lock) {
                Log.d("Thread", "Inner thread running")
                try {
                    val outputStream = FileOutputStream(destinationFile)//destinationFile.outputStream()
                    googleDriveService2.files().get(fileId).executeMediaAndDownloadTo(outputStream)
                    outputStream.flush()
                    outputStream.close()
                    AppDatabase.getDatabase(applicationContext)
                    Looper.prepare()
                    Toast.makeText(applicationContext,"GoogleDrive : File downloaded successfully: ${destinationFile.absolutePath}",Toast.LENGTH_SHORT).show()

                    Log.d("GoogleDrive", "File downloaded successfully: ${destinationFile.absolutePath}")
                } catch (e: Exception) {
                    Log.e("GoogleDrive", "Error downloading file: ${e.message}")
                    Looper.prepare()
                    Toast.makeText(applicationContext,"GoogleDrive :Error downloading file: ${e.message}",Toast.LENGTH_SHORT).show()

                }
            }

        }.start()
    }
    var fileId: String = ""
    val lock = Object()
    var status: Boolean=false
    private fun restoreDatabaseFromDrive(googleDriveService2: Drive) {
        val dbName = "expense_manager_db" // Replace with your database name
        val localDbFile = getDbPath(dbName)

Thread{
    synchronized(lock) {
        Log.d("Thread", "Outer thread running")
        fileId = getFileIdByName(googleDriveService2,"expense_manager_db.db").toString()//searchFileInDrive(dbName,googleDriveService2)!!
        if (fileId != null) {

            downloadFileFromDrive(fileId, localDbFile,googleDriveService2,dbName)
            Looper.prepare()
            Toast.makeText(applicationContext,"GoogleDrive :Database restored to internal storage: ${localDbFile.absolutePath}",Toast.LENGTH_SHORT).show()
            status=true
            Log.d("GoogleDrive", "Database restored to internal storage: ${localDbFile.absolutePath}")

        } else {
            status=false
            Looper.prepare()
            Toast.makeText(applicationContext,"GoogleDrive : Database file not found in Drive",Toast.LENGTH_SHORT).show()

            Log.e("GoogleDrive", "Database file not found in Drive")
        }

    }


}.start()
//       // val fileId = searchFileInDrive(dbName)
//        Handler().postDelayed({
//
//        }, 2000)

        if (status)
        {
            val intent = getIntent()
            finish()
            startActivity(intent)
        }

    }
    private fun getDbPath(dbName: String): File {
        val DATABASE_NAME = "expense_manager_db"
        val PACKAGE_NAME = "com.example.expensemanager.app"
        val DATABASE_PATH = "/data/data/" + PACKAGE_NAME + "/databases/" + DATABASE_NAME

        val dbPath = File(applicationContext.filesDir, "databases/expense_manager_db").absolutePath
        Log.d("DatabasePath", "Database path: $dbPath")
        return File(DATABASE_PATH)
    }

}
