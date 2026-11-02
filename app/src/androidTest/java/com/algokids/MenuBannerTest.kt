package com.algokids

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MenuBannerTest {
    @get:Rule(order=0) val notifications=AuditPermissionRule()
    @get:Rule(order=1) val compose=createAndroidComposeRule<MainActivity>()

    @Test fun firstSessionBannerLoadsAndDisappearsBeforeGameplay() {
        val prefs=compose.activity.getSharedPreferences("algokids_progress",0)
        prefs.edit().putBoolean("ads_disabled_for_tests",false)
            .putInt("ads_session",1).putInt("ads_shown_session",0)
            .putString("language","TR").commit()
        compose.activityRule.scenario.recreate()
        compose.waitUntil(60_000) {
            compose.onAllNodesWithTag("home_banner").fetchSemanticsNodes()
                .any { it.boundsInRoot.height>0 }
        }
        val capture=androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        AuditScreenshot.save(compose.activity,capture,"menu-banner-2.2.png")
        capture.recycle()
        assertEquals(1,prefs.getInt("ads_session",0))
        assertEquals(0,prefs.getInt("ads_shown_session",0))
        compose.onAllNodesWithTag("parent_banner").assertCountEquals(0)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Görsel Algı"))
        compose.onNodeWithText("Görsel Algı",useUnmergedTree=true).performClick()
        compose.onAllNodesWithTag("home_banner").assertCountEquals(0)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Gölgeden nesneye"))
        compose.onNodeWithText("Gölgeden nesneye",useUnmergedTree=true).performClick()
        compose.onAllNodesWithTag("home_banner").assertCountEquals(0)
        compose.onAllNodesWithTag("parent_banner").assertCountEquals(0)
        assertEquals(0,prefs.getInt("ads_shown_session",0))
    }
}
