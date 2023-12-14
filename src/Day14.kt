import kotlin.system.measureTimeMillis

fun main() {
    fun part1(input: List<String>): Int {
        fun transpose(xs: List<List<Char>>): List<List<Char>> {
            return xs[0].indices.map { columnIndex ->
                xs.indices.map { rowIndex ->
                    xs[rowIndex][columnIndex]
                }
            }
        }

        val rocks = input.map { line ->
            line.toList()
        }

        rocks[0].indices
            .map { columnIndex ->
                val column = rocks.indices.map { '.' }.toCharArray()
                var block = 0
                rocks.indices.map { rowIndex ->
                    when (rocks[rowIndex][columnIndex]) {
                        '.' -> {}
                        'O' -> {
                            column[block] = 'O'
                            block++
                        }

                        '#' -> {
                            column[rowIndex] = '#'
                            block = rowIndex + 1
                        }

                        else -> never()
                    }
                }
                column.toList()
            }
            .let { transpose(it) }
            .reversed()
            .mapIndexed { rowIndex, row -> (row.count { it == 'O' } * (rowIndex + 1)) }
            .sum()
            .run { return this }
    }

    fun part2(input: List<String>): Int {
        fun List<List<Char>>.transpose(): List<List<Char>> {
            return this[0].indices.map { columnIndex ->
                indices.map { rowIndex ->
                    this[rowIndex][columnIndex]
                }
            }
        }

        fun List<List<Char>>.rotate(): List<List<Char>> {
            return this[0].indices.map { columnIndex ->
                indices.reversed().map { rowIndex ->
                    this[rowIndex][columnIndex]
                }
            }
        }

        fun List<List<Char>>.slideRocks(): List<List<Char>> {
            return this[0].indices
                .map { columnIndex ->
                    val column = indices.map { '.' }.toCharArray()
                    var block = 0
                    indices.map { rowIndex ->
                        when (this[rowIndex][columnIndex]) {
                            '.' -> {}

                            'O' -> {
                                column[block] = 'O'
                                block++
                            }

                            '#' -> {
                                column[rowIndex] = '#'
                                block = rowIndex + 1
                            }

                            else -> never()
                        }
                    }
                    column.toList()
                }
                .transpose()
        }

        fun List<List<Char>>.cycle(): List<List<Char>> {
            return slideRocks().rotate().slideRocks().rotate().slideRocks().rotate().slideRocks().rotate()
        }

        fun List<List<Char>>.load(): Int {
            return reversed()
                .mapIndexed { rowIndex, row -> (row.count { it == 'O' } * (rowIndex + 1)) }
                .sum()
        }

        var rocks = input.map { line ->
            line.toList()
        }

        val table = mutableListOf<List<List<Char>>>()

        for (i in 0..1_000_000_000) {
            if (table.contains(rocks)) {
                val cycle = table.size - table.indexOf(rocks)
                val offset = table.lastIndexOf(rocks)
                return table[(1_000_000_000 - offset).mod(cycle) + offset].load()
            }

            table.add(rocks)
            rocks = rocks.cycle()
        }

        never()
    }

    val testInput1 = readLines("Day14_1_test")
    check(part1(testInput1) == 136)
    check(part2(testInput1) == 64)

    val input = readLines("Day14")
    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    measureTimeMillis { part2(input).println() }.also { println("time: $it") }
}
