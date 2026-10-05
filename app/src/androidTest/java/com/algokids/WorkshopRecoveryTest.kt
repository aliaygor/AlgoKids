package com.algokids
import android.content.pm.ActivityInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.algokids.game.model.GameCategory
import com.algokids.ui.screens.AppLanguage
import com.algokids.ui.screens.categoryTitle
import org.junit.Rule
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class)
class WorkshopRecoveryTest {
 @get:Rule val compose=createAndroidComposeRule<MainActivity>()
 @Before fun useTurkishForEachTest() {
  // Isolate recovery checks from the separate learning-path lock tests.
  val p=compose.activity.getSharedPreferences("algokids_progress",0)
  val edit=p.edit()
  listOf("TR","EN").forEach { lang -> com.algokids.game.model.ChallengeCatalog.all.forEach { c ->
   edit.putBoolean("${com.algokids.data.Curriculum.progressKey(c.id,lang)}_done",true)
   c.rounds.indices.forEach { edit.putInt(com.algokids.data.LearningPath.key(c.id,lang,it),100) }
  } }
  edit.commit()
  compose.activityRule.scenario.recreate()
  compose.onNode(hasScrollToIndexAction()).performScrollToIndex(0)
  if(compose.onAllNodesWithText("EN / TR").fetchSemanticsNodes().isNotEmpty()) {
   compose.onNodeWithText("EN / TR").performClick()
   compose.waitForIdle()
  }
 }
 private fun tap(tag:String) { compose.onNodeWithTag(tag).performScrollTo().performClick() }
 private fun category(type:GameCategory) {
  val title=categoryTitle(type,AppLanguage.TR)
  compose.onNode(hasScrollToIndexAction()).performScrollToIndex(0)
   compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(title));compose.onNodeWithText(title).performClick()
 }
 @Test fun wrongAnswerUndoMuteAndLandscapePreserveLearning() {
  category(GameCategory.MEMORY)
  compose.onNodeWithText("Yer hafızası").performScrollTo().performClick()
  if(compose.onAllNodesWithText("Sesi kapat").fetchSemanticsNodes().isNotEmpty()) compose.onNodeWithText("Sesi kapat").performClick()
  compose.onNodeWithText("Sesi aç").assertExists()
  compose.onNodeWithTag("check").assertIsNotEnabled()
  tap("memory_toggle")
  tap("cell_1");tap("check")
  compose.onNodeWithTag("feedback").assertTextContains("Henüz olmadı.",substring=true)
  tap("undo")
  compose.onNodeWithTag("check").assertIsNotEnabled()
  listOf(0,4,8).forEach { tap("cell_$it") }
  compose.activityRule.scenario.onActivity { it.requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE }
  compose.waitForIdle()
  tap("check")
  compose.onNodeWithTag("feedback").assertTextContains("Çözdün!",substring=true)
  compose.activityRule.scenario.onActivity { it.requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT }
  compose.waitForIdle()
  compose.onNodeWithText("Sesi aç").performScrollTo().performClick()
  compose.onNodeWithText("‹ Atölyeler").performScrollTo().performClick()
  compose.onNodeWithText("‹ Ana sayfa").performScrollTo().performClick()
  compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("TR / EN"))
  compose.onNodeWithText("TR / EN").performClick()
  compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Algorithm"))
  compose.onNodeWithText("Algorithm").performClick()
  compose.onNodeWithText("Loop laboratory").performScrollTo().performClick()
  tap("choice_→ × 2");tap("choice_↑ × 2");tap("check")
  compose.onNodeWithTag("feedback").assertTextContains("Solved!",substring=true)
  compose.onNodeWithText("‹ Workshops").performScrollTo().performClick()
  compose.onNodeWithText("‹ Home").performScrollTo().performClick()
  compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("EN / TR"))
  compose.onNodeWithText("EN / TR").performClick()
  compose.waitForIdle()
 }
 @Test fun everyStoryCanBeReadToTheLastPage() {
  compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Hikâyeler"))
  compose.onNodeWithText("Hikâyeler").performClick()
  listOf("Küçük Karınca","Cesur Tavşan","Uzay Yolculuğu","Kayıp Renkler","Robotun Planı","Deniz Feneri","Minik Mimar","Sessiz Kütüphane","Yağmurdan Sonra","Kaybolan Melodi").forEach { title ->
   compose.onNode(hasScrollToIndexAction()).performScrollToIndex(0)
   compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(title))
   compose.onNodeWithText(title).performClick()
   var page=0
   while(compose.onAllNodesWithText("İleri").fetchSemanticsNodes().isNotEmpty()) {
    compose.onNodeWithText("İleri").performClick();page++;check(page<20)
    if(title=="Küçük Karınca" && page==1) { compose.activityRule.scenario.recreate();compose.onNodeWithText("2 / 6").assertExists()
     compose.activityRule.scenario.onActivity { it.requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE };compose.waitForIdle()
     compose.onNodeWithText(com.algokids.data.StoryCatalog.all.first { it.id=="s1" }.pages[1]).assertIsDisplayed()
     compose.activityRule.scenario.onActivity { it.requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT };compose.waitForIdle() }
   }
   compose.onNodeWithText("Bitti").performClick()
  }
 }
}
