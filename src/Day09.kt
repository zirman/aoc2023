import kotlin.system.measureTimeMillis

fun main() {
    fun String.parse(): List<Int> = split(' ').map { it.toInt() }

    fun recur(nums: List<Int>): List<List<Int>> = buildList {
        if (nums.any { it != 0 }) addAll(recur(nums.windowed(2, 1).map { (p, n) -> n - p }))
        add(nums)
    }

    fun part1(input: List<String>): Int =
        input.sumOf { line -> recur(line.parse()).sumOf { numbers -> numbers.last() } }

    fun part2(input: List<String>): Int =
        input.sumOf { line -> recur(line.parse()).map { it.first() }.reduce { acc, n -> n - acc } }

    val testInput1 = readLines("Day09_1_test")
    check(part1(testInput1) == 114)
    check(part2(testInput1) == 2)

    val input = readLines("Day09")
    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    measureTimeMillis { part2(input).println() }.also { println("time: $it") }
}
