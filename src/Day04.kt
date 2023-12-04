fun main() {
    fun part1(input: List<String>): Int {
        val wsRegex = """\s+""".toRegex()

        return input.sumOf { line ->
            val (_, combinedNumbers) = line.split(":")
            val (winningNumbersStr, numbersStr) = combinedNumbers.split("|")
            val winningNumbers = winningNumbersStr.trim().split(wsRegex).toSet()
            val numbers = numbersStr.trim().split(wsRegex)
            1.shl(numbers.count { winningNumbers.contains(it) } - 1)
        }
    }

    fun part2(input: List<String>): Int {
        val wsRegex = """\s+""".toRegex()

        val cardPoints = input.map { line ->
            val (_, combinedNumbers) = line.split(":")
            val (winningNumbersStr, numbersStr) = combinedNumbers.split("|")
            val winningNumbers = winningNumbersStr.trim().split(wsRegex).toSet()
            val numbers = numbersStr.trim().split(wsRegex)
            numbers.count { winningNumbers.contains(it) }
        }

        val memo = mutableMapOf<Int, Int>()
        fun winningScratchCardCount(i: Int): Int =
            memo.getOrPut(i) { 1 + (i + 1..i + cardPoints[i]).sumOf { winningScratchCardCount(it) } }

        return cardPoints.indices.sumOf { winningScratchCardCount(it) }
    }

    val testInput1 = readInput("Day04_1_test")
    check(part1(testInput1) == 13)
    check(part2(testInput1) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
