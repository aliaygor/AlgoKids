package com.algokids.game
import com.algokids.data.*
import com.algokids.game.model.*
import com.algokids.notifications.ReminderRules
import com.algokids.speech.ChildNarration
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
class FollowupContentTest {
 @Test fun bilingualStoriesGrowAndEveryPageHasItsTranslation() {
  assertEquals(10,StoryCatalog.all.size)
  listOf(false,true).forEach { en ->
   val lengths=StoryCatalog.ordered(en).map { StoryCatalog.words(it,en) }
   assertEquals(lengths.sorted(),lengths)
   assertTrue(lengths.first()>=120);assertTrue(lengths.last()>=450)
  }
  StoryCatalog.all.forEach { s ->
   assertEquals(s.pages.size,s.pagesEn.size);assertEquals(s.pages.size,s.pageImages!!.size)
   assertTrue(s.pages.all { it.isNotBlank() });assertTrue(s.pagesEn.all { it.isNotBlank() })
  }
 }
 @Test fun englishLearningReallyUsesEnglishContent() {
  val english=ChallengeCatalog.all.map { ChallengeLocalization.localize(it,true) }
  english.forEach { c -> c.rounds.forEach { assertTrue(it.promptEn.isNotBlank());assertTrue(it.explanationEn.isNotBlank());assertTrue(it.accepts(it.answer,c.mode)) } }
  val alphabet=english.first { it.id=="alphabet_order" }
  assertEquals(listOf("H","I","J","K"),alphabet.rounds[1].answer)
  assertEquals(listOf("RAIN","BOW"),english.first { it.id=="syllable_build" }.rounds.first().answer)
  assertTrue(english.first { it.id=="numbers_read" }.rounds[1].promptEn.contains("sixty-four"))
  assertNotEquals(Curriculum.progressKey("alphabet_order","TR"),Curriculum.progressKey("alphabet_order","EN"))
 }
 @Test fun remindersAreSpacedAndDoNotWakeChildrenAtNight() {
  val now=Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY,18);set(Calendar.MINUTE,0);set(Calendar.SECOND,0);set(Calendar.MILLISECOND,0) }.timeInMillis
  assertTrue(ReminderRules.shouldSend(now,now-2*ReminderRules.DAY,0,2))
  assertFalse(ReminderRules.shouldSend(now,now-ReminderRules.DAY,0,2))
  assertFalse(ReminderRules.shouldSend(now,now-3*ReminderRules.DAY,now-3600000,1))
  assertFalse(ReminderRules.shouldSend(now+5*3600000,now-3*ReminderRules.DAY,0,1))
  assertTrue(ReminderRules.next(now,now,0,2)>=now+2*ReminderRules.DAY)
 }
 @Test fun spokenSymbolsBecomeWordsAndNumericTransitionsDoNotBecomeDirections() {
  assertEquals("moon star sun",ChildNarration.text("🌙 ⭐ ☀️",true))
  assertEquals("5 sonra 8 sonra 16",ChildNarration.text("5 → 8 → 16",false))
  assertEquals("right up",ChildNarration.text("→ ↑",true))
 }
}
