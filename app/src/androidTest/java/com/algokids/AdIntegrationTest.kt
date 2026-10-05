package com.algokids

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.algokids.ads.WorkshopAds
import com.google.android.gms.ads.AgeRestrictedTreatment
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AdIntegrationTest {
 @get:Rule val compose=createAndroidComposeRule<MainActivity>()
 @Test fun googleTestAdLoadsAtCompletionAndReturnsWithoutChangingLearningScore() {
  val p=compose.activity.getSharedPreferences("algokids_progress",0)
  val learningScores=p.all.filterKeys { "_exam_" in it }
  p.edit().putBoolean("ads_disabled_for_tests",false).putInt("ads_session",2)
   .putLong("ads_session_start",android.os.SystemClock.elapsedRealtime()-200000)
   .putInt("ads_completed_since",0).putInt("ads_shown_session",0).putLong("ads_last_shown",0)
   .putStringSet("ads_completed_ids",emptySet()).commit()
  val ads=WorkshopAds(compose.activity,false)
  compose.runOnIdle { ads.start() }
  try {
   compose.waitUntil(45000) { ads.isReady() }
   val config=MobileAds.getRequestConfiguration()
   assertEquals(AgeRestrictedTreatment.CHILD,config.ageRestrictedTreatment)
   assertEquals(RequestConfiguration.MAX_AD_CONTENT_RATING_G,config.maxAdContentRating)
   var returned=false
   compose.runOnIdle { ads.afterWorkshop("test_one",{}, {returned=true}) }
   assertTrue(returned)
   returned=false
   compose.runOnIdle { ads.afterWorkshop("test_two",{}, {returned=true}) }
   compose.waitUntil(10000) { p.getInt("ads_shown_session",0)==1 }
   val i=InstrumentationRegistry.getInstrumentation()
   val dir=File(i.targetContext.filesDir,"screenshots").apply {mkdirs()}
   val image=i.uiAutomation.takeScreenshot()
   File(dir,"ad-test.png").outputStream().use { image.compress(Bitmap.CompressFormat.PNG,100,it) };image.recycle()
   // Closing the SDK test creative, never clicking the advertiser.
   android.os.SystemClock.sleep(20000)
   i.uiAutomation.executeShellCommand("input keyevent KEYCODE_BACK").close()
   compose.waitUntil(10000) { returned }
   assertEquals(1,p.getInt("ads_shown_session",0))
   assertEquals(learningScores,p.all.filterKeys { "_exam_" in it })
  } finally {
   ads.destroy();p.edit().putBoolean("ads_disabled_for_tests",true).commit()
  }
 }
}
