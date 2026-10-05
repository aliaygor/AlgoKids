package com.algokids.game.engine

enum class Move(val icon: String, val dx: Int, val dy: Int) {
    UP("↑", 0, -1), DOWN("↓", 0, 1), LEFT("←", -1, 0), RIGHT("→", 1, 0)
}

data class GridPoint(val x: Int, val y: Int)

data class RouteLevel(
    val titleTr: String, val titleEn: String,
    val hero: String, val goalEmoji: String,
    val start: GridPoint, val goal: GridPoint,
    val blocks: Set<GridPoint>, val size: Int = 4
) {
    val solution: List<Move> get() = RouteEngine.shortestPath(this)
    val maxCommands: Int get() = solution.size
}

data class RouteResult(val path: List<GridPoint>, val failedStep: Int?, val reachedGoal: Boolean)

object RouteEngine {
    fun run(level: RouteLevel, commands: List<Move>): RouteResult {
        val path = mutableListOf(level.start)
        commands.forEachIndexed { index, move ->
            val current = path.last()
            val next = GridPoint(current.x + move.dx, current.y + move.dy)
            if (!isWalkable(level, next)) return RouteResult(path, index + 1, false)
            path.add(next)
        }
        return RouteResult(path, null, path.last() == level.goal)
    }

    fun shortestPath(level: RouteLevel): List<Move> {
        val queue = ArrayDeque<Pair<GridPoint, List<Move>>>()
        val visited = mutableSetOf(level.start)
        queue.add(level.start to emptyList())
        while (queue.isNotEmpty()) {
            val (point, moves) = queue.removeFirst()
            if (point == level.goal) return moves
            Move.entries.forEach { move ->
                val next = GridPoint(point.x + move.dx, point.y + move.dy)
                if (isWalkable(level, next) && visited.add(next)) queue.add(next to (moves + move))
            }
        }
        error("Route has no solution: ${level.titleEn}")
    }

    private fun isWalkable(level: RouteLevel, point: GridPoint) =
        point.x in 0 until level.size && point.y in 0 until level.size && point !in level.blocks
}

object RouteLevels {
    val all = listOf(
        RouteLevel("Roketi yıldıza ulaştır", "Guide the rocket to the star", "🚀", "⭐", GridPoint(0, 2), GridPoint(3, 0), setOf(GridPoint(1, 1))),
        RouteLevel("Tavşanı havuca ulaştır", "Guide the bunny to the carrot", "🐰", "🥕", GridPoint(0, 0), GridPoint(3, 3), setOf(GridPoint(1, 0), GridPoint(1, 1), GridPoint(2, 2))),
        RouteLevel("Robotu şarja ulaştır", "Guide the robot to the charger", "🤖", "🔋", GridPoint(3, 0), GridPoint(0, 3), setOf(GridPoint(2, 1), GridPoint(1, 1))),
        RouteLevel("Kargoyu eve ulaştır", "Deliver the parcel", "📦", "🏠", GridPoint(0, 3), GridPoint(3, 1), setOf(GridPoint(1, 2), GridPoint(2, 2))),
        RouteLevel("Arıyı çiçeğe ulaştır", "Guide the bee to the flower", "🐝", "🌸", GridPoint(0, 1), GridPoint(3, 2), setOf(GridPoint(1, 2), GridPoint(2, 1))),
        RouteLevel("Balığı denize ulaştır", "Guide the fish to the sea", "🐟", "🌊", GridPoint(3, 3), GridPoint(0, 0), setOf(GridPoint(2, 2), GridPoint(1, 2), GridPoint(2, 0))),
        RouteLevel("Treni istasyona ulaştır", "Guide the train to the station", "🚂", "🚉", GridPoint(0, 0), GridPoint(3, 2), setOf(GridPoint(0, 1), GridPoint(2, 1))),
        RouteLevel("Kediyi eve ulaştır", "Guide the cat home", "🐱", "🏠", GridPoint(3, 1), GridPoint(0, 3), setOf(GridPoint(2, 2), GridPoint(1, 1))),
        RouteLevel("Dolambaçlı yol", "The winding path", "🤖", "🔋", GridPoint(0, 0), GridPoint(4, 0), setOf(GridPoint(1, 0), GridPoint(1, 1), GridPoint(1, 2), GridPoint(3, 2), GridPoint(3, 3), GridPoint(3, 4)), 5),
        RouteLevel("Labirent ustası", "Maze master", "🚀", "⭐", GridPoint(0, 4), GridPoint(4, 0), setOf(GridPoint(1, 4), GridPoint(1, 3), GridPoint(1, 2), GridPoint(3, 0), GridPoint(3, 1), GridPoint(3, 2)), 5)
    )
}
