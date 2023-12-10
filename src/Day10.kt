import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlin.system.measureTimeMillis

fun main() {
    fun part1(input: List<String>): Int {
        val pipes = input.map { it.toList() }
        val startRowIndex = pipes.indexOfFirst { it.contains('S') }
        val startColumnIndex = pipes[startRowIndex].indexOfFirst { it == 'S' }

        fun connectedPipes(rowIndex: Int, columnIndex: Int): List<Pair<Int, Int>> = buildList {
            if (pipes.getOrNull(rowIndex - 1)?.get(columnIndex)?.let { "|F7".contains(it) } == true) {
                add(Pair(rowIndex - 1, columnIndex))
            }
            if (pipes.getOrNull(rowIndex + 1)?.get(columnIndex)?.let { "|LJ".contains(it) } == true) {
                add(Pair(rowIndex + 1, columnIndex))
            }
            if (pipes[rowIndex].getOrNull(columnIndex - 1)?.let { "-LF".contains(it) } == true) {
                add(Pair(rowIndex, columnIndex - 1))
            }
            if (pipes[rowIndex].getOrNull(columnIndex + 1)?.let { "-7J".contains(it) } == true) {
                add(Pair(rowIndex, columnIndex + 1))
            }
        }

        tailrec fun breathFirstSearch(
            depth: Int,
            current: List<Pair<Int, Int>>,
            visited: PersistentSet<Pair<Int, Int>>,
        ): Int {
            if (current.isEmpty()) return depth
            val next = current
                .flatMap { (rowIndex, columnIndex) -> connectedPipes(rowIndex, columnIndex) }
                .filter { visited.contains(it).not() }
            return breathFirstSearch(depth + 1, next, visited.addAll(current))
        }

        return breathFirstSearch(
            depth = 0,
            current = listOf(Pair(startRowIndex, startColumnIndex)),
            visited = persistentSetOf()
        ) - 1
    }

    fun part2(input: List<String>): Int {
        var pipes = input.map { it.toList() }
        val startRowIndex = pipes.indexOfFirst { it.contains('S') }
        val startColumnIndex = pipes[startRowIndex].indexOfFirst { it == 'S' }

        val startPipe =
            if (pipes.getOrNull(startRowIndex - 1)?.get(startColumnIndex)?.let { "|7F".contains(it) } == true) {
                if (pipes.getOrNull(startRowIndex + 1)?.get(startColumnIndex)?.let { "|JL".contains(it) } == true) {
                    '|'
                } else if (pipes[startRowIndex].getOrNull(startColumnIndex - 1)?.let { "-FL".contains(it) } == true) {
                    'J'
                } else if (pipes[startRowIndex].getOrNull(startColumnIndex + 1)?.let { "-FL".contains(it) } == true) {
                    'L'
                } else {
                    never()
                }
            } else if (pipes[startRowIndex].getOrNull(startColumnIndex - 1)?.let { "-FL".contains(it) } == true) {
                if (pipes[startRowIndex].getOrNull(startColumnIndex + 1)?.let { "-7J".contains(it) } == true) {
                    '-'
                } else if (pipes.getOrNull(startRowIndex + 1)?.get(startColumnIndex)
                        ?.let { "|LJ".contains(it) } == true
                ) {
                    '7'
                } else {
                    never()
                }
            } else if (pipes[startRowIndex].getOrNull(startColumnIndex + 1)?.let { "-7J".contains(it) } == true) {
                if (pipes.getOrNull(startRowIndex + 1)?.get(startColumnIndex)?.let { "|JL".contains(it) } == true) {
                    'F'
                } else {
                    never()
                }
            } else {
                never()
            }

        pipes = pipes.map { l -> l.map { if (it == 'S') startPipe else it } }

        fun connectedPipes(rowIndex: Int, columnIndex: Int): List<Pair<Int, Int>> = buildList {
            if ("|LJ".contains(pipes[rowIndex][columnIndex]) &&
                pipes.getOrNull(rowIndex - 1)?.get(columnIndex)?.let { "|F7".contains(it) } == true
            ) {
                add(Pair(rowIndex - 1, columnIndex))
            }
            if ("|F7".contains(pipes[rowIndex][columnIndex]) &&
                pipes.getOrNull(rowIndex + 1)?.get(columnIndex)?.let { "|LJ".contains(it) } == true
            ) {
                add(Pair(rowIndex + 1, columnIndex))
            }
            if ("-J7".contains(pipes[rowIndex][columnIndex]) &&
                pipes[rowIndex].getOrNull(columnIndex - 1)?.let { "-LF".contains(it) } == true
            ) {
                add(Pair(rowIndex, columnIndex - 1))
            }
            if ("-FL".contains(pipes[rowIndex][columnIndex]) &&
                pipes[rowIndex].getOrNull(columnIndex + 1)?.let { "-7J".contains(it) } == true
            ) {
                add(Pair(rowIndex, columnIndex + 1))
            }
        }

        tailrec fun breathFirstSearch(
            depth: Int,
            current: List<Pair<Int, Int>>,
            visited: PersistentSet<Pair<Int, Int>>,
        ): PersistentSet<Pair<Int, Int>> {
            if (current.isEmpty()) return visited
            val next = current
                .flatMap { (rowIndex, columnIndex) -> connectedPipes(rowIndex, columnIndex) }
                .filter { visited.contains(it).not() }
            return breathFirstSearch(depth + 1, next, visited.addAll(current))
        }

        val visited = breathFirstSearch(
            depth = 0,
            current = listOf(Pair(startRowIndex, startColumnIndex)),
            visited = persistentSetOf()
        )

        println(visited)

        val culledPipes = pipes.mapIndexed { rowIndex, row ->
            row.mapIndexed { columnIndex, pipe ->
                if (startRowIndex == rowIndex && startColumnIndex == columnIndex) {
                    startPipe
                } else if (visited.contains(Pair(rowIndex, columnIndex))) {
                    pipe
                } else {
                    '.'
                }
            }
        }

        culledPipes.joinToString("\n") { row -> row.joinToString("") }.also { println(it) }
        println()

        val filledPipes = culledPipes
            .map { row ->
                var crossings = 0
                var cornerCrossing = '.'
                row.map { pipe ->
                    when (pipe) {
                        '.' -> {
                            if (crossings % 2 == 0) 'O' else '*'
                        }

                        '|' -> {
                            crossings++
                            pipe
                        }

                        'F', 'L' -> {
                            cornerCrossing = pipe
                            pipe
                        }

                        'J' -> {
                            if (cornerCrossing == 'F') {
                                crossings++
                                cornerCrossing = '.'
                            }
                            pipe
                        }

                        '7' -> {
                            if (cornerCrossing == 'L') {
                                crossings++
                                cornerCrossing = '.'
                            }
                            pipe
                        }

                        '-' -> {
                            pipe
                        }

                        else -> never()
                    }
                }
            }

        filledPipes.joinToString("\n") { row -> row.joinToString("") }.also { println(it) }
        return filledPipes.sumOf { row -> row.count { it == '*' } }
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readLines("Day10_1_test")
    check(part1(testInput1) == 8)

    val testInput2 = readLines("Day10_2_test")
    check(part2(testInput2).also { println(it) } == 10)

    val input = readLines("Day10")
    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    measureTimeMillis { part2(input).println() }.also { println("time: $it") }
}
