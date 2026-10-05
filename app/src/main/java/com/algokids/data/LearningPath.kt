package com.algokids.data

import android.content.SharedPreferences
import com.algokids.game.model.*

/** A product learning sequence, not a measured ranking of children's intelligence. */
object LearningPath {
    val order = listOf(GameCategory.VISUAL, GameCategory.ATTENTION, GameCategory.MEMORY,
        GameCategory.AUDIOLOGY, GameCategory.NUMBERS, GameCategory.ALPHABET,
        GameCategory.GEOMETRY, GameCategory.NUMERICAL, GameCategory.LOGIC, GameCategory.ALGORITHM)
    fun earned(mistakes: Int) = 100 - mistakes.coerceIn(0,3) * 25
    fun key(id: String, language: String, round: Int) = "${Curriculum.progressKey(id,language)}_exam_$round"
    fun workshopCompleted(p:SharedPreferences,c:Challenge,language:String) =
        p.getBoolean("${Curriculum.progressKey(c.id,language)}_done",false) ||
            c.rounds.indices.all { p.contains(key(c.id,language,it)) }
    fun completed(p: SharedPreferences, category: GameCategory, language: String): Boolean =
        ChallengeCatalog.forCategory(category).all { workshopCompleted(p,it,language) }
    fun unlocked(p: SharedPreferences, category: GameCategory, language: String) =
        order.take(order.indexOf(category)).all { completed(p,it,language) }
    fun level(p: SharedPreferences, language: String) =
        (1 + order.takeWhile { completed(p,it,language) }.size).coerceAtMost(order.size)
    fun score(p: SharedPreferences, category: GameCategory, language: String): Int {
        val keys=ChallengeCatalog.forCategory(category).flatMap { c -> c.rounds.indices.map { key(c.id,language,it) } }
        return if(keys.isEmpty()) 0 else keys.sumOf { p.getInt(it,0) } / keys.size
    }
    fun total(category: GameCategory) = ChallengeCatalog.forCategory(category).sumOf { it.rounds.size }
    fun solved(p: SharedPreferences, category: GameCategory, language: String) =
        ChallengeCatalog.forCategory(category).sumOf { c -> c.rounds.indices.count { p.contains(key(c.id,language,it)) } }
}
