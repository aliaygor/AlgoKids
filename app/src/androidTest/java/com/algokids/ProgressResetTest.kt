package com.algokids

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.algokids.data.*
import com.algokids.game.model.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProgressResetTest {
 @get:Rule val compose=createAndroidComposeRule<MainActivity>()
 private fun tap(tag:String) {
  val node=compose.onNodeWithTag(tag)
  if(tag !in setOf("confirm_reset_learning","foundation_finished"))node.performScrollTo()
  node.performClick()
 }
 @Test fun resetBothLanguagesThenCompleteFoundationAndUnlockNextSet() {
  val p=compose.activity.getSharedPreferences("algokids_progress",0)
  val edit=p.edit().putBoolean("ads_disabled_for_tests",true).putInt("child_age",8).putInt("reminder_days",2)
   .putInt("bonus_points",80).putBoolean("route_done_0",true).putInt("learning_NUMBERS_EN",100)
  ChallengeCatalog.all.forEach { c -> listOf("TR","EN").forEach { lang ->
   edit.putBoolean("${Curriculum.progressKey(c.id,lang)}_done",true)
   c.rounds.indices.forEach { edit.putInt(LearningPath.key(c.id,lang,it),100) }
  } }
  edit.commit();compose.activityRule.scenario.recreate()
  if(compose.onAllNodesWithText("EN / TR").fetchSemanticsNodes().isNotEmpty())compose.onNodeWithText("EN / TR").performClick()
  compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Set 1 · Tamamlandı"))
  compose.onNodeWithText("Set 1 · Tamamlandı").assertExists()
  tap("reset_learning");tap("confirm_reset_learning")
  compose.onNodeWithText("Set 1 · Açık").assertExists()
  compose.onNodeWithText("Set 2 · Kilitli").assertExists()
  listOf("TR","EN").forEach { lang ->
   assertEquals(1,LearningPath.level(p,lang))
   LearningPath.order.drop(1).forEach { assertFalse(LearningPath.unlocked(p,it,lang)) }
  }
  assertEquals(0,p.getInt("bonus_points",0));assertFalse(p.contains("route_done_0"))
  assertFalse(p.contains("learning_NUMBERS_EN"));assertEquals(8,p.getInt("child_age",0));assertEquals(2,p.getInt("reminder_days",0))
  compose.onNodeWithText("Görsel Algı").performClick()
  compose.onNodeWithText("Gölgeden nesneye").performScrollTo().performClick()
  ContentRepository(compose.activity).getContentByCategory(GameCategory.VISUAL).forEach {
   tap("foundation_choice_${it.answer}");tap("foundation_next")
  }
  compose.onNodeWithText("Temel alıştırma tamamlandı!").assertExists()
  tap("foundation_finished")
  compose.onNodeWithText("✓ Temel alıştırma tamamlandı").assertExists()
  assertTrue(ProgressStore(compose.activity).foundationCompleted(GameCategory.VISUAL))
  assertFalse(LearningPath.unlocked(p,GameCategory.ATTENTION,"TR"))
  ChallengeCatalog.forCategory(GameCategory.VISUAL).forEach { c ->
   compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(c.titleTr))
   compose.onNodeWithText(c.titleTr).performClick()
   c.rounds.forEach { r ->
    r.answer.forEach { token -> tap(if(c.mode==ChallengeMode.GRID) "cell_$token" else "choice_$token") }
    tap("check");tap("check")
   }
   tap("workshop_finished")
  }
  compose.onNodeWithText("‹ Ana sayfa").performScrollTo().performClick()
  compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Set 1 · Tamamlandı"))
  compose.onNodeWithText("Set 1 · Tamamlandı").assertExists()
  compose.onNodeWithText("Set 2 · Açık").assertExists()
  assertTrue(LearningPath.unlocked(p,GameCategory.ATTENTION,"TR"))
  compose.activityRule.scenario.recreate()
  assertTrue(ProgressStore(compose.activity).foundationCompleted(GameCategory.VISUAL))
  tap("reset_learning");tap("confirm_reset_learning");compose.activityRule.scenario.recreate()
  assertFalse(ProgressStore(compose.activity).foundationCompleted(GameCategory.VISUAL))
  assertFalse(LearningPath.unlocked(p,GameCategory.ATTENTION,"TR"))
 }
}
