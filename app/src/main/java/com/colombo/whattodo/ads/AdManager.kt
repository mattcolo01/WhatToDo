package com.colombo.whattodo.ads

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdManager(private val context: Context) {
    private var interstitialAd: InterstitialAd? = null
    private val interstitialAdUnitId = "ca-app-pub-5545966595390113/6923248953"
    private val bannerAdUnitId = "ca-app-pub-5545966595390113/7784612902"

    init {
        MobileAds.initialize(context)
        loadInterstitialAd()
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(
            context,
            interstitialAdUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    fun showInterstitialAd(activity: Activity, onAdDismissed: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            interstitialAd?.let { ad ->
                ad.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        interstitialAd = null
                        loadInterstitialAd()
                        onAdDismissed()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                        interstitialAd = null
                        loadInterstitialAd()
                        onAdDismissed()
                    }
                }
                ad.show(activity)
            } ?: run {
                loadInterstitialAd()
                onAdDismissed()
            }
        }
    }

    @Composable
    fun BannerAd(
        onAdLoaded: () -> Unit = {},
    ) {
        AndroidView(
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = bannerAdUnitId
                    loadAd(AdRequest.Builder().build())
                    adListener = object : com.google.android.gms.ads.AdListener() {
                        override fun onAdLoaded() {
                            onAdLoaded()
                            super.onAdLoaded()
                        }
                    }
                }
            },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth()
        )
    }
} 