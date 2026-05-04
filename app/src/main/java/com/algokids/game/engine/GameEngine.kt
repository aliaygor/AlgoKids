package com.algokids.game.engine

import com.algokids.game.model.GameContent

class GameEngine {
    private var index = 0

    fun next(list: List<GameContent>): GameContent {
        val item = list[index % list.size]
        index++
        return item
    }

    fun check(content: GameContent, selected: String) =
        content.answer == selected
}
