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
import android.util.Log

enum class RewardAvailability { DISABLED, LOADING, READY, RETRY, COOLDOWN, SESSION_LIMIT }

/** Only natural workshop-completion breaks can display ads. Learning progress and free worked examples never depend on ads. */
class WorkshopAds(private val activity:ComponentActivity,freshSession:Boolean) {
    private val tag="AlgoKidsAds"
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
            val now=SystemClock.elapsedRealtime()
            if(ad!=null && now-loadedAt>=3_600_000L) ad=null
            if(reward!=null && now-rewardLoadedAt>=3_600_000L) reward=null
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
        // Rewarded ads are explicitly requested by the user, so they may be available in the first app session.
        // Keep the per-session cap and cooldown to avoid repeated prompts or accidental overexposure.
        return prefs.getInt("ads_shown_session",0)<AdPolicy.MAX_PER_SESSION &&
            (last==0L || now<last || now-last>=AdPolicy.GAP)
    }
    val rewardReady get()=reward!=null && SystemClock.elapsedRealtime()-rewardLoadedAt<3_600_000L && !showing && !disabled && !destroyed && canLoad() && rewardEligibleNow()
    val rewardAvailability:RewardAvailability get() {
        if(!BuildConfig.REWARDED_ENABLED || disabled || destroyed)return RewardAvailability.DISABLED
        if(consentUpdating)return RewardAvailability.LOADING
        if(!sdkReady && !canLoad())return RewardAvailability.RETRY
        if(!sdkReady || rewardLoading)return RewardAvailability.LOADING
        if(!canLoad())return RewardAvailability.DISABLED
        if(prefs.getInt("ads_shown_session",0)>=AdPolicy.MAX_PER_SESSION)return RewardAvailability.SESSION_LIMIT
        val now=SystemClock.elapsedRealtime()
        val lastShown=prefs.getLong("ads_last_shown",0)
        if(lastShown>0 && now>=lastShown && now-lastShown<AdPolicy.GAP)return RewardAvailability.COOLDOWN
        if(reward!=null) {
            if(now-rewardLoadedAt>=3_600_000L)return RewardAvailability.RETRY
            if(!rewardEligibleNow())return RewardAvailability.COOLDOWN
            return RewardAvailability.READY
        }
        return if(rewardedFailures>0)RewardAvailability.RETRY else RewardAvailability.LOADING
    }
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
        if(disabled || destroyed || initialized || consentUpdating) {
            Log.i(tag,"Ad startup skipped disabled=$disabled destroyed=$destroyed initialized=$initialized consentUpdating=$consentUpdating")
            return
        }
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
    }
    private fun initializeIfAllowed() {
        if(disabled || destroyed || initialized) return
        if(!canLoad()) {
            Log.i(tag,"Ad SDK initialization deferred: UMP does not currently allow ad requests")
            return
        }
        initialized=true
        Log.i(tag,"Initializing Google Mobile Ads debug=${BuildConfig.DEBUG} rewarded=${BuildConfig.REWARDED_ENABLED} banner=${BuildConfig.BANNER_ENABLED}")
        Thread {
            MobileAds.initialize(activity.applicationContext) {
                // This SDK setting is only valid after initialization and before the first ad request.
                MobileAds.putPublisherFirstPartyIdEnabled(false)
                activity.runOnUiThread {
                    if(!destroyed) {
                        sdkReady=true
                        Log.i(tag,"Google Mobile Ads initialized; requesting enabled formats")
                        load()
                        loadReward()
                    }
                }
            }
        }.start()
    }
    private fun loadReward() {
        if(reward!=null || !BuildConfig.REWARDED_ENABLED || !sdkReady || disabled || destroyed || rewardLoading || !canLoad())return
        rewardLoading=true
        RewardedAd.load(activity,BuildConfig.ADMOB_REWARDED_ID,request(),object:RewardedAdLoadCallback() {
            override fun onAdLoaded(value:RewardedAd) {
                rewardLoading=false;rewardedFailures=0
                if(!destroyed) {reward=value;rewardLoadedAt=SystemClock.elapsedRealtime();AdDiagnostics.loaded("Rewarded",value.responseInfo)}
            }
            override fun onAdFailedToLoad(error:LoadAdError) {
                AdDiagnostics.loadFailed("Rewarded",error)
                rewardLoading=false;reward=null;rewardedFailures++;retry(true)
            }
        })
    }
    fun retryReward() {
        if(disabled || destroyed || !BuildConfig.REWARDED_ENABLED || rewardLoading ||
            prefs.getInt("ads_shown_session",0)>=AdPolicy.MAX_PER_SESSION)return
        val lastShown=prefs.getLong("ads_last_shown",0)
        val now=SystemClock.elapsedRealtime()
        if(lastShown>0 && now>=lastShown && now-lastShown<AdPolicy.GAP)return
        if(reward!=null && now-rewardLoadedAt<3_600_000L)return
        reward=null
        rewardedFailures=0
        Log.i(tag,"Rewarded retry requested by user sdkReady=$sdkReady canRequestAds=${canLoad()}")
        if(!sdkReady) {
            if(!consentUpdating)lastConsentAttempt=null
            start()
        } else loadReward()
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
        if(showing || !rewardReady || available==null || now-rewardLoadedAt>=3_600_000 || !activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            Log.i(tag,"Rewarded show skipped ready=${available!=null} eligible=$rewardReady activityResumed=${activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)}")
            if(available==null || now-rewardLoadedAt>=3_600_000) {reward=null;loadReward()}
            onFinished(false);return
        }
        reward=null;showing=true;stopAudio()
        var earned=false;var finished=false
        fun finish() {if(finished)return;finished=true;showing=false;if(!destroyed) {onFinished(earned);loadReward()} }
        available.fullScreenContentCallback=object:FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Log.i(tag,"Rewarded onAdShowedFullScreenContent responseInfo=${available.responseInfo}")
                prefs.edit().putLong("ads_last_shown",SystemClock.elapsedRealtime()).putInt("ads_completed_since",0).putInt("ads_shown_session",prefs.getInt("ads_shown_session",0)+1).apply()
            }
            override fun onAdImpression()=AdDiagnostics.impression("Rewarded",available.responseInfo)
            override fun onAdDismissedFullScreenContent() {AdDiagnostics.dismissed("Rewarded",available.responseInfo);finish()}
            override fun onAdFailedToShowFullScreenContent(error:AdError) {AdDiagnostics.showFailed("Rewarded",error,available.responseInfo);finish()}
        }
        try {available.show(activity) { if(!earned) {earned=true;Log.i(tag,"Rewarded onUserEarnedReward amount=${it.amount} type=${it.type}");onEarned()} }} catch(error:Exception) {Log.e(tag,"Rewarded show threw; responseInfo=${available.responseInfo}",error);finish()}
    }
    private fun load() {
        if(ad!=null || disabled || destroyed || loading || !sdkReady || !canLoad())return
        loading=true
        val extra=Bundle().apply { putString("npa","1") }
        val request=AdRequest.Builder().addNetworkExtrasBundle(com.google.ads.mediation.admob.AdMobAdapter::class.java,extra).build()
        InterstitialAd.load(activity,BuildConfig.ADMOB_INTERSTITIAL_ID,request,object:InterstitialAdLoadCallback() {
            override fun onAdLoaded(value:InterstitialAd) {
                loading=false;interstitialFailures=0
                if(!destroyed) {ad=value;loadedAt=SystemClock.elapsedRealtime();AdDiagnostics.loaded("Interstitial",value.responseInfo)}
            }
            override fun onAdFailedToLoad(error:LoadAdError) {
                AdDiagnostics.loadFailed("Interstitial",error)
                loading=false;ad=null;interstitialFailures++;retry(false)
            }
        })
    }
    fun afterWorkshop(id:String,stopAudio:()->Unit,continueGame:()->Unit) {
        if(showing) {continueGame();return}
        if(disabled || destroyed) { continueGame();return }
        val ids=prefs.getStringSet("ads_completed_ids",emptySet()).orEmpty().toMutableSet()
        if(ids.add(id)) prefs.edit().putStringSet("ads_completed_ids",ids)
            .putInt("ads_completed_since",prefs.getInt("ads_completed_since",0)+1).apply()
        val now=SystemClock.elapsedRealtime()
        val last=prefs.getLong("ads_last_shown",0)
        val ready=AdPolicy.eligible(prefs.getInt("ads_completed_since",0),
            now-prefs.getLong("ads_session_start",now),if(last==0L || now<last)Long.MAX_VALUE else now-last,prefs.getInt("ads_shown_session",0))
        val available=ad
        if(!ready || available==null || now-loadedAt>=3_600_000 || !activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            Log.i(tag,"Interstitial show skipped eligible=$ready ready=${available!=null && now-loadedAt<3_600_000} activityResumed=${activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)}")
            if(available==null || now-loadedAt>=3_600_000) {ad=null;load()}
            continueGame();return
        }
        ad=null;showing=true;stopAudio()
        var continued=false
        fun finish() { if(continued)return;continued=true;showing=false;if(!destroyed) { continueGame();load() } }
        available.fullScreenContentCallback=object:FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Log.i(tag,"Interstitial onAdShowedFullScreenContent responseInfo=${available.responseInfo}")
                prefs.edit().putLong("ads_last_shown",SystemClock.elapsedRealtime()).putInt("ads_completed_since",0)
                    .putInt("ads_shown_session",prefs.getInt("ads_shown_session",0)+1).apply()
            }
            override fun onAdImpression()=AdDiagnostics.impression("Interstitial",available.responseInfo)
            override fun onAdDismissedFullScreenContent() {AdDiagnostics.dismissed("Interstitial",available.responseInfo);finish()}
            override fun onAdFailedToShowFullScreenContent(error:AdError) {AdDiagnostics.showFailed("Interstitial",error,available.responseInfo);finish()}
        }
        try {available.show(activity)} catch(error:Exception) {Log.e(tag,"Interstitial show threw; responseInfo=${available.responseInfo}",error);finish()}
    }
    fun privacyRequired()=consent.privacyOptionsRequirementStatus==ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    fun privacyOptions() { if(privacyRequired())UserMessagingPlatform.showPrivacyOptionsForm(activity) { ad=null;reward=null;load();loadReward() } }
    fun destroy() { destroyed=true;retryHandler.removeCallbacksAndMessages(null);ad=null;reward=null;sdkReady=false }
}
