fun main() {


    fun part1(input: List<String>, maxDepth: Int): Int {
        operator fun List<List<Char>>.get(rowIndex: Int, columnIndex: Int): Char {
            if (rowIndex < 0 || rowIndex >= size || columnIndex < 0 || columnIndex >= this[0].size) return '.'
            return this[rowIndex][columnIndex]
        }

        fun <T> Set<T>.breathFirstSearch(
            maxDepth: Int,
            childNodes: T.() -> List<T>,
        ): Int {

            tailrec fun Set<T>.recur(depth: Int): Int {
                if (depth == maxDepth) return count()

                return this
                    .flatMap(childNodes)
                    .toSet()
                    .recur(depth + 1)
            }

            return recur(0)
        }

        var start = Position(0, 0)

        val garden = input.mapIndexed { rowIndex, line ->
            line.mapIndexed { columnIndex, c ->
                if (c == 'S') {
                    start = Position(rowIndex, columnIndex)
                    '.'
                } else c
            }
        }

        return setOf(start).breathFirstSearch(maxDepth) {
            buildList {
                if (garden[rowIndex - 1, columnIndex] == '.') add(Position(rowIndex - 1, columnIndex))
                if (garden[rowIndex + 1, columnIndex] == '.') add(Position(rowIndex + 1, columnIndex))
                if (garden[rowIndex, columnIndex - 1] == '.') add(Position(rowIndex, columnIndex - 1))
                if (garden[rowIndex, columnIndex + 1] == '.') add(Position(rowIndex, columnIndex + 1))
            }
        }
    }

    fun part2(input: List<String>, maxDepth: Int): Long {
        operator fun List<List<Char>>.get(rowIndex: Int, columnIndex: Int): Char {
            return this[rowIndex.mod(size)][columnIndex.mod(this[0].size)]
        }

        operator fun List<List<Long>>.get(rowIndex: Int, columnIndex: Int): Long {
            return this[rowIndex.mod(size)][columnIndex.mod(this[0].size)]
        }

        operator fun List<MutableList<Long>>.set(rowIndex: Int, columnIndex: Int, value: Long) {
            this[rowIndex.mod(size)][columnIndex.mod(this[0].size)] = value
        }

        var start = Position(0, 0)
        val garden = input.mapIndexed { rowIndex, line ->
            line.mapIndexed { columnIndex, c ->
                if (c == 'S') {
                    start = Position(rowIndex, columnIndex)
                    '.'
                } else c
            }
        }

        val counts = garden.map { it.map { 0L }.toMutableList() }
        counts[start.rowIndex, start.columnIndex]++

        fun <T> Set<T>.breathFirstSearch(
            maxDepth: Int,
            childNodes: T.() -> Set<T>,
        ): Long {
            tailrec fun Set<T>.recur(depth: Int): Long {
                println("${depth} ${count()} ${counts.sumOf { it.sum() }}")
                if (depth == maxDepth) return counts.sumOf { it.sum() }

                return buildSet {
                    this@recur.forEach {
                        addAll(it.childNodes())
                    }
                }.recur(depth + 1)
            }

            return recur(0)
        }

        return setOf(start).breathFirstSearch(maxDepth) {
            buildSet {
                if (garden[rowIndex - 1, columnIndex] == '.') {
                    add(Position((rowIndex - 1).mod(garden.size), columnIndex))
                }
                if (garden[rowIndex + 1, columnIndex] == '.') {
                    add(Position((rowIndex + 1).mod(garden.size), columnIndex))
                }
                if (garden[rowIndex, columnIndex - 1] == '.') {
                    add(Position(rowIndex, (columnIndex - 1).mod(garden[0].size)))
                }
                if (garden[rowIndex, columnIndex + 1] == '.') {
                    add(Position(rowIndex, (columnIndex + 1).mod(garden[0].size)))
                }
            }.also {
                println(it)
                counts[rowIndex, columnIndex]--
                it.forEach { p ->
                    counts[p.rowIndex, p.columnIndex]++
                }
            }
        }
    }

    val testInput1 = readLines("Day21_1_test")
    check(part1(testInput1, 6).also { println(it) } == 16)
    check(part2(testInput1, 6).also { println(it) } == 16L)
//    check(part2(testInput1, 6).also { println(it) } == 16)

    val input = readLines("Day21")
    part1(input, 64).println()
//    part2(input, 26_501_365).println()
}
