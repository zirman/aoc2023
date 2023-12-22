import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis

sealed interface Brick {
    val xRange: IntRange
    val yRange: IntRange
    val zRange: IntRange

    data class X(val x: IntRange, val y: Int, var z: Int) : Brick {
        override val xRange: IntRange = x
        override val yRange: IntRange = y..y
        override val zRange: IntRange get() = z..z
    }

    data class Y(val x: Int, val y: IntRange, var z: Int) : Brick {
        override val xRange: IntRange = x..x
        override val yRange: IntRange = y
        override val zRange: IntRange get() = z..z
    }

    data class Z(val x: Int, val y: Int, var z: IntRange) : Brick {
        override val xRange: IntRange = x..x
        override val yRange: IntRange = y..y
        override val zRange: IntRange get() = z
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val bricks = input
            .map { line ->
                val (p1, p2) = line.split('~')
                val (x1, y1, z1) = p1.split(',').map { it.toInt() }
                val (x2, y2, z2) = p2.split(',').map { it.toInt() }
                if (y1 == y2 && z1 == z2) Brick.X(min(x1, x2)..max(x1, x2), y1, z1)
                else if (x1 == x2 && z1 == z2) Brick.Y(x1, min(y1, y2)..max(y1, y2), z1)
                else if (x1 == x2 && y1 == y2) Brick.Z(x1, y1, min(z1, z2)..max(z1, z2))
                else never()
            }

        val bricksX = bricks
            .flatMap { brick ->
                when (brick) {
                    is Brick.X -> brick.x.map { Pair(it, brick) }
                    is Brick.Y -> listOf(Pair(brick.x, brick))
                    is Brick.Z -> listOf(Pair(brick.x, brick))
                }
            }
            .groupBy { (x) -> x }
            .mapValues { (_, values) -> values.map { it.second } }

        val bricksY = bricks
            .flatMap { brick ->
                when (brick) {
                    is Brick.X -> listOf(Pair(brick.y, brick))
                    is Brick.Y -> brick.y.map { Pair(it, brick) }
                    is Brick.Z -> listOf(Pair(brick.y, brick))
                }
            }
            .groupBy { (y) -> y }
            .mapValues { (_, values) -> values.map { it.second } }

        bricks
            .groupBy { it.zRange.first }
            .toList()
            .sortedBy { (minZ) -> minZ }
            .map { (_, bricksLayer) -> bricksLayer }
            .forEach { bricksLayer ->
                bricksLayer.forEach { brick ->
                    when (brick) {
                        is Brick.X -> {
                            val maxOf = bricksY[brick.y]
                                .orEmpty()
                                .filter { it.zRange.first < brick.zRange.first && it.xRange.any { it in brick.x } }
                                .maxOfOrNull { it.zRange.last }
                                ?: 0

                            brick.z = maxOf + 1
                        }

                        is Brick.Y -> {
                            val maxOf = bricksX[brick.x]
                                .orEmpty()
                                .filter { it.zRange.first < brick.zRange.first && it.yRange.any { it in brick.y } }
                                .maxOfOrNull { it.zRange.last }
                                ?: 0

                            brick.z = maxOf + 1
                        }

                        is Brick.Z -> {
                            val maxOf = bricksX[brick.x]
                                .orEmpty()
                                .filter { it.zRange.first < brick.zRange.first && brick.y in it.yRange }
                                .maxOfOrNull { it.zRange.last }
                                ?: 0

                            brick.z = maxOf + 1..maxOf + brick.z.count()
                        }
                    }
                }
            }

        val supportedBy = bricks.associate { brick ->
            when (brick) {
                is Brick.X -> {
                    Pair(
                        brick,
                        bricksY[brick.y]
                            .orEmpty()
                            .filter { it.zRange.last + 1 == brick.zRange.first && it.xRange.any { it in brick.x } },
                    )
                }

                is Brick.Y -> {
                    Pair(
                        brick,
                        bricksX[brick.x]
                            .orEmpty()
                            .filter { it.zRange.last + 1 == brick.zRange.first && it.yRange.any { it in brick.y } },
                    )
                }

                is Brick.Z -> {
                    Pair(
                        brick,
                        bricksX[brick.x]
                            .orEmpty()
                            .filter { it.zRange.last + 1 == brick.zRange.first && brick.y in it.yRange }
                    )
                }
            }
        }

        val supportsMap = bricks.associate { brick ->
            when (brick) {
                is Brick.X -> {
                    Pair(
                        brick,
                        bricksY[brick.y]
                            .orEmpty()
                            .filter { it.zRange.first == brick.zRange.last + 1 && it.xRange.any { it in brick.x } },
                    )
                }

                is Brick.Y -> {
                    Pair(
                        brick,
                        bricksX[brick.x]
                            .orEmpty()
                            .filter { it.zRange.first == brick.zRange.last + 1 && it.yRange.any { it in brick.y } },
                    )
                }

                is Brick.Z -> {
                    Pair(
                        brick,
                        bricksX[brick.x]
                            .orEmpty()
                            .filter { it.zRange.first == brick.zRange.last + 1 && brick.y in it.yRange },
                    )
                }
            }
        }

        return bricks.count {
            supportsMap[it].orEmpty().all { supportedBy[it].orEmpty().count() > 1 }
        }
    }

