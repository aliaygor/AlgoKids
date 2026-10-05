package com.algokids.data

import com.algokids.game.model.GameCategory

/** Old picture variants are levels of one foundation exercise, never distinct workshops. */
object Curriculum {
    const val REVISION = 3
    private val foundations = mapOf(
        GameCategory.VISUAL to listOf("v4", "v9", "v14", "v19", "v23", "v29"),
        GameCategory.NUMERICAL to listOf("n5", "n1", "n2", "n3", "n4", "n6", "n7", "n8", "n9", "n10"),
        GameCategory.LOGIC to listOf("l1", "l2", "l11"),
        GameCategory.GEOMETRY to listOf("g2", "g4", "g12", "g19")
    )
    fun foundationIds(category: GameCategory) = foundations[category].orEmpty()
    fun hasFoundation(category: GameCategory) = foundationIds(category).isNotEmpty() ||
        category in setOf(GameCategory.ALGORITHM, GameCategory.ALPHABET, GameCategory.NUMBERS)
    fun progressKey(id: String, language: String = "TR") = "challenge_v${REVISION}_$id" + if(language == "EN") "_EN" else ""
}
