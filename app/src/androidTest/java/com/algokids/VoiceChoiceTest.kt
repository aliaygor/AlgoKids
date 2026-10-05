package com.algokids
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class)
class VoiceChoiceTest {
 @get:Rule val compose=createAndroidComposeRule<MainActivity>()
 @Test fun narratorCanBePreviewedAndSelectionSurvivesRecreation() {
  val p=compose.activity.getSharedPreferences("algokids_progress",0)
  p.edit().putBoolean("ads_disabled_for_tests",true).commit()
  if(compose.onAllNodesWithText("EN / TR").fetchSemanticsNodes().isNotEmpty())compose.onNodeWithText("EN / TR").performClick()
  compose.onNodeWithContentDescription("Ebeveyn bilgisi").performClick()
  compose.waitUntil(15000) {compose.onAllNodesWithTag("voice_choice_1").fetchSemanticsNodes().isNotEmpty()}
  compose.onNodeWithTag("voice_choice_1").performScrollTo().performClick()
  val selected=p.getString("narrator_voice_TR",null)
  assertNotNull(selected);assertFalse(selected!!.contains("language"))
  compose.activityRule.scenario.recreate()
  compose.waitUntil(15000) {compose.onAllNodesWithTag("voice_choice_1").fetchSemanticsNodes().isNotEmpty()}
  compose.onNodeWithTag("voice_choice_1").performScrollTo().assertTextContains("✓",substring=true)
  assertEquals(selected,p.getString("narrator_voice_TR",null))
  compose.onNodeWithText("‹ Ana sayfa").performScrollTo().performClick()
 }
}
