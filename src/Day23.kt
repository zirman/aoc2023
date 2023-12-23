import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlin.system.measureTimeMillis

fun Position.goUp(): Position = copy(rowIndex = rowIndex - 1)
fun Position.goDown(): Position = copy(rowIndex = rowIndex + 1)
fun Position.goLeft(): Position = copy(columnIndex = columnIndex - 1)
fun Position.goRight(): Position = copy(columnIndex = columnIndex + 1)

data class Day23Arguments(val start: Position, val previous: Position, val current: Position, val length: Int)

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
        operator fun List<List<Char>>.get(position: Position): Char = this[position.rowIndex][position.columnIndex]
        operator fun List<List<Char>>.get(ri: Int, ci: Int): Char = this[ri][ci]

        val map = input.map { line -> line.toList() }.let { m ->
            m.mapIndexed { ri, r ->
                r.mapIndexed { ci, c ->
                    if (c != '#') {
                        listOfNotNull(
                            m.getOrNull(ri - 1)?.getOrNull(ci)?.takeIf { it != '#' },
                            m.getOrNull(ri + 1)?.getOrNull(ci)?.takeIf { it != '#' },
                            m.getOrNull(ri)?.getOrNull(ci - 1)?.takeIf { it != '#' },
                            m.getOrNull(ri)?.getOrNull(ci + 1)?.takeIf { it != '#' },
                        )
                            .count()
                            .digitToChar()
                    } else {
                        '#'
                    }
                }
            }
        }

        val startPosition = Position(0, 1)
        val endPosition = Position(map.indices.last, map[0].indices.last - 1)

        val fastMap = buildMap<Position, List<Pair<Position, Int>>> {
            val visited = mutableSetOf(startPosition)

            val buildPaths = DeepRecursiveFunction { arguments: Day23Arguments ->
                val (start: Position, previous: Position, current: Position, length: Int) = arguments
                val endPath = map[current] != '2'

                if (endPath) {
                    compute(start) { _, paths -> paths.orEmpty() + Pair(current, length) }
                    compute(current) { _, paths -> paths.orEmpty() + Pair(start, length) }
                }

                if (visited.contains(current).not()) {
                    visited.add(current)

                    listOf(
                        current.goUp(),
                        current.goDown(),
                        current.goLeft(),
                        current.goRight(),
                    )
                        .filter { nextPosition ->
                            nextPosition != previous &&
                                    nextPosition.rowIndex in map.indices &&
                                    nextPosition.columnIndex in map[0].indices &&
                                    map[nextPosition] != '#'
                        }
                        .forEach { next ->
                            callRecursive(
                                Day23Arguments(
                                    start = if (endPath) current else start,
                                    previous = current,
                                    current = next,
                                    length = if (endPath) 1 else length + 1,
                                )
                            )
                        }
                }
            }

            buildPaths(Day23Arguments(startPosition, startPosition, Position(1, 1), 1))
        }

        fastMap.forEach { println(it) }

        fun depthFirstSearch(visited: PersistentSet<Position>, currentPath: Pair<Position, Int>, length: Int): Int {
            val (pathStart, pathLength) = currentPath
            if (pathStart == endPosition) return length + pathLength
            if (visited.contains(pathStart)) return 0
            val nextVisited = visited.add(pathStart)
            return fastMap[pathStart]!!.maxOf { nextFoo -> depthFirstSearch(nextVisited, nextFoo, length + pathLength) }
        }

        return depthFirstSearch(visited = persistentSetOf(), Pair(Position(0, 1), 0), 0)
    }

    val testInput1 = readLines("Day23_1_test")
    check(part1(testInput1) == 94)
    check(part2(testInput1) == 154)
    val input = readLines("Day23")
    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    measureTimeMillis { part2(input).println() }.also { println("time: $it") }
}
