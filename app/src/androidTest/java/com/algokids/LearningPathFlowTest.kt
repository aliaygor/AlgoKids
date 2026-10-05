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
import java.util.Calendar

@RunWith(AndroidJUnit4::class)
class LearningPathFlowTest {
 @get:Rule val compose=createAndroidComposeRule<MainActivity>()
 private fun tap(tag:String) { compose.onNodeWithTag(tag).performScrollTo().performClick() }
 private fun home() { compose.onNodeWithText("‹ Ana sayfa").performScrollTo().performClick() }
 @Test fun setUnlocksOnlyItsSuccessorAndWrongAnswersPersist() {
  val p=compose.activity.getSharedPreferences("algokids_progress",0)
  val edit=p.edit().putBoolean("ads_disabled_for_tests",true).putString("language","TR").putInt("child_age",6).putInt("bonus_points",0)
  ChallengeCatalog.all.forEach { c ->
   val key=Curriculum.progressKey(c.id)
   edit.remove("${key}_done").remove("${key}_round")
   c.rounds.indices.forEach { edit.remove(LearningPath.key(c.id,"TR",it)).remove("${key}_attempts_$it") }
  }
  edit.commit();compose.activityRule.scenario.recreate()
  assertFalse(LearningPath.unlocked(p,GameCategory.ATTENTION,"TR"))
  compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Dikkat ve Odak"))
  compose.onNodeWithText("Dikkat ve Odak").assertIsNotEnabled()
  ChallengeCatalog.forCategory(GameCategory.VISUAL).forEachIndexed { workshopIndex,c ->
   compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Görsel Algı"))
   compose.onNodeWithText("Görsel Algı").performClick()
   compose.onNodeWithText(c.titleTr).performScrollTo().performClick()
   c.rounds.forEachIndexed { index,r ->
    if(workshopIndex==0 && index==0) {
     tap("cell_${r.options.first { it !in r.answer }}");tap("check");tap("undo")
     compose.activityRule.scenario.recreate()
     assertEquals(1,p.getInt("${Curriculum.progressKey(c.id)}_attempts_0",0))
    }
    r.answer.forEach { tap("cell_$it") }
    tap("check");tap("check")
   }
   tap("workshop_finished");home()
  }
  assertEquals(95,LearningPath.score(p,GameCategory.VISUAL,"TR"))
  assertTrue(LearningPath.unlocked(p,GameCategory.ATTENTION,"TR"))
  assertFalse(LearningPath.unlocked(p,GameCategory.MEMORY,"TR"))
  compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Dikkat ve Odak"))
  compose.onNodeWithText("Dikkat ve Odak").assertIsEnabled()
  compose.onNodeWithText("Dikkat ve Odak").performClick();home()
  val day=Calendar.getInstance().let { "${it.get(Calendar.YEAR)}-${it.get(Calendar.DAY_OF_YEAR)}" }
  p.edit().remove("daily_bonus_$day").remove("daily_solved_${day}_6").commit()
  compose.onNode(hasScrollToIndexAction()).performScrollToIndex(0)
  compose.onNodeWithText("Günlük meydan okuma").performClick()
  val q=DailyMission.question(6,day.hashCode().toLong())
  tap("daily_${q.options.first { it!=q.answer }}")
  compose.onNodeWithText("Kontrol et").performClick()
  assertEquals(0,p.getInt("bonus_points",0))
  tap("daily_${q.answer}");compose.onNodeWithText("Kontrol et").performClick()
  assertEquals(20,p.getInt("bonus_points",0));home()
  compose.onNode(hasScrollToIndexAction()).performScrollToIndex(0)
  compose.onNodeWithText("Günlük meydan okuma").performClick()
  compose.onNodeWithText("Kontrol et").assertIsNotEnabled()
  assertEquals(20,p.getInt("bonus_points",0))
  assertFalse(LearningPath.unlocked(p,GameCategory.MEMORY,"TR"))
 }
}
