package com.algokids.ads

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import com.algokids.BuildConfig
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.ump.*
import com.google.android.gms.ads.rewarded.*
import androidx.compose.runtime.*

/** Only natural workshop-completion breaks can display ads. No score or solution depends on ads. */
class WorkshopAds(private val activity:ComponentActivity,freshSession:Boolean) {
    private val prefs=activity.getSharedPreferences("algokids_progress",0)
    private val consent=UserMessagingPlatform.getConsentInformation(activity)
    private var ad:InterstitialAd?=null
    private var loading=false
    private var initialized=false
    private var showing=false
    private var destroyed=false
    private var loadedAt=0L
    private var sdkReady by mutableStateOf(false)
    private var reward:RewardedAd? by mutableStateOf(null)
    private var rewardLoading=false
    private var rewardLoadedAt=0L
    val bannerAllowed get()=BuildConfig.BANNER_ENABLED && sdkReady && !disabled && !destroyed && canLoad()
    private fun rewardEligibleNow():Boolean {
        val now=SystemClock.elapsedRealtime();val last=prefs.getLong("ads_last_shown",0)
        return prefs.getInt("ads_session",1)>1 && prefs.getInt("ads_shown_session",0)<2 &&
            (last==0L || now<last || now-last>=AdPolicy.GAP)
    }
    val rewardReady get()=reward!=null && !showing && !disabled && !destroyed && canLoad() && rewardEligibleNow()
    fun request():AdRequest=AdRequest.Builder().addNetworkExtrasBundle(com.google.ads.mediation.admob.AdMobAdapter::class.java,Bundle().apply { putString("npa","1") }).build()
    private val disabled get()=!BuildConfig.ADS_ENABLED || (BuildConfig.DEBUG && prefs.getBoolean("ads_disabled_for_tests",false))
    private fun canLoad()=BuildConfig.DEBUG || consent.canRequestAds()
    fun isReady()=ad!=null
    init {
        if(freshSession) prefs.edit().putInt("ads_session",prefs.getInt("ads_session",0)+1)
            .putLong("ads_session_start",SystemClock.elapsedRealtime()).putStringSet("ads_completed_ids",emptySet())
            .putInt("ads_completed_since",0).putInt("ads_shown_session",0).apply()
    }
    fun start() {
        if(disabled || destroyed || prefs.getInt("ads_session",1)<=1)return
        // Configure all users as children, irrespective of the optional learning-age preference.
        MobileAds.setRequestConfiguration(RequestConfiguration.Builder()
            .setAgeRestrictedTreatment(AgeRestrictedTreatment.CHILD)
            .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
            .setPublisherPrivacyPersonalizationState(RequestConfiguration.PublisherPrivacyPersonalizationState.DISABLED)
            .build())
        val params=ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(true).build()
        consent.requestConsentInfoUpdate(activity,params,{
            if(!destroyed)UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { initializeIfAllowed() }
        },{ initializeIfAllowed() })
        initializeIfAllowed()
    }
    private fun initializeIfAllowed() {
        if(disabled || destroyed || initialized || !canLoad())return
        initialized=true
        Thread {
            MobileAds.initialize(activity.applicationContext) {
                MobileAds.putPublisherFirstPartyIdEnabled(false)
                activity.runOnUiThread { if(!destroyed) {sdkReady=true;load();loadReward()} }
            }
        }.start()
    }
    private fun loadReward() {
        if(!BuildConfig.REWARDED_ENABLED || !sdkReady || disabled || destroyed || rewardLoading || !canLoad())return
        rewardLoading=true
        RewardedAd.load(activity,BuildConfig.ADMOB_REWARDED_ID,request(),object:RewardedAdLoadCallback() {
            override fun onAdLoaded(value:RewardedAd) {rewardLoading=false;if(!destroyed) {reward=value;rewardLoadedAt=SystemClock.elapsedRealtime()} }
            override fun onAdFailedToLoad(error:LoadAdError) {rewardLoading=false;reward=null}
        })
    }
    /** Explicit opt-in; only the SDK's earned callback unlocks a cosmetic, never learning progress. */
    fun rewardTheme(stopAudio:()->Unit,onFinished:(Boolean)->Unit) {
        val now=SystemClock.elapsedRealtime()
        val available=reward
        if(!rewardReady || available==null || now-rewardLoadedAt>=3_600_000 || !activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            if(available==null || now-rewardLoadedAt>=3_600_000) {reward=null;loadReward()}
            onFinished(false);return
        }
        reward=null;showing=true;stopAudio()
        var earned=false;var finished=false
        fun finish() {if(finished)return;finished=true;showing=false;if(!destroyed) {onFinished(earned);loadReward()} }
        available.fullScreenContentCallback=object:FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {prefs.edit().putLong("ads_last_shown",SystemClock.elapsedRealtime()).putInt("ads_completed_since",0).putInt("ads_shown_session",prefs.getInt("ads_shown_session",0)+1).apply()}
            override fun onAdDismissedFullScreenContent()=finish()
            override fun onAdFailedToShowFullScreenContent(error:AdError)=finish()
        }
        try {available.show(activity) { if(!earned) {earned=true;prefs.edit().putBoolean("cosmetic_sunset_unlocked",true).apply()} }} catch(_:Exception) {finish()}
    }
    private fun load() {
        if(disabled || destroyed || loading || !initialized || !canLoad())return
        loading=true
        val extra=Bundle().apply { putString("npa","1") }
        val request=AdRequest.Builder().addNetworkExtrasBundle(com.google.ads.mediation.admob.AdMobAdapter::class.java,extra).build()
        InterstitialAd.load(activity,BuildConfig.ADMOB_INTERSTITIAL_ID,request,object:InterstitialAdLoadCallback() {
            override fun onAdLoaded(value:InterstitialAd) { loading=false;if(!destroyed) { ad=value;loadedAt=SystemClock.elapsedRealtime() } }
            override fun onAdFailedToLoad(error:LoadAdError) { loading=false;ad=null }
        })
    }
    fun afterWorkshop(id:String,stopAudio:()->Unit,continueGame:()->Unit) {
        if(showing)return
        if(disabled || destroyed) { continueGame();return }
        val ids=prefs.getStringSet("ads_completed_ids",emptySet()).orEmpty().toMutableSet()
        if(ids.add(id)) prefs.edit().putStringSet("ads_completed_ids",ids)
            .putInt("ads_completed_since",prefs.getInt("ads_completed_since",0)+1).apply()
        val now=SystemClock.elapsedRealtime()
        val last=prefs.getLong("ads_last_shown",0)
        val ready=AdPolicy.eligible(prefs.getInt("ads_session",1)<=1,prefs.getInt("ads_completed_since",0),
            now-prefs.getLong("ads_session_start",now),if(last==0L || now<last)Long.MAX_VALUE else now-last,prefs.getInt("ads_shown_session",0))
        val available=ad
        if(!ready || available==null || now-loadedAt>=3_600_000 || !activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            if(available==null || now-loadedAt>=3_600_000) {ad=null;load()}
            continueGame();return
        }
        ad=null;showing=true;stopAudio()
        var continued=false
        fun finish() { if(continued)return;continued=true;showing=false;if(!destroyed) { continueGame();load() } }
        available.fullScreenContentCallback=object:FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                prefs.edit().putLong("ads_last_shown",SystemClock.elapsedRealtime()).putInt("ads_completed_since",0)
                    .putInt("ads_shown_session",prefs.getInt("ads_shown_session",0)+1).apply()
            }
            override fun onAdDismissedFullScreenContent()=finish()
            override fun onAdFailedToShowFullScreenContent(error:AdError)=finish()
        }
        try { available.show(activity) } catch(_:Exception) { finish() }
    }
    fun privacyRequired()=consent.privacyOptionsRequirementStatus==ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    fun privacyOptions() { if(privacyRequired())UserMessagingPlatform.showPrivacyOptionsForm(activity) { ad=null;reward=null;load();loadReward() } }
    fun destroy() { destroyed=true;ad=null;reward=null;sdkReady=false }
}
