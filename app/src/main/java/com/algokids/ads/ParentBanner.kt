package com.algokids.ads

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import com.algokids.BuildConfig
import com.algokids.ui.screens.AppLanguage
import com.algokids.ui.screens.label
import com.google.android.gms.ads.*

/** One labelled placement per menu screen; never used inside gameplay. */
@Composable fun ParentBanner(ads:WorkshopAds,language:AppLanguage,placement:String="parent_banner") {
 if(!ads.bannerAllowed)return
 val context=LocalContext.current
 BoxWithConstraints(Modifier.fillMaxWidth()) {
  val width=maxWidth.value.toInt().coerceAtLeast(1)
  val banner=remember(width) { AdView(context).apply {
   adUnitId=BuildConfig.ADMOB_BANNER_ID
   setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context,width))
  } }
  var loaded by remember(banner) {mutableStateOf(false)}
  DisposableEffect(banner) {
   val owner=context as? androidx.lifecycle.LifecycleOwner
   val handler=android.os.Handler(android.os.Looper.getMainLooper())
   var active=true
   var isLoading=false
   var pendingRetry=false
   var retryCount=0
   val retryDelays=longArrayOf(60_000L,120_000L,240_000L)
   fun isResumed()=owner?.lifecycle?.currentState?.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)!=false
   var loadBanner:(()->Unit)?=null
   val observer=androidx.lifecycle.LifecycleEventObserver { _,event ->
    if(event==androidx.lifecycle.Lifecycle.Event.ON_PAUSE)banner.pause()
    if(event==androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
     banner.resume()
     if(pendingRetry)loadBanner?.invoke()
    }
   }
   owner?.lifecycle?.addObserver(observer)
   loadBanner={
    if(active && !loaded && !isLoading) {
     if(!isResumed()) pendingRetry=true
     else {
      pendingRetry=false
      isLoading=true
      android.util.Log.i("AlgoKidsAds","Banner[$placement] load requested widthDp=$width")
      banner.loadAd(ads.request())
     }
    }
   }
   banner.adListener=object:AdListener() {
    override fun onAdLoaded() {
     isLoading=false;loaded=true;retryCount=0
     AdDiagnostics.loaded("Banner[$placement]",banner.responseInfo)
    }
    override fun onAdImpression()=AdDiagnostics.impression("Banner[$placement]",banner.responseInfo)
    override fun onAdClicked() { android.util.Log.i("AlgoKidsAds","Banner[$placement] onAdClicked responseInfo=${banner.responseInfo}") }
    override fun onAdOpened() { android.util.Log.i("AlgoKidsAds","Banner[$placement] onAdOpened responseInfo=${banner.responseInfo}") }
    override fun onAdClosed() { android.util.Log.i("AlgoKidsAds","Banner[$placement] onAdClosed responseInfo=${banner.responseInfo}") }
    override fun onAdFailedToLoad(error:LoadAdError) {
     isLoading=false;loaded=false
     AdDiagnostics.loadFailed("Banner[$placement]",error)
     if(active && retryCount<retryDelays.size) {
      val delay=retryDelays[retryCount++]
      android.util.Log.i("AlgoKidsAds","Banner[$placement] retry $retryCount/${retryDelays.size} in ${delay}ms")
      handler.postDelayed({
       if(active) {
        if(isResumed())loadBanner() else pendingRetry=true
       }
      },delay)
     } else if(active) android.util.Log.w("AlgoKidsAds","Banner[$placement] retry limit reached; waiting for placement recreation")
    }
   }
   loadBanner?.invoke()
   onDispose {active=false;handler.removeCallbacksAndMessages(null);owner?.lifecycle?.removeObserver(observer);banner.adListener=object:AdListener() {};banner.destroy()}
  }
  Column(Modifier.padding(top=12.dp)) {
   if(loaded)Text(label(language,"Reklam","Advertisement"))
   AndroidView(factory={banner},modifier=Modifier.fillMaxWidth().height(if(loaded) banner.adSize!!.height.dp else 0.dp).testTag(placement))
  }
 }
}
