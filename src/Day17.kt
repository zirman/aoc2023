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
    startNodes: Set<Pair<T, Long>>,
    isEnd: T.() -> Boolean,
    nextNodes: Pair<T, Long>.() -> Set<Pair<T, Long>>,
): Pair<T, Long> {
    val visited = startNodes.associate { it }.toMutableMap()
    val queue = PriorityQueue(compareBy<Pair<T, Long>> { (_, cost) -> cost })
    queue.addAll(startNodes)

    tailrec fun recur(): Pair<T, Long> {
        val current = queue.poll()!!
        if (current.first.isEnd()) return current
        current
            .nextNodes()
            .filter { it.second < visited.getOrDefault(it.first, Long.MAX_VALUE) && queue.contains(it).not() }
            .run {
                queue.addAll(this)
                visited.putAll(this)
            }
        return recur()
    }

    return recur()
}

fun main() {
    fun part1(input: List<String>): Long {
        val map = input.map { line -> line.map { it.code - '0'.code } }

        fun Pair<Crucible, Long>.nextStart(): Set<Pair<Crucible, Long>> = buildSet {
            val (crucible, cooled) = this@nextStart

            with(crucible) {
                fun goNorth(stepsTaken: Int) {
                    if (rowIndex > 0) {
                        add(
                            Pair(
                                copy(
                                    rowIndex = rowIndex - 1,
                                    direction = North,
                                    stepsTaken = stepsTaken,
                                ),
                                cooled + map[rowIndex - 1][columnIndex],
                            )
                        )
                    }
                }

                fun goSouth(stepsTaken: Int) {
                    if (rowIndex < map.size - 1) {
                        add(
                            Pair(
                                copy(
                                    rowIndex = rowIndex + 1,
                                    direction = South,
                                    stepsTaken = stepsTaken,
                                ),
                                cooled + map[rowIndex + 1][columnIndex],
                            )
                        )
                    }
                }

                fun goEast(stepsTaken: Int) {
                    if (columnIndex < map[0].size - 1) {
                        add(
                            Pair(
                                copy(
                                    columnIndex = columnIndex + 1,
                                    direction = East,
                                    stepsTaken = stepsTaken,
                                ),
                                cooled + map[rowIndex][columnIndex + 1],
                            )
                        )
                    }
                }

                fun goWest(stepsTaken: Int) {
                    if (columnIndex > 0) {
                        add(
                            Pair(
                                copy(
                                    columnIndex = columnIndex - 1,
                                    direction = West,
                                    stepsTaken = stepsTaken,
                                ),
                                cooled + map[rowIndex][columnIndex - 1],
                            )
                        )
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
        }

        return aStar(
            startNodes = listOf(
                Crucible(rowIndex = 0, columnIndex = 0, direction = East, stepsTaken = 0),
                Crucible(rowIndex = 0, columnIndex = 0, direction = South, stepsTaken = 0),
            )
                .flatMap { Pair(it, 0L).nextStart() }
                .toSet(),
            isEnd = { rowIndex == map.size - 1 && columnIndex == map[0].size - 1 },
            nextNodes = { nextStart() },
        ).second
    }

    fun part2(input: List<String>): Long {
        val map = input.map { line -> line.map { it.code - '0'.code } }

        fun Pair<Crucible, Long>.nextStart(): Set<Pair<Crucible, Long>> = buildSet {
            val (crucible, cooled) = this@nextStart

            with(crucible) {
                fun goNorth(stepsTaken: Int) {
                    if (rowIndex > 0) {
                        add(
                            Pair(
                                copy(
                                    rowIndex = rowIndex - 1,
                                    direction = North,
                                    stepsTaken = stepsTaken,
                                ),
                                cooled + map[rowIndex - 1][columnIndex],
                            )
                        )
                    }
                }

                fun goSouth(stepsTaken: Int) {
                    if (rowIndex < map.size - 1) {
                        add(
                            Pair(
                                copy(
                                    rowIndex = rowIndex + 1,
                                    direction = South,
                                    stepsTaken = stepsTaken,
                                ),
                                cooled + map[rowIndex + 1][columnIndex],
                            )
                        )
                    }
                }

                fun goEast(stepsTaken: Int) {
                    if (columnIndex < map[0].size - 1) {
                        add(
                            Pair(
                                copy(
                                    columnIndex = columnIndex + 1,
                                    direction = East,
                                    stepsTaken = stepsTaken,
                                ),
                                cooled + map[rowIndex][columnIndex + 1],
                            )
                        )
                    }
                }

                fun goWest(stepsTaken: Int) {
                    if (columnIndex > 0) {
                        add(
                            Pair(
                                copy(
                                    columnIndex = columnIndex - 1,
                                    direction = West,
                                    stepsTaken = stepsTaken,
                                ),
                                cooled + map[rowIndex][columnIndex - 1],
                            )
                        )
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
        }

        return aStar(
            startNodes = listOf(
                Crucible(rowIndex = 0, columnIndex = 0, direction = East, stepsTaken = 0),
                Crucible(rowIndex = 0, columnIndex = 0, direction = South, stepsTaken = 0),
            )
                .flatMap { Pair(it, 0L).nextStart() }
                .toSet(),
            isEnd = { rowIndex == map.size - 1 && columnIndex == map[0].size - 1 },
            nextNodes = { nextStart() },
        ).second
    }

    val testInput1 = readLines("Day17_1_test")
    check(part1(testInput1) == 102L)
    check(part2(testInput1) == 94L)

    val input = readLines("Day17")
    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    measureTimeMillis { part2(input).println() }.also { println("time: $it") }
}
