import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlin.system.measureTimeMillis

fun main() {
    operator fun List<List<Char>>.get(rowIndex: Int, columnIndex: Int): Char? =
        getOrNull(rowIndex)?.getOrNull(columnIndex)

    fun Char.connectsNorth(): Boolean = "|JL".contains(this)
    fun Char.connectsSouth(): Boolean = "|7F".contains(this)
    fun Char.connectsWest(): Boolean = "-7J".contains(this)
    fun Char.connectsEast(): Boolean = "-LF".contains(this)

    fun parse(input: List<String>): Triple<Int, Int, List<List<Char>>> {
        val pipes = input.map { it.toList() }
        val startRowIndex = pipes.indexOfFirst { it.contains('S') }
        val startColumnIndex = pipes[startRowIndex].indexOfFirst { it == 'S' }

        return Triple(
            startRowIndex,
            startColumnIndex,
            pipes.map { row ->
                row.map {
                    if (it == 'S') {
                        when {
                            pipes[startRowIndex - 1, startColumnIndex]?.connectsSouth() == true -> when {
                                pipes[startRowIndex + 1, startColumnIndex]?.connectsNorth() == true -> '|'
                                pipes[startRowIndex, startColumnIndex - 1]?.connectsWest() == true -> 'J'
                                pipes[startRowIndex, startColumnIndex + 1]?.connectsEast() == true -> 'L'
                                else -> never()
                            }

                            pipes[startRowIndex, startColumnIndex - 1]?.connectsEast() == true -> when {
                                pipes[startRowIndex, startColumnIndex + 1]?.connectsWest() == true -> '-'
                                pipes[startRowIndex + 1, startColumnIndex]?.connectsNorth() == true -> '7'
                                else -> never()
                            }

                            pipes[startRowIndex, startColumnIndex + 1]?.connectsWest() == true -> when {
                                pipes[startRowIndex + 1, startColumnIndex]?.connectsNorth() == true -> 'F'
                                else -> never()
                            }

                            else -> never()
                        }
                    } else {
                        it
                    }
                }
            },
        )
    }

    fun List<List<Char>>.connectedPipes(rowIndex: Int, columnIndex: Int): List<Pair<Int, Int>> = buildList {
        if (this@connectedPipes[rowIndex][columnIndex].connectsNorth() &&
            this@connectedPipes[rowIndex - 1, columnIndex]?.connectsSouth() == true
        ) {
            add(Pair(rowIndex - 1, columnIndex))
        }
        if (this@connectedPipes[rowIndex][columnIndex].connectsSouth() &&
            this@connectedPipes[rowIndex + 1, columnIndex]?.connectsNorth() == true
        ) {
            add(Pair(rowIndex + 1, columnIndex))
        }
        if (this@connectedPipes[rowIndex][columnIndex].connectsWest() &&
            this@connectedPipes[rowIndex, columnIndex - 1]?.connectsEast() == true
        ) {
            add(Pair(rowIndex, columnIndex - 1))
        }
        if (this@connectedPipes[rowIndex][columnIndex].connectsEast() &&
            this@connectedPipes[rowIndex, columnIndex + 1]?.connectsWest() == true
        ) {
            add(Pair(rowIndex, columnIndex + 1))
        }
    }

    fun part1(input: List<String>): Int {
        val (startRowIndex, startColumnIndex, pipes) = parse(input)

        tailrec fun List<Pair<Int, Int>>.breathFirstSearch(
            depth: Int,
            visited: PersistentSet<Pair<Int, Int>>,
        ): Int = flatMap { (rowIndex, columnIndex) -> pipes.connectedPipes(rowIndex, columnIndex) }
            .filter { visited.contains(it).not() }
            .ifEmpty { return depth }
            .breathFirstSearch(depth + 1, visited = visited.addAll(this))

        return listOf(Pair(startRowIndex, startColumnIndex)).breathFirstSearch(
            depth = 0,
            visited = persistentSetOf(),
        )
    }

    fun part2(input: List<String>): Int {
        val (startRowIndex, startColumnIndex, pipes) = parse(input)

        tailrec fun List<Pair<Int, Int>>.breathFirstSearch(
            depth: Int,
            visited: PersistentSet<Pair<Int, Int>>,
        ): PersistentSet<Pair<Int, Int>> =
            flatMap { (rowIndex, columnIndex) -> pipes.connectedPipes(rowIndex, columnIndex) }
                .filter { visited.contains(it).not() }
                .ifEmpty { return visited.addAll(this) }
                .breathFirstSearch(depth = depth + 1, visited = visited.addAll(this))

        val visitedPipes = listOf(Pair(startRowIndex, startColumnIndex)).breathFirstSearch(
            depth = 0,
            visited = persistentSetOf(),
        )

        pipes
            .mapIndexed { rowIndex, row ->
                row.mapIndexed { columnIndex, pipe ->
                    if (visitedPipes.contains(Pair(rowIndex, columnIndex))) {
                        pipe
                    } else {
                        '.'
                    }
                }
            }
            .map { row ->
                var crossings = 0
                var cornerCrossing = '.'

                row.map { pipe ->
                    when (pipe) {
                        '.' -> if (crossings % 2 == 0) 'O' else 'I'

                        '|' -> {
                            crossings++
                            pipe
                        }

                        // start possible corner crossing
                        'F', 'L' -> {
                            cornerCrossing = pipe
                            pipe
                        }

                        'J' -> {
                            // finish corner crossing
                            if (cornerCrossing == 'F') crossings++
                            pipe
                        }

                        '7' -> {
                            // finish corner crossing
                            if (cornerCrossing == 'L') crossings++
                            pipe
                        }

                        '-' -> pipe
                        else -> never()
                    }
                }
            }
//            .also { println(it.joinToString("\n") { row -> row.joinToString("") }) }
            .sumOf { row -> row.count { it == 'I' } }
            .run { return this }
    }

    val testInput1 = readLines("Day10_1_test")
    check(part1(testInput1) == 8)

    val testInput2 = readLines("Day10_2_test")
    check(part2(testInput2) == 10)

    val input = readLines("Day10")
    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    measureTimeMillis { part2(input).println() }.also { println("time: $it") }
}
