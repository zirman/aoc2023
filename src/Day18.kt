import Direction.*
import kotlin.math.abs
import kotlin.system.measureTimeMillis

fun main() {
    fun part1(input: List<String>): Int {
        val dug = mutableSetOf<Position>()
        var p = Position(0, 0)
        dug.add(p)
        input.forEach { line ->
            val (direction, length, color) = line.split(' ')
            when (direction) {
                "R" -> {
                    (p.columnIndex + 1..p.columnIndex + length.toInt()).forEach {
                        p = p.copy(columnIndex = it)
                        dug.add(p)
                    }
                }

                "D" -> {
                    (p.rowIndex + 1..p.rowIndex + length.toInt()).forEach {
                        p = p.copy(rowIndex = it)
                        dug.add(p)
                    }
                }

                "L" -> {
                    val c = p.columnIndex
                    (1..length.toInt()).forEach {
                        p = p.copy(columnIndex = c - it)
                        dug.add(p)
                    }
                }

                "U" -> {
                    val r = p.rowIndex
                    (1..length.toInt()).forEach {
                        p = p.copy(rowIndex = r - it)
                        dug.add(p)
                    }
                }

                else -> never()
            }
        }
        val rowRange = dug.minOf { (a, _) -> a }..dug.maxOf { (a, _) -> a }
        val columnRange = dug.minOf { (_, b) -> b }..dug.maxOf { (_, b) -> b }

//        println(minRow)
//        println(maxRow)
//        println(minColumn)
//        println(maxColumn)
//        val foo = (minRow..maxRow).map { rowIndex ->
//            (minColumn..maxColumn).map { columnIndex ->
//                if (dug.contains(Position(rowIndex, columnIndex))) {
//                    '#'
//                } else {
//                    '.'
//                }
//            }
//        }

        fun floodFillEdge(): Int {
            val visited = mutableSetOf<Position>()

            val recur = DeepRecursiveFunction { position: Position ->
                if (rowRange.contains(position.rowIndex).not() ||
                    columnRange.contains(position.columnIndex).not() ||
                    dug.contains(position) ||
                    visited.contains(position)
                ) return@DeepRecursiveFunction

                visited.add(position)

                callRecursive(position.copy(rowIndex = position.rowIndex + 1))
                callRecursive(position.copy(rowIndex = position.rowIndex - 1))
                callRecursive(position.copy(columnIndex = position.columnIndex + 1))
                callRecursive(position.copy(columnIndex = position.columnIndex - 1))
            }

            rowRange.forEach { recur(Position(it, columnRange.first)) }
            rowRange.forEach { recur(Position(it, columnRange.last)) }
            columnRange.forEach { recur(Position(rowRange.first, it)) }
            columnRange.forEach { recur(Position(rowRange.last, it)) }

            return (rowRange.count() * columnRange.count()) - visited.count()
        }

        return floodFillEdge()
    }

    fun part2(input: List<String>): Long {
        val points = buildList {
            var x = 0L
            var y = 0L

            add(Pair(x, y))

            input.forEach { line ->
                val (_, _, hex) = line.split(' ')
                val q = hex.filter { it.isLetterOrDigit() }
                val length = q.take(5).toInt(16)

                val direction = when (q.drop(5)) {
                    "0" -> East
                    "2" -> West

                    "1" -> South
                    "3" -> North

                    else -> never()
                }

                when (direction) {
                    East -> {
                        x += length
                        add(Pair(x, y))
                    }

                    South -> {
                        y -= length
                        add(Pair(x, y))
                    }

                    West -> {
                        x -= length
                        add(Pair(x, y))
                    }

                    North -> {
                        y += length
                        add(Pair(x, y))
                    }
                }
            }
        }

        println("b")

        // shoelace formula to get area from points in center of squares
        val a = sequence { while (true) yieldAll(points) }
            .windowed(2, 1) { (a, b) -> a.first * b.second }
            .take(points.size)
            .sum()

        val b = sequence { while (true) yieldAll(points) }
            .windowed(2, 1) { (a, b) -> a.second * b.first }
            .take(points.size)
            .sum()

        return abs(a - b) / 2
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readLines("Day18_1_test")
//    check(part1(testInput1).also { println(it) } == 62)
    check(part2(testInput1).also { println(it) } == 952_408_144_115L)

    val input = readLines("Day18")
//    part1(input).println()
    measureTimeMillis { part2(input).println() }.also { println(it) }
}
