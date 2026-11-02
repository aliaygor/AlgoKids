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

/** Only natural workshop-completion breaks can display ads. Learning progress and free worked examples never depend on ads. */
class WorkshopAds(private val activity:ComponentActivity,freshSession:Boolean) {
    private val prefs=activity.getSharedPreferences("algokids_progress",0)
    private val consent=UserMessagingPlatform.getConsentInformation(activity)
    private val retryHandler=android.os.Handler(android.os.Looper.getMainLooper())
    private var interstitialFailures=0
    private var rewardedFailures=0
    private var consentUpdating=false
    private var lastConsentAttempt:Long?=null
    private fun retry(rewarded:Boolean) {
        val failures=if(rewarded) rewardedFailures else interstitialFailures
        if(failures>3 || destroyed || disabled)return
        retryHandler.postDelayed({
            if(!destroyed && activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                if(rewarded)loadReward() else load()
            }
        },30_000L * failures.coerceAtLeast(1))
    }
    fun resume() {
        if(!initialized)start()
        if(sdkReady && !destroyed && !disabled) {
            if(ad==null && !loading)load()
            if(reward==null && !rewardLoading)loadReward()
        }
    }
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
        return prefs.getInt("ads_session",1)>1 && prefs.getInt("ads_shown_session",0)<AdPolicy.MAX_PER_SESSION &&
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
        if(disabled || destroyed || initialized || consentUpdating)return
        val now=SystemClock.elapsedRealtime()
        if(lastConsentAttempt?.let { now-it<30_000L }==true)return
        lastConsentAttempt=now
        consentUpdating=true
        // Configure all users as children, irrespective of the optional learning-age preference.
        MobileAds.setRequestConfiguration(RequestConfiguration.Builder()
            .setAgeRestrictedTreatment(AgeRestrictedTreatment.CHILD)
            .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
            .setPublisherPrivacyPersonalizationState(RequestConfiguration.PublisherPrivacyPersonalizationState.DISABLED)
            .build())
        val params=ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(true).build()
        consent.requestConsentInfoUpdate(activity,params,{
            if(!destroyed)UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                consentUpdating=false
                if(it!=null)android.util.Log.w("AlgoKidsAds","Consent form failed: ${it.errorCode}")
                initializeIfAllowed()
            }
        },{
            consentUpdating=false
            android.util.Log.w("AlgoKidsAds","Consent update failed: ${it.errorCode}")
            initializeIfAllowed()
        })
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
        if(reward!=null || !BuildConfig.REWARDED_ENABLED || !sdkReady || disabled || destroyed || rewardLoading || !canLoad())return
        rewardLoading=true
        RewardedAd.load(activity,BuildConfig.ADMOB_REWARDED_ID,request(),object:RewardedAdLoadCallback() {
            override fun onAdLoaded(value:RewardedAd) {rewardLoading=false;rewardedFailures=0;if(!destroyed) {reward=value;rewardLoadedAt=SystemClock.elapsedRealtime()} }
            override fun onAdFailedToLoad(error:LoadAdError) {android.util.Log.w("AlgoKidsAds","Rewarded load failed: ${error.domain}/${error.code}");rewardLoading=false;reward=null;rewardedFailures++;retry(true)}
        })
    }
    /** Explicit opt-in; only the SDK's earned callback grants the selected reward, never learning progress. */
    fun rewardTheme(stopAudio:()->Unit,onFinished:(Boolean)->Unit) = showReward(stopAudio,{
        prefs.edit().putBoolean("cosmetic_sunset_unlocked",true).apply()
    },onFinished)
    fun rewardSolution(stopAudio:()->Unit,onFinished:(Boolean)->Unit) = showReward(stopAudio,{
        com.algokids.data.SolutionAllowance(prefs).reward()
    },onFinished)
    private fun showReward(stopAudio:()->Unit,onEarned:()->Unit,onFinished:(Boolean)->Unit) {
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
        try {available.show(activity) { if(!earned) {earned=true;onEarned()} }} catch(_:Exception) {finish()}
    }
    private fun load() {
        if(ad!=null || disabled || destroyed || loading || !sdkReady || !canLoad())return
        loading=true
        val extra=Bundle().apply { putString("npa","1") }
        val request=AdRequest.Builder().addNetworkExtrasBundle(com.google.ads.mediation.admob.AdMobAdapter::class.java,extra).build()
        InterstitialAd.load(activity,BuildConfig.ADMOB_INTERSTITIAL_ID,request,object:InterstitialAdLoadCallback() {
            override fun onAdLoaded(value:InterstitialAd) { loading=false;interstitialFailures=0;if(!destroyed) { ad=value;loadedAt=SystemClock.elapsedRealtime() } }
            override fun onAdFailedToLoad(error:LoadAdError) { android.util.Log.w("AlgoKidsAds","Interstitial load failed: ${error.domain}/${error.code}");loading=false;ad=null;interstitialFailures++;retry(false) }
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
    fun destroy() { destroyed=true;retryHandler.removeCallbacksAndMessages(null);ad=null;reward=null;sdkReady=false }
}
