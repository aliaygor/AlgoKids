package com.algokids.game

import android.content.SharedPreferences
import com.algokids.data.*
import com.algokids.game.model.*
import org.junit.Assert.*
import org.junit.Test
import java.lang.reflect.Proxy

class LearningPathTest {
    private fun prefs(values:Map<String,Any>)=Proxy.newProxyInstance(SharedPreferences::class.java.classLoader,arrayOf(SharedPreferences::class.java)) { _,m,args ->
        when(m.name) {
            "getBoolean", "getInt" -> values[args!![0]] ?: args[1]
            "contains" -> values.containsKey(args!![0])
            else -> error("Unexpected preference call ${m.name}")
        }
    } as SharedPreferences
    @Test fun existingCompletionsUnlockNextSetWithoutInventingScores() {
        val values=mutableMapOf<String,Any>()
        assertTrue(LearningPath.unlocked(prefs(values),GameCategory.VISUAL,"TR"))
        assertFalse(LearningPath.unlocked(prefs(values),GameCategory.ATTENTION,"TR"))
        ChallengeCatalog.forCategory(GameCategory.VISUAL).forEach { c ->
            values["${Curriculum.progressKey(c.id)}_done"]=true
        }
        assertTrue(LearningPath.unlocked(prefs(values),GameCategory.ATTENTION,"TR"))
        assertEquals(0,LearningPath.solved(prefs(values),GameCategory.VISUAL,"TR"))
        ChallengeCatalog.forCategory(GameCategory.VISUAL).forEach { c -> c.rounds.indices.forEach { values[LearningPath.key(c.id,"TR",it)]=75 } }
        assertTrue(LearningPath.unlocked(prefs(values),GameCategory.ATTENTION,"TR"))
        assertFalse(LearningPath.unlocked(prefs(values),GameCategory.MEMORY,"TR"))
        assertFalse(LearningPath.unlocked(prefs(values),GameCategory.ATTENTION,"EN"))
        assertEquals(75,LearningPath.score(prefs(values),GameCategory.VISUAL,"TR"))
        assertEquals(2,LearningPath.level(prefs(values),"TR"))
    }
    @Test fun finalCorrectAnswerUnlocksSetBeforeLeavingTheResultScreen() {
        val values=mutableMapOf<String,Any>()
        ChallengeCatalog.forCategory(GameCategory.VISUAL).forEach { c -> c.rounds.indices.forEach { values[LearningPath.key(c.id,"TR",it)]=75 } }
        assertTrue(LearningPath.unlocked(prefs(values),GameCategory.ATTENTION,"TR"))
        values.remove(LearningPath.key(ChallengeCatalog.forCategory(GameCategory.VISUAL).last().id,"TR",2))
        assertFalse(LearningPath.unlocked(prefs(values),GameCategory.ATTENTION,"TR"))
    }
    @Test fun correctingAnAnswerDoesNotEraseTheMistakePenalty() {
        assertEquals(100,LearningPath.earned(0));assertEquals(75,LearningPath.earned(1))
        assertEquals(50,LearningPath.earned(2));assertEquals(25,LearningPath.earned(20))
        assertEquals(10,LearningPath.order.distinct().size)
    }
    @Test fun dailyQuestionsAreDeterministicAndHaveOneCorrectOptionForEveryAge() {
        for(age in 4..12) for(day in 0L..90L) {
            val q=DailyMission.question(age,day)
            assertEquals(q,DailyMission.question(age,day))
            assertEquals(4,q.options.distinct().size)
            assertEquals(1,q.options.count { it==q.answer })
            assertTrue(q.tr.isNotBlank() && q.en.isNotBlank() && q.whyTr.isNotBlank() && q.whyEn.isNotBlank())
        }
        assertNotEquals(DailyMission.question(4,2).tr,DailyMission.question(12,2).tr)
    }
}
