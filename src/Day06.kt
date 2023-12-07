import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

fun main() {
    fun part1(input: List<String>): Int {
        val times = input[0].substringAfter("Time:").trim().split("""\s+""".toRegex()).map { it.toInt() }
        val distances = input[1].substringAfter("Distance:").trim().split("""\s+""".toRegex()).map { it.toInt() }
        return times
            .zip(distances)
            .map { (time, distance) ->
                (1..<time)
                    .map { (time - it) * it }
                    .count { it > distance }
            }
            .reduce(Int::times)
    }

    fun part2(input: List<String>): Int {
        val time = input[0].substringAfter("Time:").trim().filter { it.isDigit() }.toLong()
        val distance = input[1].substringAfter("Distance:").trim().filter { it.isDigit() }.toLong()
        val q = sqrt(((time * time) - (4 * distance)).toDouble())
        val maxX = floor((time + q) / 2).toInt()
        val minX = ceil((time - q) / 2).toInt()
        return (maxX - minX) + 1
    }

    val testInput1 = readLines("Day06_1_test")
//    check(part1(testInput1) == 288)
    check(part2(testInput1) == 71503)

    val input = readLines("Day06")
    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    measureTimeMillis { part2(input).println() }.also { println("time: $it") }
}
