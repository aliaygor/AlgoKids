package com.algokids.game.engine

object GridRules {
    fun rotate(cells: Set<Int>, size: Int, quarterTurns: Int): Set<Int> {
        require(size > 0 && cells.all { it in 0 until size * size })
        var result = cells
        repeat(((quarterTurns % 4) + 4) % 4) {
            result = result.map { cell -> (cell % size) * size + (size - 1 - cell / size) }.toSet()
        }
        return result
    }

    fun reflect(cells: Set<Int>, size: Int, vertical: Boolean): Set<Int> {
        require(size > 0 && cells.all { it in 0 until size * size })
        return cells.map { cell ->
            val row = cell / size
            val col = cell % size
            if (vertical) row * size + (size - 1 - col) else (size - 1 - row) * size + col
        }.toSet()
    }
}
