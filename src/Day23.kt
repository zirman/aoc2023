import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlin.system.measureTimeMillis

fun Position.goUp(): Position = copy(rowIndex = rowIndex - 1)
fun Position.goDown(): Position = copy(rowIndex = rowIndex + 1)
fun Position.goLeft(): Position = copy(columnIndex = columnIndex - 1)
fun Position.goRight(): Position = copy(columnIndex = columnIndex + 1)

fun main() {
    fun part1(input: List<String>): Int {
        val map = input.map { line -> line.toList() }

        operator fun List<List<Char>>.get(position: Position): Char = this[position.rowIndex][position.columnIndex]
        val endPosition = Position(map.indices.last, map[0].indices.last - 1)

        fun depthFirstSearch(visited: PersistentSet<Position>, current: Position): Int {
            if (current == endPosition) return visited.size
            val nextVisited = visited.add(current)
            when (map[current]) {
                '>' -> listOf(current.goRight())
                '<' -> listOf(current.goLeft())
                'v' -> listOf(current.goDown())
                '^' -> listOf(current.goUp())
                '.' -> listOf(
                    current.goUp(),
                    current.goDown(),
                    current.goLeft(),
                    current.goRight(),
                )

                else -> never()
            }
                .maxOf { nextPosition ->
                    if (
                        nextPosition.rowIndex in map.indices &&
                        nextPosition.columnIndex in map[0].indices &&
                        map[nextPosition] != '#' &&
                        visited.contains(nextPosition).not()
                    ) {
                        depthFirstSearch(visited = nextVisited, current = nextPosition)
                    } else {
                        0
                    }
                }
                .run { return this }
        }

        return depthFirstSearch(visited = persistentSetOf(), current = Position(0, 1))
    }

    fun part2(input: List<String>): Int {
        val map = input.map { line -> line.toList() }

        operator fun List<List<Char>>.get(position: Position): Char = this[position.rowIndex][position.columnIndex]


        val endPosition = Position(map.indices.last, map[0].indices.last - 1)

        fun depthFirstSearch(visited: PersistentSet<Position>, current: Position): Int {
            return if (current == endPosition) {
                visited.size
            } else {
                val nextVisited = visited.add(current)
                when (map[current]) {
                    '>', '<', 'v', '^', '.' -> listOf(
                        current.goUp(),
                        current.goDown(),
                        current.goLeft(),
                        current.goRight(),
                    )

                    else -> never()
                }
                    .maxOf { nextPosition ->
                        if (
                            nextPosition.rowIndex in map.indices &&
                            nextPosition.columnIndex in map[0].indices &&
                            map[nextPosition] != '#' &&
                            visited.contains(nextPosition).not()
                        ) {
                            depthFirstSearch(visited = nextVisited, current = nextPosition)
                        } else {
                            0
                        }
                    }
            }
        }

        return depthFirstSearch(persistentSetOf(), Position(0, 1))
    }

    val testInput1 = readLines("Day23_1_test")
    check(part1(testInput1) == 94)
    check(part2(testInput1) == 154)
    val input = readLines("Day23")
    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    measureTimeMillis { part2(input).println() }.also { println("time: $it") }
}
