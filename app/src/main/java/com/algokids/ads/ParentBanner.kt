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

/** One labelled placement, only on the separate parent-information screen. */
@Composable fun ParentBanner(ads:WorkshopAds,language:AppLanguage) {
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
   val observer=androidx.lifecycle.LifecycleEventObserver { _,event ->
    if(event==androidx.lifecycle.Lifecycle.Event.ON_PAUSE)banner.pause()
    if(event==androidx.lifecycle.Lifecycle.Event.ON_RESUME)banner.resume()
   }
   owner?.lifecycle?.addObserver(observer)
   banner.adListener=object:AdListener() {override fun onAdLoaded() {loaded=true};override fun onAdFailedToLoad(error:LoadAdError) {loaded=false} }
   banner.loadAd(ads.request())
   onDispose {owner?.lifecycle?.removeObserver(observer);banner.destroy()}
  }
  Column {
   if(loaded)Text(label(language,"Reklam","Advertisement"))
   AndroidView(factory={banner},modifier=Modifier.fillMaxWidth().height(if(loaded) banner.adSize!!.height.dp else 0.dp).testTag("parent_banner"))
  }
 }
}
