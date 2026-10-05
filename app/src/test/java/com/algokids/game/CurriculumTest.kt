package com.algokids.game
import com.algokids.data.Curriculum
import com.algokids.game.engine.GridRules
import com.algokids.game.model.*
import org.junit.Assert.*
import org.junit.Test
class CurriculumTest {
 @Test fun distinctCurriculum() {
  assertEquals(21,ChallengeCatalog.all.size)
  assertEquals(63,ChallengeCatalog.all.sumOf { it.rounds.size })
  assertEquals(21,ChallengeCatalog.all.map { it.mechanic }.distinct().size)
  ChallengeCatalog.all.forEach { assertEquals(it.mechanic.owner,it.category) }
  assertEquals(23,GameCategory.entries.sumOf { Curriculum.foundationIds(it).size })
 }
 @Test fun independentGeometryCoordinates() {
  val shape=setOf(0,3,6,7)
  assertEquals(setOf(0,1,2,3),GridRules.rotate(shape,3,1))
  assertEquals(setOf(1,2,5,8),GridRules.rotate(shape,3,2))
  assertEquals(setOf(5,6,7,8),GridRules.rotate(shape,3,3))
  assertEquals(shape,GridRules.rotate(shape,3,4))
  assertEquals(setOf(2,5,7,8),GridRules.reflect(shape,3,true))
  assertEquals(setOf(0,1,3,6),GridRules.reflect(shape,3,false))
 }
 @Test fun visualIntegrationAndMissingParts() {
  val union=ChallengeCatalog.all.first { it.id=="visual_mirror" }.rounds.first()
  val missing=ChallengeCatalog.all.first { it.id=="visual_rule" }.rounds.first()
  assertEquals(setOf("0","1","4","7","8"),union.answer.toSet())
  assertEquals(setOf("1","3"),missing.answer.toSet())
  assertFalse(missing.accepts(listOf("0","1","3","4"),ChallengeMode.GRID))
  assertFalse(union.accepts(union.answer+"4",ChallengeMode.GRID))
 }
 @Test fun independentPlanningStepsHaveMultipleValidOrders() {
  val r=ChallengeCatalog.all.first { it.id=="dependencies" }.rounds[1]
  assertTrue(r.accepts(listOf("☀️","🪴","🌰","💧","🌱"),ChallengeMode.ORDER))
  assertTrue(r.accepts(listOf("🪴","🌰","☀️","💧","🌱"),ChallengeMode.ORDER))
  assertFalse(r.accepts(listOf("🪴","🌰","🌱","💧","☀️"),ChallengeMode.ORDER))
 }
 @Test fun everyAttentionDecisionUsesCurrentTarget() {
  listOf("attention_filter","attention_switch").forEach { id ->
   ChallengeCatalog.all.first { it.id==id }.rounds.forEach { r ->
    assertEquals(r.stimulus.size,r.stageRulesTr.size)
    r.stimulus.forEachIndexed { i,card -> assertEquals(if(card==r.stageRulesTr[i].substringAfter(": ")) "✓" else "–",r.answer[i]) }
   }
  }
 }
 @Test fun independentBalanceAndBranchSolutions() {
  ChallengeCatalog.all.first { it.id=="number_order" }.rounds.zip(listOf(8-3,12/2,(16-2)/2)).forEach { (r,n) -> assertEquals(listOf(n.toString()),r.answer) }
  assertEquals(listOf("🚪","🔋","⏸","🚪"),ChallengeCatalog.all.first { it.id=="conditions" }.rounds[2].answer)
  assertEquals(listOf("2","3","4"),ChallengeCatalog.all.first { it.id=="debug" }.rounds.map { it.answer.single() })
 }
}
