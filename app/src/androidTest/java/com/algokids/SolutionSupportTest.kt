package com.algokids
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.algokids.data.*
import com.algokids.game.model.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class)
class SolutionSupportTest {
 @get:Rule(order=0) val notifications=AuditPermissionRule()
 @get:Rule(order=1) val compose=createAndroidComposeRule<MainActivity>()
 @Test fun automaticExamplesSpendFiveSharedUsesWithoutAwardingCompletion() {
  val p=compose.activity.getSharedPreferences("algokids_progress",0)
  p.edit().putBoolean("ads_disabled_for_tests",true).putString("language","TR")
   .remove("solution_day").remove("solution_used").remove("solution_bonus").commit()
  ProgressStore(compose.activity).resetLearning()
  compose.activityRule.scenario.recreate()
  if(compose.onAllNodesWithText("EN / TR").fetchSemanticsNodes().isNotEmpty())compose.onNodeWithText("EN / TR").performClick()
  compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Görsel Algı"))
  compose.onNodeWithText("Görsel Algı").performClick()
  val c=ChallengeCatalog.forCategory(GameCategory.VISUAL).first()
  compose.onNodeWithText(c.titleTr).performScrollTo().performClick()
  repeat(5) { count ->
   compose.onNodeWithTag("automatic_solution").performScrollTo().performClick()
   compose.waitUntil(15000) {compose.onAllNodesWithText("Şimdi kendi panonda dene.",substring=true).fetchSemanticsNodes().isNotEmpty()}
   assertEquals(4-count,SolutionAllowance(p).remaining())
   assertFalse(p.contains(LearningPath.key(c.id,"TR",0)))
   compose.onNodeWithTag("example_solution").performScrollTo().performClick()
  }
  compose.onNodeWithTag("automatic_solution").assertIsNotEnabled()
  compose.activityRule.scenario.recreate()
  assertEquals(0,SolutionAllowance(p).remaining())
  compose.onNodeWithTag("example_solution").performScrollTo().performClick()
  compose.onNodeWithText("Sonraki çözüm adımı").performScrollTo().assertIsEnabled()
  val i=InstrumentationRegistry.getInstrumentation()
  val dir=java.io.File(i.targetContext.filesDir,"screenshots").apply {mkdirs()}
  val bitmap=i.uiAutomation.takeScreenshot()
  java.io.File(dir,"solution-support.png").outputStream().use {bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)}
  AuditScreenshot.save(compose.activity,bitmap,"solution-support.png")
  bitmap.recycle()
 }
}
