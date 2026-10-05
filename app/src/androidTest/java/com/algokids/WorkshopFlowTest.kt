package com.algokids
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.algokids.data.*
import com.algokids.game.engine.RouteLevels
import com.algokids.game.model.*
import com.algokids.ui.screens.AppLanguage
import com.algokids.ui.screens.categoryTitle
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
@RunWith(AndroidJUnit4::class)
class WorkshopFlowTest {
 @get:Rule val compose=createAndroidComposeRule<MainActivity>()
 private val english=InstrumentationRegistry.getArguments().getString("auditLanguage")=="EN"
 private val language get()=if(english) AppLanguage.EN else AppLanguage.TR
 private fun t(tr:String,en:String)=if(english) en else tr
 private fun tap(tag:String) { compose.onNodeWithTag(tag).performScrollTo().performClick() }
 private fun category(type:GameCategory) {
  val title=categoryTitle(type,language)
  compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(title))
  compose.onNodeWithText(title).performClick()
 }
 private fun home() { compose.onNodeWithText(t("‹ Ana sayfa","‹ Home")).performScrollTo().performClick() }
 private fun capture(name:String) {
  compose.waitForIdle()
  val i=InstrumentationRegistry.getInstrumentation()
  val dir=File(i.targetContext.filesDir,"screenshots").apply { mkdirs() }
  val screenshot=i.uiAutomation.takeScreenshot()
  File(dir,"${name}${if(english) "-en" else ""}.png").outputStream().use { screenshot.compress(Bitmap.CompressFormat.PNG,100,it) }
  screenshot.recycle()
 }
 @Test fun completeEveryWorkshopFoundationRouteAndLearningCard() {
  val prefs=compose.activity.getSharedPreferences("algokids_progress",Context.MODE_PRIVATE)
  val editor=prefs.edit().putBoolean("ads_disabled_for_tests",true)
  ChallengeCatalog.all.forEach { c ->
   val key=Curriculum.progressKey(c.id,language.name)
   editor.remove("${key}_done").remove("${key}_round")
   c.rounds.indices.forEach { editor.remove(LearningPath.key(c.id,language.name,it)).remove("${key}_attempts_$it") }
  }
  GameCategory.entries.forEach { editor.remove("session_v${Curriculum.REVISION}_${it.name}") }
  editor.putInt("route_level",0).putInt("learning_NUMBERS_${language.name}",0).putInt("learning_ALPHABET_${language.name}",0).commit()
  compose.activityRule.scenario.recreate()
  compose.onNode(hasScrollToIndexAction()).performScrollToIndex(0)
  val switch=if(english) "TR / EN" else "EN / TR"
  if(compose.onAllNodesWithText(switch).fetchSemanticsNodes().isNotEmpty()) { compose.onNodeWithText(switch).performClick();compose.waitForIdle() }
  capture("home")
  ChallengeCatalog.all.sortedBy { LearningPath.order.indexOf(it.category) }.map { ChallengeLocalization.localize(it,english) }.forEach { c ->
   category(c.category)
   if(c.id=="loops") capture("algorithm-workshops")
   compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(t(c.titleTr,c.titleEn)))
   compose.onNodeWithText(t(c.titleTr,c.titleEn)).performClick()
   c.rounds.forEachIndexed { index,r ->
    compose.onNodeWithText(t("Görev ${index+1}/3","Mission ${index+1}/3"),substring=true).assertExists()
    if(c.mode in setOf(ChallengeMode.RECALL,ChallengeMode.GRID_RECALL)) { compose.onNodeWithTag("check").assertIsNotEnabled(); tap("memory_toggle") }
    if(c.mode in setOf(ChallengeMode.LISTEN,ChallengeMode.LISTEN_CHOICE)) { tap("transcript"); compose.onNodeWithText(t(r.spokenTr,r.spokenEn)).assertExists() }
    r.answer.forEach { token -> tap(when(c.mode) {
     ChallengeMode.GRID,ChallengeMode.GRID_RECALL -> "cell_$token"
     ChallengeMode.SCAN -> if(token=="✓") "scan_select" else "scan_skip"
     else -> "choice_$token"
    }) }
    if(c.id=="conditions" && index==2) compose.activityRule.scenario.recreate()
    tap("check")
    compose.onNodeWithTag("feedback").assertTextContains(t("Çözdün!","Solved!"),substring=true)
    if(index==0 && c.id in setOf("loops","geometry_turn","visual_mirror","memory_order","attention_switch","listening_order")) capture(c.id+"-feedback")
    tap("check")
   }
   tap("workshop_finished")
   assertTrue(c.id,prefs.getBoolean("${Curriculum.progressKey(c.id,language.name)}_done",false))
   home()
  }
  val repository=ContentRepository(compose.activity)
  GameCategory.entries.filter { Curriculum.foundationIds(it).isNotEmpty() }.forEach { type ->
   category(type)
   val title=when(type) { GameCategory.VISUAL -> t("Gölgeden nesneye","From silhouette to object"); GameCategory.NUMERICAL -> t("Bire bir sayma","One-to-one counting"); GameCategory.LOGIC -> t("Tekrar eden gruplar","Repeating groups"); else -> t("Şekillerin özellikleri","Shape properties") }
   compose.onNodeWithText(title).performScrollTo().performClick()
   repository.getContentByCategory(type).forEach { item -> tap("foundation_choice_${item.answer}"); tap("foundation_next") }
   compose.onNodeWithTag("foundation_finished").performClick()
   home()
  }
  category(GameCategory.ALGORITHM)
  compose.onNodeWithText(t("Rota atölyesi","Route workshop")).performScrollTo().performClick()
  RouteLevels.all.forEachIndexed { index,level ->
   level.solution.forEach { move -> compose.onNode(hasText(move.icon) and hasClickAction()).performScrollTo().performClick() }
   compose.onNodeWithText(t("Programı çalıştır","Run program")).performScrollTo().performClick()
   compose.waitUntil(15000) { compose.onAllNodesWithText(t("Başardın!","You did it!"),substring=true).fetchSemanticsNodes().isNotEmpty() }
   assertTrue(prefs.getBoolean("route_done_$index",false))
   if(index==0) capture("rocket-complete")
   compose.onNodeWithText(if(index==RouteLevels.all.lastIndex) t("Atölyeyi bitir","Finish workshop") else t("Sonraki bölüm","Next level")).performScrollTo().performClick()
  }
  compose.onNodeWithText(t("Oyunlara dön","Back to games")).performClick()
  home()
  listOf(GameCategory.ALPHABET to LearningContent.alphabet(!english),GameCategory.NUMBERS to LearningContent.numbers()).forEach { (type,cards) ->
   category(type)
   compose.onNodeWithText(t("Keşfet ve dinle","Explore and listen")).performScrollTo().performClick()
   cards.forEachIndexed { index,card ->
    compose.onNodeWithText(t(card.detailTr,card.detailEn)).assertExists()
    if(index<cards.lastIndex) compose.onNodeWithText(t("İleri","Next")).performScrollTo().performClick()
   }
   compose.activityRule.scenario.recreate()
   compose.onNodeWithText(t(cards.last().detailTr,cards.last().detailEn)).assertExists()
   compose.onNodeWithContentDescription(t("Menüye dön","Back to menu")).performClick()
   home()
  }
  capture("completed-home")
 }
}
