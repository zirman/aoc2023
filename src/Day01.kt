fun main() {
    fun part1(input: List<String>): Int =
         input.sumOf { line -> "${line.first { it.isDigit() }}${line.last { it.isDigit() }}".toInt() }

    val numbers = mapOf(
        "1" to 1,
        "one" to 1,
        "2" to 2,
        "two" to 2,
        "3" to 3,
        "three" to 3,
        "4" to 4,
        "four" to 4,
        "5" to 5,
        "five" to 5,
        "6" to 6,
        "six" to 6,
        "7" to 7,
        "seven" to 7,
        "8" to 8,
        "eight" to 8,
        "9" to 9,
        "nine" to 9,
    ).mapKeys { (k) -> k.toRegex() }

    fun part2(input: List<String>): Int {
        fun String.checkMatch(i: Int): Int? =
            numbers.firstNotNullOfOrNull { (regex, n) -> if (regex.matchesAt(this, i)) n else null }

        return input.sumOf { line ->
            "${
                line.indices.firstNotNullOf { i -> line.checkMatch(i) }
            }${
                line.indices.reversed().firstNotNullOf { i -> line.checkMatch(i) }
            }".toInt()
        }
    }

    val testInput1 = readInput("Day01_1_test")
    check(part1(testInput1) == 142)

    val testInput2 = readInput("Day01_2_test")
    check(part2(testInput2) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
