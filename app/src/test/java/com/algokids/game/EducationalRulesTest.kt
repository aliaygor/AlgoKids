package com.algokids.game

import com.algokids.game.engine.*
import com.algokids.game.model.*
import com.algokids.data.LearningContent
import org.junit.Assert.*
import org.junit.Test

class EducationalRulesTest {
    @Test fun everyRouteHasAValidShortestSolution() {
        RouteLevels.all.forEach { level ->
            val result = RouteEngine.run(level, level.solution)
            assertTrue(level.titleEn, result.reachedGoal)
            assertNull(result.failedStep)
            assertEquals(level.maxCommands, result.path.size - 1)
            assertTrue(result.path.none { it in level.blocks })
            assertEquals(level.solution.size + 1, result.path.distinct().size)
        }
    }

    @Test fun rocketCountsMovesRatherThanVisitedSquares() {
        val level=RouteLevels.all.first()
        assertEquals(5,level.maxCommands)
        assertEquals(6,RouteEngine.run(level,level.solution).path.size)
    }

    @Test fun executionStopsAtFirstCollisionEvenIfLaterMovesReachGoal() {
        val level=RouteLevel("test","test","","",GridPoint(0,0),GridPoint(1,0),emptySet())
        val result=RouteEngine.run(level,listOf(Move.LEFT,Move.RIGHT))
        assertEquals(1,result.failedStep)
        assertEquals(listOf(GridPoint(0,0)),result.path)
        assertFalse(result.reachedGoal)
    }

    @Test fun obstacleCollisionDoesNotExecuteLaterCommands() {
        val level=RouteLevel("test","test","","",GridPoint(0,0),GridPoint(0,1),setOf(GridPoint(1,0)))
        assertEquals(1,RouteEngine.run(level,listOf(Move.RIGHT,Move.DOWN)).failedStep)
        assertEquals(level.start,RouteEngine.run(level,listOf(Move.RIGHT,Move.DOWN)).path.last())
    }

    @Test fun turkishAlphabetHas29DistinctLettersAndCorrectDottedI() {
        val letters=LearningContent.turkishLetters
        assertEquals(29,letters.size)
        assertEquals(29,letters.map { it.first }.distinct().size)
        assertEquals("ı",letters.toMap()["I"])
        assertEquals("i",letters.toMap()["İ"])
        assertEquals("ce",letters.toMap()["C"])
        assertEquals("yumuşak ge",letters.toMap()["Ğ"])
        assertEquals(listOf("H","I","İ","J"),letters.map { it.first }.subList(9,13))
    }

    @Test fun commonlyConfusedTurkishNumbersAreCorrect() {
        mapOf(0 to "sıfır",4 to "dört",40 to "kırk",60 to "altmış",70 to "yetmiş",80 to "seksen",90 to "doksan",100 to "yüz",64 to "altmış dört").forEach { (number,name) ->
            assertEquals(name,LearningContent.numberToTurkish(number))
        }
        assertEquals(101,LearningContent.numbers().size)
        assertEquals(101,(0..100).map { LearningContent.numberToTurkish(it) }.distinct().size)
    }

    @Test fun everyChallengeCanBeAnsweredUsingItsCards() {
        assertEquals(ChallengeCatalog.all.size,ChallengeCatalog.all.map { it.id }.distinct().size)
        GameCategory.entries.forEach { assertTrue(it.name,ChallengeCatalog.forCategory(it).size>=2) }
        ChallengeCatalog.all.forEach { challenge ->
            assertTrue(challenge.rounds.size>=3)
            challenge.rounds.forEach { round ->
                assertEquals("${challenge.id}: duplicate options",round.options.size,round.options.distinct().size)
                assertTrue(round.answer.isNotEmpty())
                assertTrue(round.options.containsAll(round.answer))
                assertTrue(round.accepts(round.answer,challenge.mode))
                assertFalse(round.accepts(emptyList(),challenge.mode))
                assertTrue(round.explanationTr.isNotBlank() && round.explanationEn.isNotBlank())
            }
        }
    }

    @Test fun multiSelectRequiresAllAndOnlyMatchingCards() {
        val round=ChallengeRound("","",listOf("6","8","9"),listOf("6","8"),"","")
        assertTrue(round.accepts(listOf("8","6"),ChallengeMode.MULTI))
        assertFalse(round.accepts(listOf("6"),ChallengeMode.MULTI))
        assertFalse(round.accepts(listOf("6","8","9"),ChallengeMode.MULTI))
        assertFalse(round.accepts(listOf("6","8","8"),ChallengeMode.MULTI))
    }

    @Test fun orderMattersAndReverseRecallReallyReversesTheStimulus() {
        val challenge=ChallengeCatalog.all.first { it.id=="memory_reverse" }
        challenge.rounds.forEach { round ->
            assertEquals(round.stimulus.reversed(),round.answer)
            assertFalse(round.accepts(round.stimulus,challenge.mode))
        }
    }

    @Test fun numberMachinesApplyOperationsInTheDeclaredOrder() {
        val challenge=ChallengeCatalog.all.first { it.id=="number_machine" }
        val calculated = listOf((3+2)*2, 8/2+3, (5+3)*2-4)
        challenge.rounds.forEachIndexed { index,round ->
            assertEquals(listOf(calculated[index].toString()),round.answer)
            assertFalse(round.accepts(listOf("99"),challenge.mode))
        }
    }

    @Test fun matchingDoesNotAcceptAnAssociationFromADifferentQuestion() {
        val content=GameContent(type=GameType.FUNCTIONAL_MATCH,questionAssets=listOf("dog","bee"),options=listOf("house","flower"))
        assertTrue(ContentRules.checkMatch(content,"dog","house"))
        assertFalse(ContentRules.checkMatch(content,"dog","bone"))
        assertFalse(ContentRules.checkMatch(content,"dog","flower"))
    }
}
