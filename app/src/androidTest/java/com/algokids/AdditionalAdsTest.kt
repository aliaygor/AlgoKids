package com.algokids

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdditionalAdsTest {
 @get:Rule val compose=createAndroidComposeRule<MainActivity>()
 @Test fun optionalRewardUnlocksOnlyThemeAndParentBannerLoads() {
  val p=compose.activity.getSharedPreferences("algokids_progress",0)
  p.edit().putBoolean("ads_disabled_for_tests",false).putInt("ads_session",3)
   .putInt("ads_shown_session",0).putLong("ads_last_shown",0)
   .remove("cosmetic_sunset_unlocked").putString("cosmetic_theme","ocean").commit()
  val scores=p.all.filterKeys {"_exam_" in it}
  compose.activityRule.scenario.recreate()
  if(compose.onAllNodesWithText("EN / TR").fetchSemanticsNodes().isNotEmpty())compose.onNodeWithText("EN / TR").performClick()
  compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasTestTag("appearance_options"))
  compose.onNodeWithTag("appearance_options").performClick()
  compose.onNodeWithText("Okyanus · Ücretsiz ✓").assertExists()
  compose.waitUntil(60000) {compose.onAllNodesWithTag("reward_theme").fetchSemanticsNodes().any { !it.config.contains(androidx.compose.ui.semantics.SemanticsProperties.Disabled) }}
  compose.onNodeWithTag("reward_theme").performScrollTo().performClick()
  compose.onNodeWithText("Vazgeç").performClick()
  assertFalse(p.getBoolean("cosmetic_sunset_unlocked",false))
  compose.onNodeWithTag("reward_theme").performScrollTo().performClick()
  compose.onNodeWithTag("confirm_reward").performClick()
  compose.waitUntil(70000) {p.getBoolean("cosmetic_sunset_unlocked",false)}
  InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("input keyevent KEYCODE_BACK").close()
  compose.waitUntil(15000) {compose.onAllNodesWithTag("theme_sunset").fetchSemanticsNodes().isNotEmpty()}
  compose.onNodeWithTag("theme_sunset").performScrollTo().performClick()
  assertEquals("sunset",p.getString("cosmetic_theme",""))
  assertEquals(scores,p.all.filterKeys {"_exam_" in it})
  compose.onNodeWithText("‹ Ana sayfa").performScrollTo().performClick()
  compose.onNodeWithContentDescription("Ebeveyn bilgisi").performScrollTo().performClick()
  compose.waitUntil(60000) {compose.onAllNodesWithText("Reklam").fetchSemanticsNodes().isNotEmpty()}
  compose.onNodeWithTag("parent_banner").performScrollTo().assertExists()
  assertTrue(compose.onNodeWithTag("parent_banner").fetchSemanticsNode().boundsInRoot.height>0)
  compose.onNodeWithText("‹ Ana sayfa").performScrollTo().performClick()
  compose.onAllNodesWithTag("parent_banner").assertCountEquals(0)
  p.edit().putBoolean("ads_disabled_for_tests",true).putString("cosmetic_theme","ocean").commit()
 }
}
