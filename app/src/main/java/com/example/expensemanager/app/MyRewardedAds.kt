package com.example.expensemanager.app


import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class MyRewardedAds(private val activity: Activity) {

    private var rewardedAd : RewardedAd? = null

    fun loadRewardedAds(adUnitIdL : Int){
        val adRequest = AdRequest.Builder().build()


        RewardedAd.load(
            activity,
            activity.getString(adUnitIdL),
            adRequest,
            object : RewardedAdLoadCallback(){
                override fun onAdLoaded(mRewardedAd: RewardedAd) {
                    rewardedAd = mRewardedAd
                    Toast.makeText(activity,"Ads is Loaded1",Toast.LENGTH_LONG).show()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    rewardedAd = null
                    Log.v("ads_failed", "Ads Failed : "+p0.toString())
                }
            }
        )
    }

    fun showRewardedAds(adUnitIdL: Int,afterCodeRewardCoin : (RewardItem) -> Unit){
        if (rewardedAd != null){
            rewardedAd!!.fullScreenContentCallback = object : FullScreenContentCallback(){
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewardedAds(adUnitIdL)
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    rewardedAd = null
                }
            }
            rewardedAd!!.show(activity){
                afterCodeRewardCoin(it)
            }
        }else{
            Toast.makeText(activity,"Ads is Loaded",Toast.LENGTH_LONG).show()
        }
    }
}