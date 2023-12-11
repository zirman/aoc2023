import kotlin.math.abs
import kotlin.system.measureTimeMillis

fun main() {
    fun <T> List<List<T>>.transpose(): List<List<T>> = this[0].indices.map { columnIndex ->
        indices.map { rowIndex ->
            this[rowIndex][columnIndex]
        }
    }

    fun List<List<Char>>.expandRows(): List<List<Char>> = flatMap { row ->
        if (row.contains('#')) {
            listOf(row)
        } else {
            listOf(row, row)
        }
    }

    fun part1(input: List<String>): Int {
        val galaxyMap =
            input.map { rowString -> rowString.toList() }.expandRows().transpose().expandRows().transpose()

        val galaxyPositions = galaxyMap.flatMapIndexed { rowIndex: Int, row: List<Char> ->
            row.flatMapIndexed { columnIndex, char ->
                if (char == '#') listOf(
                    Pair(
                        rowIndex,
                        columnIndex
                    )
                ) else emptyList()
            }
        }

        galaxyPositions
            .flatMapIndexed { galaxyIndex1, (rowIndex1, columnIndex1) ->
                galaxyPositions.subList(galaxyIndex1 + 1, galaxyPositions.size)
                    .map { (rowIndex2, columnIndex2) ->
                        abs(rowIndex1 - rowIndex2) + abs(columnIndex1 - columnIndex2)
                    }
            }
            .sum()
            .run { return this }
    }

    fun part2(input: List<String>, expansion: Long): Long {
        val galaxyMap = input.map { rowString -> rowString.toList() }

        var rowOffset = 0L
        val galaxyPositions = galaxyMap
            .flatMapIndexed { rowIndex, row ->
                var columnOffset = 0L
                if (row.all { it != '#' }) {
                    rowOffset += expansion - 1
                    emptyList()
                } else {
                    row.mapIndexedNotNull { columnIndex, c ->
                        if (c == '#') {
                            Pair(rowIndex + rowOffset, columnIndex + columnOffset)
                        } else if (galaxyMap.map { it[columnIndex] }.all { it != '#' }) {
                            columnOffset += expansion - 1
                            null
                        } else {
                            null
                        }
                    }
                }
            }

        galaxyPositions
            .flatMapIndexed { galaxyIndex1, (rowIndex1, columnIndex1) ->
                galaxyPositions
                    .subList(galaxyIndex1 + 1, galaxyPositions.size)
                    .map { (rowIndex2, columnIndex2) -> abs(rowIndex1 - rowIndex2) + abs(columnIndex1 - columnIndex2) }
            }
            .sum()
            .run { return this }
    }

    val testInput1 = readLines("Day11_1_test")
    check(part1(testInput1) == 374)
    check(part2(testInput1, 2) == 374L)

    val input = readLines("Day11")
    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    measureTimeMillis { part2(input, 1000000).println() }.also { println("time: $it") }
}
