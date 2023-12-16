import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlin.system.measureTimeMillis

enum class Direction {
    North,
    West,
    East,
    South,
}

data class Position(val rowIndex: Int, val columnIndex: Int)
data class Beam(val position: Position, val direction: Direction)

data class BreathFirstSearchResult<T>(val finalNodes: Set<T>, val visitedNodes: Set<T>)

fun <T> Set<T>.breathFirstSearch(
    isFinished: (Set<T>) -> Boolean,
    childNodes: T.() -> List<T>,
): BreathFirstSearchResult<T> {
    tailrec fun Set<T>.recur(visited: PersistentSet<T>): BreathFirstSearchResult<T> {
        if (isFinished(this)) return BreathFirstSearchResult(finalNodes = this, visitedNodes = visited)

        return this
            .flatMap(childNodes)
            .filter { visited.contains(it).not() }
            .toSet()
            .recur(visited.addAll(this))
    }

    return recur(persistentSetOf())
}

fun main() {
    fun Sequence<Beam>.computeVisitedBeamStates(grid: List<List<Char>>): Sequence<Set<Beam>> {
        fun Beam.getChildren(): List<Beam> = buildList {
            val (rowIndex, columnIndex) = position

            fun goNorth() {
                if (rowIndex > 0) add(Beam(Position(rowIndex - 1, columnIndex), Direction.North))
            }

            fun goSouth() {
                if (rowIndex < grid.size - 1) add(
                    Beam(
                        Position(rowIndex + 1, columnIndex),
                        Direction.South
                    )
                )
            }

            fun goEast() {
                if (columnIndex < grid[0].size - 1) add(
                    Beam(
                        Position(rowIndex, columnIndex + 1),
                        Direction.East
                    )
                )
            }

            fun goWest() {
                if (columnIndex > 0) add(Beam(Position(rowIndex, columnIndex - 1), Direction.West))
            }

            when (direction) {
                Direction.North -> {
                    when (grid[rowIndex][columnIndex]) {
                        '.', '|' -> goNorth()
                        '-' -> {
                            goEast()
                            goWest()
                        }

                        '/' -> goEast()
                        '\\' -> goWest()
                        else -> never()
                    }
                }

                Direction.West -> {
                    when (grid[rowIndex][columnIndex]) {
                        '.', '-' -> goWest()
                        '|' -> {
                            goNorth()
                            goSouth()
                        }

                        '/' -> goSouth()
                        '\\' -> goNorth()
                        else -> never()
                    }
                }

                Direction.East -> {
                    when (grid[rowIndex][columnIndex]) {
                        '.', '-' -> goEast()
                        '|' -> {
                            goNorth()
                            goSouth()
                        }

                        '/' -> goNorth()
                        '\\' -> goSouth()
                        else -> never()
                    }
                }

                Direction.South -> {
                    when (grid[rowIndex][columnIndex]) {
                        '.', '|' -> goSouth()
                        '-' -> {
                            goEast()
                            goWest()
                        }

                        '/' -> goWest()
                        '\\' -> goEast()
                        else -> never()
                    }
                }
            }
        }

        return map { startPosition ->
            setOf(startPosition)
                .breathFirstSearch({ it.isEmpty() }) { getChildren() }
                .visitedNodes
        }
    }

    fun part1(input: List<String>): Int {
        val grid = input.map { line -> line.toList() }

        sequence {
            yield(Beam(Position(0, 0), Direction.East))
        }
            .computeVisitedBeamStates(grid = grid)
            .map { visitedBeamStates -> visitedBeamStates.map { it.position }.toSet().count() }
            .max()
            .run { return this }
    }

    fun part2(input: List<String>): Int {
        val grid = input.map { line -> line.toList() }

        sequence {
            yieldAll(grid.indices.map { Beam(Position(it, 0), Direction.East) })
            yieldAll(grid.indices.map { Beam(Position(it, grid[0].size - 1), Direction.West) })
            yieldAll(grid[0].indices.map { Beam(Position(0, it), Direction.South) })
            yieldAll(grid[0].indices.map { Beam(Position(grid.size - 1, it), Direction.North) })
        }
            .computeVisitedBeamStates(grid = grid)
            .map { visitedBeamStates -> visitedBeamStates.map { it.position }.toSet().count() }
            .max()
            .run { return this }
    }

    val testInput1 = readLines("Day16_1_test")
    check(part1(testInput1) == 46)
    check(part2(testInput1) == 51)
    val input = readLines("Day16")
    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    measureTimeMillis { part2(input).println() }.also { println("time: $it") }
}
