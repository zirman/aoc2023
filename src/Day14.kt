import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlin.system.measureTimeMillis

fun <T> T.iterateSkippingCycles(n: Long, iterate: T.() -> T): T {
    tailrec fun T.recur(lookupTable: PersistentList<T>): T {
        val lastSeenIndex = lookupTable.lastIndexOf(this)

        if (lastSeenIndex != -1) {
            val cycle = lookupTable.size - lastSeenIndex
            val offset = lookupTable.lastIndexOf(this)
            return lookupTable[(n - offset).mod(cycle) + offset]
        }

        return iterate().recur(lookupTable = lookupTable.add(this))
    }

    return recur(persistentListOf())
}

fun main() {
    fun List<List<Char>>.transpose(): List<List<Char>> {
        return this[0].indices.map { columnIndex ->
            indices.map { rowIndex ->
                this[rowIndex][columnIndex]
            }
        }
    }

    fun part1(input: List<String>): Int {
        val rocks = input.map { line -> line.toList() }

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
            .transpose()
            .reversed()
            .mapIndexed { rowIndex, row -> (row.count { it == 'O' } * (rowIndex + 1)) }
            .sum()
            .run { return this }
    }

    fun part2(input: List<String>): Int {
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

        val rocks = input.map { line ->
            line.toList()
        }

        return rocks.iterateSkippingCycles(1_000_000_000) { cycle() }.load()
    }

    val testInput1 = readLines("Day14_1_test")
    check(part1(testInput1) == 136)
    check(part2(testInput1) == 64)

    val input = readLines("Day14")
    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    measureTimeMillis { part2(input).println() }.also { println("time: $it") }
}
