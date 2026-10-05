package com.algokids
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.algokids.data.StoryCatalog
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class)
class EnglishStoryTest {
 @get:Rule val compose=createAndroidComposeRule<MainActivity>()
 @Test fun everyEnglishStoryPageMatchesTheEnglishEdition() {
  compose.onNode(hasScrollToIndexAction()).performScrollToIndex(0)
  if(compose.onAllNodesWithText("TR / EN").fetchSemanticsNodes().isNotEmpty()) { compose.onNodeWithText("TR / EN").performClick();compose.waitForIdle() }
  compose.onNodeWithText("Stories").performClick()
  StoryCatalog.ordered(true).forEach { story ->
   compose.onNode(hasScrollToIndexAction()).performScrollToIndex(0)
   compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(story.titleEn))
   compose.onNodeWithText(story.titleEn).performClick()
   story.pagesEn.forEachIndexed { index,text ->
    compose.onNodeWithText(text).assertExists()
    compose.onNodeWithText(if(index==story.pagesEn.lastIndex) "Done" else "Next").performClick()
   }
  }
 }
}
