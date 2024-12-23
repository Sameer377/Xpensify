package com.example.expensemanager.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.expensemanager.app.database.AppDatabase
import com.example.expensemanager.app.database.Category
import com.example.expensemanager.app.dialogs.TransactionBottomSheet
import com.example.expensemanager.app.fragments.Categories
import com.example.expensemanager.app.fragments.Transactions
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.RequestConfiguration
import kotlin.math.log









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
        fetchAndDisplayCategories()
    }

    private fun fetchAndDisplayCategories() {
        // Launch a coroutine on the IO thread to fetch data
        CoroutineScope(Dispatchers.IO).launch {
            val categoryDao = AppDatabase.getDatabase(baseContext ).categoryDao()
            val categories = categoryDao.getAllCategories()

            // Prepare the data to show in the Toast
            val categoryNames = categories.joinToString { it.name }

            // Switch to the main thread to show the Toast
            withContext(Dispatchers.Main) {
                Toast.makeText(baseContext, "Categories: $categoryNames", Toast.LENGTH_LONG).show()
            }
        }
    }


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

    /*public fun showAds() {
        val adRequest = AdRequest.Builder().build()
        var mInterstitialAd: InterstitialAd? = null

        InterstitialAd.load(
            this,
            "ca-app-pub-9253311157837179/1114277327",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    println("Ad successfully loaded!")

                    mInterstitialAd?.show(this@DashBoard)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    println("Ad failed to load: ${error.message}")
                }
            }
        )

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                println("Ad dismissed.")
                mInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                println("Ad failed to show: ${error.message}")
                mInterstitialAd = null
            }
        }
    }*/

    private fun getRewardedCoin(totalRewardedAmount: Int){
        Toast.makeText(this@DashBoard,"Total Rewarded Coins: $totalRewardedAmount Coins",Toast.LENGTH_LONG).show()
    }

    override fun onBottomSheetClosed() {
        changeToTransactions()
        Toast.makeText(this, "BottomSheet Closed", Toast.LENGTH_SHORT).show()
        // Add your logic here
    }


}
