fun main() {
    fun part1(input: List<String>): Int {
        val wsRegex = """\s+""".toRegex()

        input
            .sumOf { line ->
                val (_, combinedNumbers) = line.split(":")
                val (winningNumbersStr, numbersStr) = combinedNumbers.split("|")
                val winningNumbers = winningNumbersStr.trim().split(wsRegex).toSet()
                val numbers = numbersStr.trim().split(wsRegex)
                1.shl(numbers.count { winningNumbers.contains(it) } - 1)
            }
            .run { return this }
    }

    fun part2(input: List<String>): Int {
        val wsRegex = """\s+""".toRegex()
        val cardPoints = input
            .map { line ->
                val (_, combinedNumbers) = line.split(":")
                val (winningNumbersStr, numbersStr) = combinedNumbers.split("|")
                val winningNumbers = winningNumbersStr.trim().split(wsRegex).map { it.toInt() }.toSet()
                val numbers = numbersStr.trim().split(wsRegex).map { it.toInt() }
                numbers.count { winningNumbers.contains(it) }
            }

        fun winningScratchCardCount(i: Int): Int {
            val memo = mutableMapOf<Int, Int>()
            fun treeRecur(i: Int): Int = memo.getOrPut(i) { 1 + (i + 1..i + cardPoints[i]).sumOf { treeRecur(it) } }
            return treeRecur(i)
        }

        return cardPoints.indices.sumOf { winningScratchCardCount(it) }
    }

    val testInput1 = readInput("Day04_1_test")
    check(part1(testInput1) == 13)
    check(part2(testInput1) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
