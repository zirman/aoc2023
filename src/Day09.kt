fun main() {
    fun List<Int>.recur(): List<List<Int>> {
        return if (all { it == 0 }) listOf(this)
        else buildList {
            add(this@recur)
            this@recur
                .windowed(2, 1).map { (previousNumber, nextNumber) ->
                    nextNumber - previousNumber
                }
                .recur()
                .run { addAll(this) }
        }
    }

    fun String.parse(): List<Int> {
        return split(' ').map { it.toInt() }
    }

    fun part1(input: List<String>): Int {
        input
            .sumOf { line -> line.parse().recur().asReversed().sumOf { numbers -> numbers.last() } }
            .run { return this }
    }

    fun part2(input: List<String>): Int {
        input
            .sumOf { line ->
                line.parse()
                    .recur()
                    .asReversed()
                    .map { it.first() }
                    .reduce { acc, n -> n - acc }
            }
            .run { return this }
    }

    val testInput1 = readLines("Day09_1_test")
    check(part1(testInput1) == 114)
    check(part2(testInput1) == 2)

    val input = readLines("Day09")
    part1(input).println()
    part2(input).println()
}
