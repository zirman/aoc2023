import Direction.*
import java.util.PriorityQueue
import kotlin.system.measureTimeMillis

data class Crucible(
    val rowIndex: Int,
    val columnIndex: Int,
    val direction: Direction,
    val stepsTaken: Int,
)

fun <T> aStar(
    startNodes: Set<T>,
    comparator: Comparator<T>,
    isEnd: T.() -> Boolean,
    nextNodes: T.() -> Set<T>,
): T {
    val queue = PriorityQueue(comparator)
    queue.addAll(startNodes)

    tailrec fun recur(): T {
        val next = queue.poll()!!
        if (next.isEnd()) return next
        queue.addAll(next.nextNodes().filter { queue.contains(it).not() })
        return recur()
    }

    return recur()
}

fun main() {
    fun part1(input: List<String>): Long {
        val map = input.map { line -> line.map { it.code - '0'.code } }
        val visitedNorth = mutableMapOf<Triple<Int, Int, Int>, Long>()
        val visitedSouth = mutableMapOf<Triple<Int, Int, Int>, Long>()
        val visitedEast = mutableMapOf<Triple<Int, Int, Int>, Long>()
        val visitedWest = mutableMapOf<Triple<Int, Int, Int>, Long>()

        fun Crucible.nextStart(cooled: Long): Set<Pair<Crucible, Long>> = buildSet {
            fun goNorth(stepsTaken: Int) {
                if (rowIndex > 0) {
                    val k = Triple(rowIndex - 1, columnIndex, stepsTaken)
                    val c = cooled + map[rowIndex - 1][columnIndex]
                    val q = visitedNorth[k]
                    if (q == null || q > c) {
                        visitedNorth[k] = c
                        add(
                            Pair(
                                copy(
                                    rowIndex = rowIndex - 1,
                                    direction = North,
                                    stepsTaken = stepsTaken,
                                ),
                                c,
                            )
                        )
                    }
                }
            }

            fun goSouth(stepsTaken: Int) {
                if (rowIndex < map.size - 1) {
                    val k = Triple(rowIndex + 1, columnIndex, stepsTaken)
                    val c = cooled + map[rowIndex + 1][columnIndex]
                    val q = visitedSouth[k]
                    if (q == null || q > c) {
                        visitedSouth[k] = c
                        add(
                            Pair(
                                copy(
                                    rowIndex = rowIndex + 1,
                                    direction = South,
                                    stepsTaken = stepsTaken,
                                ),
                                c,
                            )
                        )
                    }
                }
            }

            fun goEast(stepsTaken: Int) {
                if (columnIndex < map[0].size - 1) {
                    val k = Triple(rowIndex, columnIndex + 1, stepsTaken)
                    val c = cooled + map[rowIndex][columnIndex + 1]
                    val q = visitedEast[k]
                    if (q == null || q > c) {
                        visitedEast[k] = c
                        add(
                            Pair(
                                copy(
                                    columnIndex = columnIndex + 1,
                                    direction = East,
                                    stepsTaken = stepsTaken,
                                ),
                                c,
                            )
                        )
                    }
                }
            }

            fun goWest(stepsTaken: Int) {
                if (columnIndex > 0) {
                    val k = Triple(rowIndex, columnIndex - 1, stepsTaken)
                    val c = cooled + map[rowIndex][columnIndex - 1]
                    val q = visitedWest[k]
                    if (q == null || q > c) {
                        visitedWest[k] = c
                        add(
                            Pair(
                                copy(
                                    columnIndex = columnIndex - 1,
                                    direction = West,
                                    stepsTaken = stepsTaken,
                                ),
                                c,
                            )
                        )
                    }
                }
            }

            when (direction) {
                North -> {
                    goEast(1)
                    goWest(1)
                    if (stepsTaken < 3) goNorth(stepsTaken + 1)
                }

                West -> {
                    goNorth(1)
                    goSouth(1)
                    if (stepsTaken < 3) goWest(stepsTaken + 1)
                }

                East -> {
                    goNorth(1)
                    goSouth(1)
                    if (stepsTaken < 3) goEast(stepsTaken + 1)
                }

                South -> {
                    goEast(1)
                    goWest(1)
                    if (stepsTaken < 3) goSouth(stepsTaken + 1)
                }
            }
        }

        return aStar(
            startNodes = listOf(
                Crucible(rowIndex = 0, columnIndex = 0, direction = East, stepsTaken = 0),
                Crucible(rowIndex = 0, columnIndex = 0, direction = South, stepsTaken = 0),
            )
                .flatMap { it.nextStart(0) }
                .toSet(),
            comparator = compareBy { (_, cooled) -> cooled },
            isEnd = { first.rowIndex == map.size - 1 && first.columnIndex == map[0].size - 1 },
            nextNodes = { first.nextStart(second) },
        ).second
    }

    fun part2(input: List<String>): Long {
        val map = input.map { line -> line.map { it.code - '0'.code } }
        val visitedNorth = mutableMapOf<Triple<Int, Int, Int>, Long>()
        val visitedSouth = mutableMapOf<Triple<Int, Int, Int>, Long>()
        val visitedEast = mutableMapOf<Triple<Int, Int, Int>, Long>()
        val visitedWest = mutableMapOf<Triple<Int, Int, Int>, Long>()

        fun Crucible.nextStart(cooled: Long): Set<Pair<Crucible, Long>> = buildSet {
            fun goNorth(stepsTaken: Int) {
                if (rowIndex > 0) {
                    val k = Triple(rowIndex - 1, columnIndex, stepsTaken)
                    val c = cooled + map[rowIndex - 1][columnIndex]
                    val q = visitedNorth[k]
                    if (q == null || q > c) {
                        visitedNorth[k] = c
                        add(
                            Pair(
                                copy(
                                    rowIndex = rowIndex - 1,
                                    direction = North,
                                    stepsTaken = stepsTaken,
                                ),
                                c,
                            )
                        )
                    }
                }
            }

            fun goSouth(stepsTaken: Int) {
                if (rowIndex < map.size - 1) {
                    val k = Triple(rowIndex + 1, columnIndex, stepsTaken)
                    val c = cooled + map[rowIndex + 1][columnIndex]
                    val q = visitedSouth[k]
                    if (q == null || q > c) {
                        visitedSouth[k] = c
                        add(
                            Pair(
                                copy(
                                    rowIndex = rowIndex + 1,
                                    direction = South,
                                    stepsTaken = stepsTaken,
                                ),
                                c,
                            )
                        )
                    }
                }
            }

            fun goEast(stepsTaken: Int) {
                if (columnIndex < map[0].size - 1) {
                    val k = Triple(rowIndex, columnIndex + 1, stepsTaken)
                    val c = cooled + map[rowIndex][columnIndex + 1]
                    val q = visitedEast[k]
                    if (q == null || q > c) {
                        visitedEast[k] = c
                        add(
                            Pair(
                                copy(
                                    columnIndex = columnIndex + 1,
                                    direction = East,
                                    stepsTaken = stepsTaken,
                                ),
                                c,
                            )
                        )
                    }
                }
            }

            fun goWest(stepsTaken: Int) {
                if (columnIndex > 0) {
                    val k = Triple(rowIndex, columnIndex - 1, stepsTaken)
                    val c = cooled + map[rowIndex][columnIndex - 1]
                    val q = visitedWest[k]
                    if (q == null || q > c) {
                        visitedWest[k] = c
                        add(
                            Pair(
                                copy(
                                    columnIndex = columnIndex - 1,
                                    direction = West,
                                    stepsTaken = stepsTaken,
                                ),
                                c,
                            )
                        )
                    }
                }
            }

            when (direction) {
                North -> {
                    if (stepsTaken < 4) {
                        goNorth(stepsTaken + 1)
                    } else {
                        goEast(1)
                        goWest(1)
                        if (stepsTaken < 10) goNorth(stepsTaken + 1)
                    }
                }

                West -> {
                    if (stepsTaken < 4) {
                        goWest(stepsTaken + 1)
                    } else {
                        goNorth(1)
                        goSouth(1)
                        if (stepsTaken < 10) goWest(stepsTaken + 1)
                    }
                }

                East -> {
                    if (stepsTaken < 4) {
                        goEast(stepsTaken + 1)
                    } else {
                        goNorth(1)
                        goSouth(1)
                        if (stepsTaken < 10) goEast(stepsTaken + 1)
                    }
                }

                South -> {
                    if (stepsTaken < 4) {
                        goSouth(stepsTaken + 1)
                    } else {
                        goEast(1)
                        goWest(1)
                        if (stepsTaken < 10) goSouth(stepsTaken + 1)
                    }
                }
            }
        }

        return aStar(
            startNodes = listOf(
                Crucible(rowIndex = 0, columnIndex = 0, direction = East, stepsTaken = 0),
                Crucible(rowIndex = 0, columnIndex = 0, direction = South, stepsTaken = 0),
            )
                .flatMap { it.nextStart(0) }
                .toSet(),
            comparator = compareBy { (_, cooled) -> cooled },
            isEnd = { first.rowIndex == map.size - 1 && first.columnIndex == map[0].size - 1 },
            nextNodes = { first.nextStart(second) },
        ).second
    }

    val testInput1 = readLines("Day17_1_test")
    check(part1(testInput1) == 102L)
    check(part2(testInput1) == 94L)

    val input = readLines("Day17")
    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    measureTimeMillis { part2(input).println() }.also { println("time: $it") }
}
