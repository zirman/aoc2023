fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val (_, winningNumbersStr, myNumbersStr) = line.split(':', '|')
            val winningNumbers = winningNumbersStr.trim().split("""\s+""".toRegex()).toSet()
            1.shl(myNumbersStr.trim().split("""\s+""".toRegex()).count { winningNumbers.contains(it) } - 1)
        }
    }

    fun part2(input: List<String>): Int {
        val cardPoints = input.map { line ->
            val (_, winningNumbersStr, myNumbersStr) = line.split(':', '|')
            val winningNumbers = winningNumbersStr.trim().split("""\s+""".toRegex()).toSet()
            myNumbersStr.trim().split("""\s+""".toRegex()).count { winningNumbers.contains(it) }
        }

        val memo = mutableMapOf<Int, Int>()
        fun winningScratchCardCount(i: Int): Int =
            memo.getOrPut(i) { 1 + (i + 1..i + cardPoints[i]).sumOf { winningScratchCardCount(it) } }

        return cardPoints.indices.sumOf { winningScratchCardCount(it) }
    }

    val testInput1 = readLines("Day04_1_test")
    check(part1(testInput1) == 13)
    check(part2(testInput1) == 30)

    val input = readLines("Day04")
    part1(input).println()
    part2(input).println()
}
