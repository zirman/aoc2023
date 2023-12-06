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
        return (1..<time)
            .map { (time - it) * it }
            .count { it > distance }
    }

    val testInput1 = readLines("Day06_1_test")
    check(part1(testInput1) == 288)
    check(part2(testInput1) == 71503)

    val input = readLines("Day06")
    part1(input).println()
    part2(input).println()
}
