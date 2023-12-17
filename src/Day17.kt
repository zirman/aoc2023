import Direction.*
import java.util.PriorityQueue
import kotlin.system.measureTimeMillis

data class Crucible(
    val rowIndex: Int,
    val columnIndex: Int,
    val direction: Direction,
    val stepsTaken: Int,
)

fun aStar(
    start: Map<Crucible, Long>,
    end: Map<Crucible, Long>,
    comparatorStart: Comparator<Pair<Crucible, Long>>,
    comparatorEnd: Comparator<Pair<Crucible, Long>>,
    nextStart: Crucible.(Long) -> Set<Pair<Crucible, Long>>,
    nextEnd: Crucible.(Long) -> Set<Pair<Crucible, Long>>,
): Long {
//    end.forEach { println("${it}") }
//    println()
    val startLeafs = start.toMutableMap()
    val startQueue = PriorityQueue(comparatorStart)
    startQueue.addAll(start.map { (k, v) -> Pair(k, v) })

    val endLeafs = end.toMutableMap()
    val endQueue = PriorityQueue(comparatorEnd)
    endQueue.addAll(end.map { (k, v) -> Pair(k, v) })

    tailrec fun recur(): Long {
//        println("${startQueue.size} ${endQueue.size}")
        if (true) { //startQueue.size < endQueue.size) {
            val (startNext, startLeafCooling) = startQueue.poll()!!
//            println("${startNext} ${startLeafCooling}")
            startLeafs.remove(startNext)!!

            endLeafs[startNext]?.run {
//                println("w ${startNext} ${startLeafCooling}")
                return this + startLeafCooling
            }

            val nextStarts = startNext.nextStart(startLeafCooling).filter { startQueue.contains(it).not() }
            startQueue.addAll(nextStarts)
            startLeafs.putAll(nextStarts)
        } else {
            val (endNext, endLeafCooling) = endQueue.poll()!!
            endLeafs.remove(endNext) ?: run {
//                println(endNext)
                throw IllegalStateException()
            }

            startLeafs[endNext]?.run {
                return this + endLeafCooling
            }

            val nextEnds = endNext.nextEnd(endLeafCooling).filter { endQueue.contains(it).not() }
            endQueue.addAll(nextEnds)
            endLeafs.putAll(nextEnds)
        }
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
                    if (q == null || q >= c) {
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
                    if (q == null || q >= c) {
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
                    if (q == null || q >= c) {
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
                    if (q == null || q >= c) {
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

        val visitedBackwardsNorth = mutableMapOf<Triple<Int, Int, Int>, Long>()
        val visitedBackwardsSouth = mutableMapOf<Triple<Int, Int, Int>, Long>()
        val visitedBackwardsEast = mutableMapOf<Triple<Int, Int, Int>, Long>()
        val visitedBackwardsWest = mutableMapOf<Triple<Int, Int, Int>, Long>()

        fun Crucible.nextEnd(cooled: Long): Set<Pair<Crucible, Long>> = buildSet {
            fun goBackwardsNorth(stepsTaken: Int, direction: Direction = North) {
                if (rowIndex < map.size - 1) {
                    val k = Triple(rowIndex + 1, columnIndex, stepsTaken)
                    val c = cooled + map[rowIndex][columnIndex]
                    val q = visitedBackwardsNorth[k]
                    if (q == null || q >= c) {
                        visitedBackwardsNorth[k] = c
                        add(
                            Pair(
                                copy(
                                    rowIndex = rowIndex + 1,
                                    direction = direction,
                                    stepsTaken = stepsTaken,
                                ),
                                c,
                            )
                        )
                    }
                }
            }

            fun goBackwardsSouth(stepsTaken: Int, direction: Direction = South) {
                if (rowIndex > 0) {
                    val k = Triple(rowIndex - 1, columnIndex, stepsTaken)
                    val c = cooled + map[rowIndex][columnIndex]
                    val q = visitedBackwardsSouth[k]
                    if (q == null || q >= c) {
                        visitedBackwardsSouth[k] = c
                        add(
                            Pair(
                                copy(
                                    rowIndex = rowIndex - 1,
                                    direction = direction,
                                    stepsTaken = stepsTaken,
                                ),
                                c,
                            )
                        )
                    }
                }
            }

            fun goBackwardsEast(stepsTaken: Int, direction: Direction = East) {
                if (columnIndex > 0) {
                    val k = Triple(rowIndex, columnIndex - 1, stepsTaken)
                    val c = cooled + map[rowIndex][columnIndex]
                    val q = visitedBackwardsEast[k]
                    if (q == null || q >= c) {
                        visitedBackwardsEast[k] = c
                        add(
                            Pair(
                                copy(
                                    columnIndex = columnIndex - 1,
                                    direction = direction,
                                    stepsTaken = stepsTaken,
                                ),
                                c,
                            )
                        )
                    }
                }
            }

            fun goBackwardsWest(stepsTaken: Int, direction: Direction = West) {
                if (columnIndex < map[0].size - 1) {
                    val k = Triple(rowIndex, columnIndex + 1, stepsTaken)
                    val c = cooled + map[rowIndex][columnIndex]
                    val q = visitedBackwardsWest[k]
                    if (q == null || q >= c) {
                        visitedBackwardsWest[k] = c
                        add(
                            Pair(
                                copy(
                                    columnIndex = columnIndex + 1,
                                    direction = direction,
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
                    if (stepsTaken == 1) {
                        goBackwardsEast(1)
                        goBackwardsEast(1, North)
                        goBackwardsEast(1, South)
                        goBackwardsEast(2)
                        goBackwardsEast(2, North)
                        goBackwardsEast(2, South)
                        goBackwardsEast(3, North)
                        goBackwardsEast(3, South)

                        goBackwardsWest(1)
                        goBackwardsWest(1, North)
                        goBackwardsWest(1, South)
                        goBackwardsWest(2)
                        goBackwardsWest(2, North)
                        goBackwardsWest(2, South)
                        goBackwardsWest(3, North)
                        goBackwardsWest(3, South)
                    } else if (stepsTaken == 2) {
                        goBackwardsNorth(1)
                    } else if (stepsTaken == 3) {
                        goBackwardsNorth(2)
                    }
                }

                West -> {
                    when (stepsTaken) {
                        1 -> {
                            goBackwardsNorth(1)
                            goBackwardsNorth(1, East)
                            goBackwardsNorth(1, West)
                            goBackwardsNorth(2)
                            goBackwardsNorth(2, East)
                            goBackwardsNorth(2, West)
                            goBackwardsNorth(3, East)
                            goBackwardsNorth(3, West)

                            goBackwardsSouth(1)
                            goBackwardsSouth(1, East)
                            goBackwardsSouth(1, West)
                            goBackwardsSouth(2)
                            goBackwardsSouth(2, East)
                            goBackwardsSouth(2, West)
                            goBackwardsSouth(3, East)
                            goBackwardsSouth(3, West)
                        }

                        2 -> {
                            goBackwardsWest(1)
                        }

                        3 -> {
                            goBackwardsWest(2)
                        }
                    }
                }

                East -> {
                    when (stepsTaken) {
                        1 -> {
                            goBackwardsNorth(1)
                            goBackwardsNorth(1, East)
                            goBackwardsNorth(1, West)
                            goBackwardsNorth(2)
                            goBackwardsNorth(2, East)
                            goBackwardsNorth(2, West)
                            goBackwardsNorth(3, East)
                            goBackwardsNorth(3, West)

                            goBackwardsSouth(1)
                            goBackwardsSouth(1, East)
                            goBackwardsSouth(1, West)
                            goBackwardsSouth(2)
                            goBackwardsSouth(2, East)
                            goBackwardsSouth(2, West)
                            goBackwardsSouth(3, East)
                            goBackwardsSouth(3, West)
                        }

                        2 -> {
                            goBackwardsEast(1)
                        }

                        3 -> {
                            goBackwardsEast(2)
                        }
                    }
                }

                South -> {
                    if (stepsTaken == 1) {
                        goBackwardsEast(1)
                        goBackwardsEast(1, North)
                        goBackwardsEast(1, South)
                        goBackwardsEast(2)
                        goBackwardsEast(2, North)
                        goBackwardsEast(2, South)
                        goBackwardsEast(3, North)
                        goBackwardsEast(3, South)

                        goBackwardsWest(1)
                        goBackwardsWest(1, North)
                        goBackwardsWest(1, South)
                        goBackwardsWest(2)
                        goBackwardsWest(2, North)
                        goBackwardsWest(2, South)
                        goBackwardsWest(3, North)
                        goBackwardsWest(3, South)
                    } else if (stepsTaken == 2) {
                        goBackwardsSouth(1)
                    } else if (stepsTaken == 3) {
                        goBackwardsSouth(2)
                    }
                }
            }
        }

        aStar(
            start = listOf(
                Crucible(rowIndex = 0, columnIndex = 0, direction = East, stepsTaken = 0),
                Crucible(rowIndex = 0, columnIndex = 0, direction = South, stepsTaken = 0),
            )
                .flatMap { it.nextStart(0) }
                .associate { it },
            end = listOf(
                Crucible(
                    rowIndex = map.size - 1,
                    columnIndex = map[0].size - 1,
                    direction = East,
                    stepsTaken = 1,
                ),
                Crucible(
                    rowIndex = map.size - 1,
                    columnIndex = map[0].size - 1,
                    direction = East,
                    stepsTaken = 2,
                ),
                Crucible(
                    rowIndex = map.size - 1,
                    columnIndex = map[0].size - 1,
                    direction = East,
                    stepsTaken = 3,
                ),
                Crucible(
                    rowIndex = map.size - 1,
                    columnIndex = map[0].size - 1,
                    direction = South,
                    stepsTaken = 1,
                ),
                Crucible(
                    rowIndex = map.size - 1,
                    columnIndex = map[0].size - 1,
                    direction = South,
                    stepsTaken = 2,
                ),
                Crucible(
                    rowIndex = map.size - 1,
                    columnIndex = map[0].size - 1,
                    direction = South,
                    stepsTaken = 3,
                ),
            )
                .flatMap { it.nextEnd(0) }
                .associate { it },
            comparatorStart = compareBy { (_, cooled) -> cooled },
            comparatorEnd = compareBy { (_, cooled) -> cooled },
            nextStart = { nextStart(it) },
            nextEnd = { nextEnd(it) },
        ).run { return this }
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
                    if (q == null || q >= c) {
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
                    if (q == null || q >= c) {
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
                    if (q == null || q >= c) {
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
                    if (q == null || q >= c) {
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
                    if (stepsTaken < 10) goNorth(stepsTaken + 1)
                }

                West -> {
                    goNorth(1)
                    goSouth(1)
                    if (stepsTaken < 10) goWest(stepsTaken + 1)
                }

                East -> {
                    goNorth(1)
                    goSouth(1)
                    if (stepsTaken < 10) goEast(stepsTaken + 1)
                }

                South -> {
                    goEast(1)
                    goWest(1)
                    if (stepsTaken < 10) goSouth(stepsTaken + 1)
                }
            }
        }

        val visitedBackwardsNorth = mutableMapOf<Triple<Int, Int, Int>, Long>()
        val visitedBackwardsSouth = mutableMapOf<Triple<Int, Int, Int>, Long>()
        val visitedBackwardsEast = mutableMapOf<Triple<Int, Int, Int>, Long>()
        val visitedBackwardsWest = mutableMapOf<Triple<Int, Int, Int>, Long>()

        fun Crucible.nextEnd(cooled: Long): Set<Pair<Crucible, Long>> = buildSet {
            fun goBackwardsNorth(stepsTaken: Int, direction: Direction = North) {
                if (rowIndex < map.size - 1) {
                    val k = Triple(rowIndex + 1, columnIndex, stepsTaken)
                    val c = cooled + map[rowIndex][columnIndex]
                    val q = visitedBackwardsNorth[k]
                    if (q == null || q >= c) {
                        visitedBackwardsNorth[k] = c
                        add(
                            Pair(
                                copy(
                                    rowIndex = rowIndex + 1,
                                    direction = direction,
                                    stepsTaken = stepsTaken,
                                ),
                                c,
                            )
                        )
                    }
                }
            }

            fun goBackwardsSouth(stepsTaken: Int, direction: Direction = South) {
                if (rowIndex > 0) {
                    val k = Triple(rowIndex - 1, columnIndex, stepsTaken)
                    val c = cooled + map[rowIndex][columnIndex]
                    val q = visitedBackwardsSouth[k]
                    if (q == null || q >= c) {
                        visitedBackwardsSouth[k] = c
                        add(
                            Pair(
                                copy(
                                    rowIndex = rowIndex - 1,
                                    direction = direction,
                                    stepsTaken = stepsTaken,
                                ),
                                c,
                            )
                        )
                    }
                }
            }

            fun goBackwardsEast(stepsTaken: Int, direction: Direction = East) {
                if (columnIndex > 0) {
                    val k = Triple(rowIndex, columnIndex - 1, stepsTaken)
                    val c = cooled + map[rowIndex][columnIndex]
                    val q = visitedBackwardsEast[k]
                    if (q == null || q >= c) {
                        visitedBackwardsEast[k] = c
                        add(
                            Pair(
                                copy(
                                    columnIndex = columnIndex - 1,
                                    direction = direction,
                                    stepsTaken = stepsTaken,
                                ),
                                c,
                            )
                        )
                    }
                }
            }

            fun goBackwardsWest(stepsTaken: Int, direction: Direction = West) {
                if (columnIndex < map[0].size - 1) {
                    val k = Triple(rowIndex, columnIndex + 1, stepsTaken)
                    val c = cooled + map[rowIndex][columnIndex]
                    val q = visitedBackwardsWest[k]
                    if (q == null || q >= c) {
                        visitedBackwardsWest[k] = c
                        add(
                            Pair(
                                copy(
                                    columnIndex = columnIndex + 1,
                                    direction = direction,
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
                    if (stepsTaken == 1) {
                        goBackwardsEast(1)
                        goBackwardsEast(1, North)
                        goBackwardsEast(1, South)
                        goBackwardsEast(2)
                        goBackwardsEast(2, North)
                        goBackwardsEast(2, South)
                        goBackwardsEast(3)
                        goBackwardsEast(3, North)
                        goBackwardsEast(3, South)
                        goBackwardsEast(4)
                        goBackwardsEast(4, North)
                        goBackwardsEast(4, South)
                        goBackwardsEast(5)
                        goBackwardsEast(5, North)
                        goBackwardsEast(5, South)
                        goBackwardsEast(6)
                        goBackwardsEast(6, North)
                        goBackwardsEast(6, South)
                        goBackwardsEast(7)
                        goBackwardsEast(7, North)
                        goBackwardsEast(7, South)
                        goBackwardsEast(8)
                        goBackwardsEast(8, North)
                        goBackwardsEast(8, South)
                        goBackwardsEast(9)
                        goBackwardsEast(9, North)
                        goBackwardsEast(9, South)
                        goBackwardsEast(10, North)
                        goBackwardsEast(10, South)

                        goBackwardsWest(1)
                        goBackwardsWest(1, North)
                        goBackwardsWest(1, South)
                        goBackwardsWest(2)
                        goBackwardsWest(2, North)
                        goBackwardsWest(2, South)

                        goBackwardsWest(3)
                        goBackwardsWest(3, North)
                        goBackwardsWest(3, South)

                        goBackwardsWest(4)
                        goBackwardsWest(4, North)
                        goBackwardsWest(4, South)

                        goBackwardsWest(5)
                        goBackwardsWest(5, North)
                        goBackwardsWest(5, South)

                        goBackwardsWest(6)
                        goBackwardsWest(6, North)
                        goBackwardsWest(6, South)

                        goBackwardsWest(7)
                        goBackwardsWest(7, North)
                        goBackwardsWest(7, South)

                        goBackwardsWest(8)
                        goBackwardsWest(8, North)
                        goBackwardsWest(8, South)

                        goBackwardsWest(9)
                        goBackwardsWest(9, North)
                        goBackwardsWest(9, South)

                        goBackwardsWest(10, North)
                        goBackwardsWest(10, South)
                    } else if (stepsTaken == 2) {
                        goBackwardsNorth(1)
                    } else if (stepsTaken == 3) {
                        goBackwardsNorth(2)
                    }
                }

                West -> {
                    when (stepsTaken) {
                        1 -> {
                            goBackwardsNorth(1)
                            goBackwardsNorth(1, East)
                            goBackwardsNorth(1, West)
                            goBackwardsNorth(2)
                            goBackwardsNorth(2, East)
                            goBackwardsNorth(2, West)

                            goBackwardsNorth(3)
                            goBackwardsNorth(3, East)
                            goBackwardsNorth(3, West)

                            goBackwardsNorth(4)
                            goBackwardsNorth(4, East)
                            goBackwardsNorth(4, West)

                            goBackwardsNorth(5)
                            goBackwardsNorth(5, East)
                            goBackwardsNorth(5, West)

                            goBackwardsNorth(6)
                            goBackwardsNorth(6, East)
                            goBackwardsNorth(6, West)

                            goBackwardsNorth(7)
                            goBackwardsNorth(7, East)
                            goBackwardsNorth(7, West)

                            goBackwardsNorth(8)
                            goBackwardsNorth(8, East)
                            goBackwardsNorth(8, West)

                            goBackwardsNorth(9)
                            goBackwardsNorth(9, East)
                            goBackwardsNorth(9, West)

                            goBackwardsNorth(10, East)
                            goBackwardsNorth(10, West)

                            goBackwardsSouth(1)
                            goBackwardsSouth(1, East)
                            goBackwardsSouth(1, West)
                            goBackwardsSouth(2)
                            goBackwardsSouth(2, East)
                            goBackwardsSouth(2, West)

                            goBackwardsSouth(3)
                            goBackwardsSouth(3, East)
                            goBackwardsSouth(3, West)

                            goBackwardsSouth(4)
                            goBackwardsSouth(4, East)
                            goBackwardsSouth(4, West)

                            goBackwardsSouth(5)
                            goBackwardsSouth(5, East)
                            goBackwardsSouth(5, West)

                            goBackwardsSouth(6)
                            goBackwardsSouth(6, East)
                            goBackwardsSouth(6, West)

                            goBackwardsSouth(7)
                            goBackwardsSouth(7, East)
                            goBackwardsSouth(7, West)

                            goBackwardsSouth(8)
                            goBackwardsSouth(8, East)
                            goBackwardsSouth(8, West)

                            goBackwardsSouth(9)
                            goBackwardsSouth(9, East)
                            goBackwardsSouth(9, West)

                            goBackwardsSouth(10, East)
                            goBackwardsSouth(10, West)
                        }

                        2 -> {
                            goBackwardsWest(1)
                        }

                        3 -> {
                            goBackwardsWest(2)
                        }
                    }
                }

                East -> {
                    when (stepsTaken) {
                        1 -> {
                            goBackwardsNorth(1)
                            goBackwardsNorth(1, East)
                            goBackwardsNorth(1, West)
                            goBackwardsNorth(2)
                            goBackwardsNorth(2, East)
                            goBackwardsNorth(2, West)

                            goBackwardsNorth(3)
                            goBackwardsNorth(3, East)
                            goBackwardsNorth(3, West)
                            goBackwardsNorth(4)
                            goBackwardsNorth(4, East)
                            goBackwardsNorth(4, West)
                            goBackwardsNorth(5)
                            goBackwardsNorth(5, East)
                            goBackwardsNorth(5, West)
                            goBackwardsNorth(6)
                            goBackwardsNorth(6, East)
                            goBackwardsNorth(6, West)
                            goBackwardsNorth(7)
                            goBackwardsNorth(7, East)
                            goBackwardsNorth(7, West)
                            goBackwardsNorth(8)
                            goBackwardsNorth(8, East)
                            goBackwardsNorth(8, West)
                            goBackwardsNorth(9)
                            goBackwardsNorth(9, East)
                            goBackwardsNorth(9, West)
                            goBackwardsNorth(10, East)
                            goBackwardsNorth(10, West)

                            goBackwardsSouth(1)
                            goBackwardsSouth(1, East)
                            goBackwardsSouth(1, West)
                            goBackwardsSouth(2)
                            goBackwardsSouth(2, East)
                            goBackwardsSouth(2, West)

                            goBackwardsSouth(3)
                            goBackwardsSouth(3, East)
                            goBackwardsSouth(3, West)
                            goBackwardsSouth(4)
                            goBackwardsSouth(4, East)
                            goBackwardsSouth(4, West)
                            goBackwardsSouth(5)
                            goBackwardsSouth(5, East)
                            goBackwardsSouth(5, West)
                            goBackwardsSouth(6)
                            goBackwardsSouth(6, East)
                            goBackwardsSouth(6, West)
                            goBackwardsSouth(7)
                            goBackwardsSouth(7, East)
                            goBackwardsSouth(7, West)
                            goBackwardsSouth(8)
                            goBackwardsSouth(8, East)
                            goBackwardsSouth(8, West)
                            goBackwardsSouth(9)
                            goBackwardsSouth(9, East)
                            goBackwardsSouth(9, West)
                            goBackwardsSouth(10, East)
                            goBackwardsSouth(10, West)
                        }

                        2 -> {
                            goBackwardsEast(1)
                        }

                        3 -> {
                            goBackwardsEast(2)
                        }
                    }
                }

                South -> {
                    if (stepsTaken == 1) {
                        goBackwardsEast(1)
                        goBackwardsEast(1, North)
                        goBackwardsEast(1, South)
                        goBackwardsEast(2)
                        goBackwardsEast(2, North)
                        goBackwardsEast(2, South)

                        goBackwardsEast(3)
                        goBackwardsEast(3, North)
                        goBackwardsEast(3, South)

                        goBackwardsEast(4)
                        goBackwardsEast(4, North)
                        goBackwardsEast(4, South)

                        goBackwardsEast(5)
                        goBackwardsEast(5, North)
                        goBackwardsEast(5, South)

                        goBackwardsEast(6)
                        goBackwardsEast(6, North)
                        goBackwardsEast(6, South)

                        goBackwardsEast(7)
                        goBackwardsEast(7, North)
                        goBackwardsEast(7, South)

                        goBackwardsEast(8)
                        goBackwardsEast(8, North)
                        goBackwardsEast(8, South)

                        goBackwardsEast(9)
                        goBackwardsEast(9, North)
                        goBackwardsEast(9, South)

                        goBackwardsEast(10, North)
                        goBackwardsEast(10, South)

                        goBackwardsWest(1)
                        goBackwardsWest(1, North)
                        goBackwardsWest(1, South)
                        goBackwardsWest(2)
                        goBackwardsWest(2, North)
                        goBackwardsWest(2, South)
                        goBackwardsWest(3)
                        goBackwardsWest(3, North)
                        goBackwardsWest(3, South)

                        goBackwardsWest(4)
                        goBackwardsWest(4, North)
                        goBackwardsWest(4, South)

                        goBackwardsWest(5)
                        goBackwardsWest(5, North)
                        goBackwardsWest(5, South)

                        goBackwardsWest(6)
                        goBackwardsWest(6, North)
                        goBackwardsWest(6, South)

                        goBackwardsWest(7)
                        goBackwardsWest(7, North)
                        goBackwardsWest(7, South)

                        goBackwardsWest(8)
                        goBackwardsWest(8, North)
                        goBackwardsWest(8, South)

                        goBackwardsWest(9)
                        goBackwardsWest(9, North)
                        goBackwardsWest(9, South)

                        goBackwardsWest(10, North)
                        goBackwardsWest(10, South)
                    } else if (stepsTaken == 2) {
                        goBackwardsSouth(1)
                    } else if (stepsTaken == 3) {
                        goBackwardsSouth(2)
                    }
                }
            }
        }

        aStar(
            start = listOf(
                Crucible(rowIndex = 0, columnIndex = 0, direction = East, stepsTaken = 0),
                Crucible(rowIndex = 0, columnIndex = 0, direction = South, stepsTaken = 0),
            )
                .flatMap { it.nextStart(0) }
                .associate { it },
            end = listOf(
                Crucible(
                    rowIndex = map.size - 1,
                    columnIndex = map[0].size - 1,
                    direction = East,
                    stepsTaken = 1,
                ),
                Crucible(
                    rowIndex = map.size - 1,
                    columnIndex = map[0].size - 1,
                    direction = East,
                    stepsTaken = 2,
                ),
                Crucible(
                    rowIndex = map.size - 1,
                    columnIndex = map[0].size - 1,
                    direction = East,
                    stepsTaken = 3,
                ),
                Crucible(
                    rowIndex = map.size - 1,
                    columnIndex = map[0].size - 1,
                    direction = South,
                    stepsTaken = 1,
                ),
                Crucible(
                    rowIndex = map.size - 1,
                    columnIndex = map[0].size - 1,
                    direction = South,
                    stepsTaken = 2,
                ),
                Crucible(
                    rowIndex = map.size - 1,
                    columnIndex = map[0].size - 1,
                    direction = South,
                    stepsTaken = 3,
                ),
            )
                .flatMap { it.nextEnd(0) }
                .associate { it },
            comparatorStart = compareBy { (_, cooled) -> cooled },
            comparatorEnd = compareBy { (_, cooled) -> cooled },
            nextStart = { nextStart(it) },
            nextEnd = { nextEnd(it) },
        ).run { return this }
    }

    val testInput1 = readLines("Day17_1_test")
    check(part1(testInput1).also { println(it) } == 102L)
    val input = readLines("Day17")
    check(part1(input).also { println(it) } == 1004L)
//    check(part2(testInput1).also { println(it) } == 71L)


//    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
//    measureTimeMillis { part2(input).println() }.also { println("time: $it") }
}