    fun part2(input: List<String>): Int {
        val bricks = input
            .map { line ->
                val (p1, p2) = line.split('~')
                val (x1, y1, z1) = p1.split(',').map { it.toInt() }
                val (x2, y2, z2) = p2.split(',').map { it.toInt() }
                if (y1 == y2 && z1 == z2) Brick.X(min(x1, x2)..max(x1, x2), y1, z1)
                else if (x1 == x2 && z1 == z2) Brick.Y(x1, min(y1, y2)..max(y1, y2), z1)
                else if (x1 == x2 && y1 == y2) Brick.Z(x1, y1, min(z1, z2)..max(z1, z2))
                else never()
            }

        val bricksX = bricks
            .flatMap { brick ->
                when (brick) {
                    is Brick.X -> brick.x.map { Pair(it, brick) }
                    is Brick.Y -> listOf(Pair(brick.x, brick))
                    is Brick.Z -> listOf(Pair(brick.x, brick))
                }
            }
            .groupBy { (x) -> x }
            .mapValues { (_, values) -> values.map { it.second } }

        val bricksY = bricks
            .flatMap { brick ->
                when (brick) {
                    is Brick.X -> listOf(Pair(brick.y, brick))
                    is Brick.Y -> brick.y.map { Pair(it, brick) }
                    is Brick.Z -> listOf(Pair(brick.y, brick))
                }
            }
            .groupBy { (y) -> y }
            .mapValues { (_, values) -> values.map { it.second } }

        bricks
            .groupBy { it.zRange.first }
            .toList()
            .sortedBy { (minZ) -> minZ }
            .map { (_, bricksLayer) -> bricksLayer }
            .forEach { bricksLayer ->
                bricksLayer.forEach { brick ->
                    when (brick) {
                        is Brick.X -> {
                            val maxOf = bricksY[brick.y]
                                .orEmpty()
                                .filter { it.zRange.first < brick.zRange.first && it.xRange.any { it in brick.x } }
                                .maxOfOrNull { it.zRange.last }
                                ?: 0

                            brick.z = maxOf + 1
                        }

                        is Brick.Y -> {
                            val maxOf = bricksX[brick.x]
                                .orEmpty()
                                .filter { it.zRange.first < brick.zRange.first && it.yRange.any { it in brick.y } }
                                .maxOfOrNull { it.zRange.last }
                                ?: 0

                            brick.z = maxOf + 1
                        }

                        is Brick.Z -> {
                            val maxOf = bricksX[brick.x]
                                .orEmpty()
                                .filter { it.zRange.first < brick.zRange.first && brick.y in it.yRange }
                                .maxOfOrNull { it.zRange.last }
                                ?: 0

                            brick.z = maxOf + 1..maxOf + brick.z.count()
                        }
                    }
                }
            }

        val supportedBy = bricks.associate { brick ->
            when (brick) {
                is Brick.X -> {
                    Pair(
                        brick,
                        bricksY[brick.y]
                            .orEmpty()
                            .filter { it.zRange.last + 1 == brick.zRange.first && it.xRange.any { it in brick.x } },
                    )
                }

                is Brick.Y -> {
                    Pair(
                        brick,
                        bricksX[brick.x]
                            .orEmpty()
                            .filter { it.zRange.last + 1 == brick.zRange.first && it.yRange.any { it in brick.y } },
                    )
                }

                is Brick.Z -> {
                    Pair(
                        brick,
                        bricksX[brick.x]
                            .orEmpty()
                            .filter { it.zRange.last + 1 == brick.zRange.first && brick.y in it.yRange }
                    )
                }
            }
        }

        val supportsMap = bricks.associate { brick ->
            when (brick) {
                is Brick.X -> {
                    Pair(
                        brick,
                        bricksY[brick.y]
                            .orEmpty()
                            .filter { b -> b.zRange.first == brick.zRange.last + 1 && b.xRange.any { it in brick.x } },
                    )
                }

                is Brick.Y -> {
                    Pair(
                        brick,
                        bricksX[brick.x]
                            .orEmpty()
                            .filter { b -> b.zRange.first == brick.zRange.last + 1 && b.yRange.any { it in brick.y } },
                    )
                }

                is Brick.Z -> {
                    Pair(
                        brick,
                        bricksX[brick.x]
                            .orEmpty()
                            .filter { it.zRange.first == brick.zRange.last + 1 && brick.y in it.yRange },
                    )
                }
            }
        }

        tailrec fun recur(previouslyRemovedBricks: PersistentSet<Brick>, bricks: PersistentSet<Brick>): Int {
            if (bricks.isEmpty()) return previouslyRemovedBricks.count()

            val removedBricks = previouslyRemovedBricks.addAll(bricks)

            return recur(
                removedBricks,
                bricks
                    .flatMap { brick -> supportsMap[brick].orEmpty() }
                    .filter { brick -> supportedBy[brick].orEmpty().all { removedBricks.contains(it) } }
                    .toPersistentSet(),
            )
        }

        return bricks.sumOf { brick -> (recur(persistentSetOf(), persistentSetOf(brick)) - 1) }
    }

    val testInput1 = readLines("Day22_1_test")
    check(part1(testInput1) == 5)
    check(part2(testInput1) == 7)

    val input = readLines("Day22")
    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    measureTimeMillis { part2(input).println() }.also { println("time: $it") }
}
