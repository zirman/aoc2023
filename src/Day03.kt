fun main() {
    fun part1(input: List<String>): Int {
        val schematic = buildList {
            add(buildList {
                add('.')
                addAll(input[0].map { '.' })
                add('.')
            }) // padding for window
            addAll(input.map { line ->
                buildList {
                    add('.')
                    addAll(line.map { it })
                    add('.')
                }
            })
            add(buildList {
                add('.')
                addAll(input[0].map { '.' })
                add('.')
            }) // padding for window
        }

        schematic
            .windowed(3, 1) { (above, line, below) ->
                buildList {
                    var x = 1

                    while (true) {
                        while (line[x].isDigit().not()) {
                            x++
                            if (x >= line.size - 1) {
                                return@buildList
                            }
                        }

                        val start = x
                        val digits = buildList {
                            do {
                                add(line[x])
                                x++
                            } while (line[x].isDigit())
                        }

                        if (
                            above.slice(start - 1..start + digits.size)
                                .any { it.isDigit().not() && it != '.' } ||
                            line.slice(start - 1..start + digits.size)
                                .any { it.isDigit().not() && it != '.' } ||
                            below.slice(start - 1..start + digits.size)
                                .any { it.isDigit().not() && it != '.' }
                        ) {
                            add(digits.joinToString("") { it.toString() }.toInt())
                        }
                    }
                }
            }
            .flatten()
            .sum()
            .run { return this }
    }

    fun part2(input: List<String>): Int {
        val schematic = buildList {
            addAll(input.map { line ->
                buildList {
                    addAll(line.map { it })
                }
            })
        }

        fun List<Char>.toInt(): Int = joinToString("") { it.toString() }.toInt()

        schematic
            .indices.flatMap { y ->
                schematic[y]
                    .mapIndexedNotNull { x, c -> if (c == '*') x else null }
                    .mapNotNull { x ->
                        buildList {
                            fun tryAddNumberAt(x: Int, y: Int) {
                                schematic[y]
                                    .let { it.subList(x, it.size) }
                                    .takeWhile { it.isDigit() }
                                    .ifEmpty { null }
                                    ?.toInt()
                                    ?.run { add(this) }
                            }

                            fun tryAddNumberBackwardsAt(x: Int, y: Int) {
                                schematic[y]
                                    .subList(0, x + 1)
                                    .asReversed()
                                    .takeWhile { it.isDigit() }
                                    .ifEmpty { null }
                                    ?.asReversed()
                                    ?.toInt()
                                    ?.run { add(this) }
                            }

                            fun tryAddAdjacentNumbers(y: Int) {
                                val adjacent = schematic[y]
                                val leftCorner = adjacent[x - 1].isDigit()
                                val middle = adjacent[x].isDigit()
                                val rightCorner = adjacent[x + 1].isDigit()

                                // two separate numbers in corners
                                if (leftCorner && middle.not() && rightCorner) {
                                    tryAddNumberBackwardsAt(x = x - 1, y = y)
                                    tryAddNumberAt(x = x + 1, y = y)
                                } else if (leftCorner || middle || rightCorner) {
                                    tryAddNumberAt(
                                        x = adjacent
                                            .subList(0, x + 2)
                                            .dropLastWhile { it.isDigit().not() }
                                            // if not found -1 is returned which still works out to the correct index
                                            .indexOfLast { it.isDigit().not() } + 1,
                                        y = y
                                    )
                                }
                            }

                            tryAddNumberAt(x = x + 1, y = y)
                            tryAddNumberBackwardsAt(x = x - 1, y = y)
                            tryAddAdjacentNumbers(y = y - 1)
                            tryAddAdjacentNumbers(y = y + 1)
                        }   // remove parts that are not connected
                            .takeIf { it.size > 1 }
                            // get gear ratio
                            ?.reduce { a, b -> a * b }
                    }
            }
            // add up gear ratios
            .sum()
            .run { return this }
    }

    val testInput1 = readInput("Day03_1_test")
    check(part1(testInput1) == 4361)
    check(part2(testInput1) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
