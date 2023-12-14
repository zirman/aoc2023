import kotlin.system.measureTimeMillis

fun main() {
    fun part1(input: String): Int {
        fun transpose(xs: List<List<Char>>): List<List<Char>> {
            return xs[0].indices.map { columnIndex ->
                xs.indices.map { rowIndex ->
                    xs[rowIndex][columnIndex]
                }
            }
        }

        input
            .split("\n\n")
            .map { group -> group.split('\n').filter { it.isNotBlank() }.map { it.toList() } }
            .sumOf { rocks ->
                val transposed = transpose(rocks)
                val columnsBefore = transposed.indices.drop(1)
                    .map { rowIndex ->
                        Pair(
                            transposed
                                .subList(0, rowIndex)
                                .reversed()
                                .map { line ->
                                    line.joinToString("") { if (it == '#') "1" else "0" }.toInt(2)
                                },
                            transposed
                                .subList(rowIndex, transposed.size)
                                .map { line ->
                                    line.joinToString("") { if (it == '#') "1" else "0" }.toInt(2)
                                },
                        )
                    }
                    .sumOf { (rowsBefore, rowsAfter) ->
                        if (rowsBefore.zip(rowsAfter).all { (rowBefore, rowAfter) -> rowBefore == rowAfter }) {
                            rowsBefore.size
                        } else {
                            0
                        }
                    }

                val rowsBefore = rocks.indices.drop(1)
                    .map { rowIndex ->
                        Pair(
                            rocks.subList(0, rowIndex).reversed(),
                            rocks.subList(rowIndex, rocks.size),
                        )
                    }
                    .sumOf { (rowsBefore, rowsAfter) ->
                        if (rowsBefore.zip(rowsAfter).all { (rowBefore, rowAfter) -> rowBefore == rowAfter }) {
                            rowsBefore.size
                        } else {
                            0
                        }
                    }

                if (columnsBefore == 0 && rowsBefore == 0) {
                    rocks.joinToString("\n") { it.joinToString("") }.also { println(it) }
                    throw Exception()
                }
                if (columnsBefore != 0 && rowsBefore != 0) throw Exception()

                columnsBefore + (100 * rowsBefore)
            }
            .run { return this }
    }

    fun part2(input: String): Int {
        fun transpose(xs: List<List<Char>>): List<List<Char>> {
            return xs[0].indices.map { columnIndex ->
                xs.indices.map { rowIndex ->
                    xs[rowIndex][columnIndex]
                }
            }
        }

        input
            .split("\n\n")
            .map { group -> group.split('\n').filter { it.isNotBlank() }.map { it.toList() } }
            .sumOf { rocks ->
                val transposed = transpose(rocks)
                val columnsBefore = transposed.indices
                    .drop(1)
                    .map { rowIndex ->
                        Pair(
                            transposed
                                .subList(0, rowIndex)
                                .reversed()
                                .map { line ->
                                    line.joinToString("") { if (it == '#') "1" else "0" }.toInt(2)
                                },
                            transposed
                                .subList(rowIndex, transposed.size)
                                .map { line ->
                                    line.joinToString("") { if (it == '#') "1" else "0" }.toInt(2)
                                },
                        )
                    }
                    .sumOf { (rowsBefore, rowsAfter) ->
                        if (rowsBefore
                                .zip(rowsAfter)
                                .sumOf { (rowBefore, rowAfter) -> (rowBefore xor rowAfter).countOneBits() } == 1
                        ) {
                            rowsBefore.size
                        } else {
                            0
                        }
                    }

                val rowsBefore = rocks.indices.drop(1)
                    .map { rowIndex ->
                        Pair(
                            rocks.subList(0, rowIndex).reversed().map { line ->
                                line.joinToString("") { if (it == '#') "1" else "0" }.toInt(2)
                            },
                            rocks.subList(rowIndex, rocks.size).map { line ->
                                line.joinToString("") { if (it == '#') "1" else "0" }.toInt(2)
                            },
                        )
                    }
                    .sumOf { (rowsBefore, rowsAfter) ->
                        if (rowsBefore
                                .zip(rowsAfter)
                                .sumOf { (rowBefore, rowAfter) -> (rowBefore xor rowAfter).countOneBits() } == 1
                        ) {
                            rowsBefore.size
                        } else {
                            0
                        }
                    }

                if (columnsBefore == 0 && rowsBefore == 0) {
                    rocks.joinToString("\n") { it.joinToString("") }.also { println(it) }
                    throw Exception()
                }
                if (columnsBefore != 0 && rowsBefore != 0) throw Exception()

                columnsBefore + (100 * rowsBefore)
            }
            .run { return this }
    }

    val input = readFile("Day13")
    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    measureTimeMillis { part2(input).println() }.also { println("time: $it") }
}
