import Direction.*
import java.util.PriorityQueue

data class Crucible(
    val rowIndex: Int,
    val columnIndex: Int,
    val direction: Direction,
    val stepsTaken: Int,
    val cooled: Long,
)

fun <T> Set<T>.aStar(
    comparator: Comparator<T>,
    finished: T.() -> Boolean,
    next: T.() -> List<T>
): T {
    val queue = PriorityQueue(comparator)
    queue.addAll(this)

    tailrec fun recur(): T {
        val n = queue.poll()!!
//        if (n.cooled > 60) throw IllegalStateException()
        if (n.finished()) return n
        queue.addAll(n.next())
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

        fun Crucible.next(): List<Crucible> = buildList {
//            println("${rowIndex + columnIndex} ${cooled}")
            val f = 0
            fun goNorth(stepsTaken: Int) {
                if (rowIndex > 0) {
                    val k = Triple(rowIndex - 1, columnIndex, stepsTaken)
                    val c = cooled + map[rowIndex - 1][columnIndex]
                    val q = visitedNorth[k]
                    if (q == null || q + f >= c) {
                        visitedNorth[k] = c
                        add(
                            copy(
                                rowIndex = rowIndex - 1,
                                direction = North,
                                stepsTaken = stepsTaken,
                                cooled = c,
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
                    if (q == null || q + f >= c) {
                        visitedSouth[k] = c
                        add(
                            copy(
                                rowIndex = rowIndex + 1,
                                direction = South,
                                stepsTaken = stepsTaken,
                                cooled = c,
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
                    if (q == null || q + f >= c) {
                        visitedEast[k] = c
                        add(
                            copy(
                                columnIndex = columnIndex + 1,
                                direction = East,
                                stepsTaken = stepsTaken,
                                cooled = c,
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
                    if (q == null || q + f >= c) {
                        visitedWest[k] = c
                        add(
                            copy(
                                columnIndex = columnIndex - 1,
                                direction = West,
                                stepsTaken = stepsTaken,
                                cooled = c,
                            )
                        )
                    }
                }
            }

            when (direction) {
                North -> {
                    goWest(1)
                    goEast(1)
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

        listOf(
            Crucible(rowIndex = 0, columnIndex = 0, direction = East, stepsTaken = 0, cooled = 0),
            Crucible(rowIndex = 0, columnIndex = 0, direction = South, stepsTaken = 0, cooled = 0),
        )
            .flatMap { it.next() }
            .toSet()
            .aStar(
                comparator = compareBy {
                    it.cooled + 4 * ((map.size - 1 - it.rowIndex) + (map[0].size - 1 - it.columnIndex))
                },
                finished = { rowIndex == map.size - 1 && columnIndex == map[0].size - 1 },
                next = { next() },
            )
            .run { return cooled }
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readLines("Day17_1_test")
    check(part1(testInput1).also { println(it) } == 102L)

//    val testInput2 = readInput("Day17_2_test")
//    check(part2(testInput2) == 1)

    val input = readLines("Day17")
    part1(input).println()
//    part2(input).println()
}
