fun never(): Nothing {
    throw IllegalStateException()
}

fun main() {
    fun part1(input: List<String>): Long {
        val hands = input.map { it.split(' ') }
        hands
            .sortedWith(compareBy<List<String>> { (cards, _) ->
                val counts =
                    cards.toList().groupingBy { it }.eachCount().toList().sortedByDescending { (_, count) -> count }
                when (counts[0].second) {
                    5 -> 6 // 5 of a kind
                    4 -> 5 // 4 of a kind
                    3 -> {
                        when (counts[1].second) {
                            2 -> 4 // full house
                            else -> 3 // three of a kind
                        }
                    }

                    2 -> {
                        when (counts[1].second) {
                            2 -> 2 // two pair
                            else -> 1  // one pair
                        }
                    }

                    1 -> 0 // high card
                    else -> never()
                }
            }.thenBy { (cards, _) ->
                cards.fold(0) { order, card ->
                    order * 13 + when (card) {
                        'A' -> 12
                        'K' -> 11
                        'Q' -> 10
                        'J' -> 9
                        'T' -> 8
                        '9' -> 7
                        '8' -> 6
                        '7' -> 5
                        '6' -> 4
                        '5' -> 3
                        '4' -> 2
                        '3' -> 1
                        '2' -> 0
                        else -> never()
                    }
                }
            })
            .mapIndexed { index, (_, bid) -> (index + 1) * bid.toLong() }
            .sum()
            .run { return this }
    }

    fun part2(input: List<String>): Long {
        val hands = input.map { it.split(' ') }
        hands
            .sortedWith(compareBy<List<String>> { (cards, _) ->
                var counts =
                    cards.toList().groupingBy { it }.eachCount().toList().sortedByDescending { (_, count) -> count }
                val wildcardCount = counts.firstOrNull { (card, _) -> card == 'J' }?.second ?: 0
                counts = counts.filter { (card, _) -> card != 'J' }.ifEmpty { listOf(Pair('K', 0)) }
                when (counts[0].second + wildcardCount) {
                    5 -> 6 // 5 of a kind
                    4 -> 5 // 4 of a kind
                    3 -> {
                        when (counts[1].second) {
                            2 -> 4 // full house
                            else -> 3 // three of a kind
                        }
                    }

                    2 -> {
                        when (counts[1].second) {
                            2 -> 2 // two pair
                            else -> 1  // one pair
                        }
                    }

                    1 -> 0 // high card
                    else -> never()
                }
            }.thenBy { (cards, _) ->
                cards.fold(0) { order, card ->
                    order * 13 + when (card) {
                        'A' -> 12
                        'K' -> 11
                        'Q' -> 10
                        'T' -> 9
                        '9' -> 8
                        '8' -> 7
                        '7' -> 6
                        '6' -> 5
                        '5' -> 4
                        '4' -> 3
                        '3' -> 2
                        '2' -> 1
                        'J' -> 0
                        else -> never()
                    }
                }
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
    part1(input).println()
    part2(input).println()
}
