import kotlin.system.measureTimeMillis

fun never(): Nothing {
    throw IllegalStateException()
}

fun main() {
    fun part1(input: List<String>): Long {
        val hands = input.map { it.split(' ') }
        hands
            .sortedWith(compareBy<List<String>> { (cards, _) ->
                val kindCounts = cards
                    .toList().groupingBy { it }
                    .eachCount().toList()
                    .sortedByDescending { (_, count) -> count }
                    .map { (_, count) -> count }
                when (kindCounts[0]) {
                    5 -> 6 // 5 of a kind
                    4 -> 5 // 4 of a kind
                    3 -> when (kindCounts[1]) {
                        2 -> 4 // full house
                        else -> 3 // three of a kind
                    }

                    2 -> when (kindCounts[1]) {
                        2 -> 2 // two pair
                        else -> 1  // one pair
                    }

                    1 -> 0 // high card
                    else -> never()
                }
            }.thenBy { (cards, _) ->
                val cardMapping = "23456789TJQKA"
                cards.fold(0) { order, card -> (order * cardMapping.length) + cardMapping.indexOf(card) }
            })
            .mapIndexed { index, (_, bid) -> (index + 1) * bid.toLong() }
            .sum()
            .run { return this }
    }

    fun part2(input: List<String>): Long {
        val hands = input.map { it.split(' ') }
        hands
            .sortedWith(compareBy<List<String>> { (cards, _) ->
                val cardCountsList = cards.toList().groupingBy { it }.eachCount().toList()
                val (wildcardsList, nonWildcardsList) = cardCountsList.partition { (card, _) -> card == 'J' }
                val nonWildcardsSortedList = nonWildcardsList.sortedByDescending { (_, count) -> count }
                val wildcardCount = wildcardsList.sumOf { (_, count) -> count }
                val kindCounts = nonWildcardsSortedList.map { (_, count) -> count }.ifEmpty { listOf(0) }
                    .mapIndexed { index, i -> if (index == 0) i + wildcardCount else i }
                when (kindCounts[0]) {
                    5 -> 6 // 5 of a kind
                    4 -> 5 // 4 of a kind
                    3 -> when (kindCounts[1]) {
                        2 -> 4 // full house
                        else -> 3 // three of a kind
                    }

                    2 -> when (kindCounts[1]) {
                        2 -> 2 // two pair
                        else -> 1  // one pair
                    }

                    1 -> 0 // high card
                    else -> never()
                }
            }.thenBy { (cards, _) ->
                val cardMapping = "J23456789TQKA"
                cards.fold(0) { order, card -> (order * cardMapping.length) + cardMapping.indexOf(card) }
            })
            .mapIndexed { index, (_, bid) -> (index + 1) * bid.toLong() }
            .sum()
            .run { return this }
    }
    // test if implementation meets criteria from the description, like:
    val testInput1 = readLines("Day07_1_test")
    check(part1(testInput1) == 6440L)
    check(part2(testInput1) == 5905L)

    val input = readLines("Day07")
    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    measureTimeMillis { part2(input).println() }.also { println("time: $it") }
}
