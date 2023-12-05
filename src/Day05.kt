import kotlin.math.min

fun main() {
    fun part1(input: String): Long {
        val headingRegex = """(\w+)-to-(\w+) map:""".toRegex()
        val lines = input.split("\n\n")
        val seeds = lines[0].split("""(:|\s)+""".toRegex()).drop(1)

        val mappings: Map<String, Pair<String, List<Pair<LongRange, Long>>>> = buildMap {
            lines.drop(1).forEach { mapping ->
                val mappingLines = mapping.split('\n')
                val (source, destination) = headingRegex.matchEntire(mappingLines[0])!!.destructured

                this[source] = Pair(
                    destination,
                    buildList {
                        mappingLines.drop(1).filter { it.isNotBlank() }.forEach { line ->
                            val (startDestination, startSource, length) = line.split(' ')

                            add(
                                Pair(
                                    startSource.toLong()..<startSource.toLong() + length.toLong(),
                                    startDestination.toLong()
                                )
                            )
                        }
                    }
                )
            }
        }

        fun findMappingFor(source: String, id: Long): Long {
            val (destination, mapping) = mappings[source] ?: return id
            return findMappingFor(
                source = destination,
                id = mapping
                    .firstOrNull { (sourceRange) -> sourceRange.contains(id) }
                    ?.let { (sourceRange, startDestination) -> (id - sourceRange.start) + startDestination }
                    ?: id
            )
        }

        return seeds
            .map { it.toLong() }
            .map { findMappingFor("seed", it) }
            .min()
    }

    fun part2(input: String): Long {
        val headingRegex = """(\w+)-to-(\w+) map:""".toRegex()
        val lines = input.split("\n\n")
        val seeds = lines[0].split("""(:|\s)+""".toRegex()).drop(1)

        val mappings: Map<String, Pair<String, List<Pair<LongRange, Long>>>> = buildMap {
            lines.drop(1).forEach { mapping ->
                val mappingLines = mapping.split('\n')
                val (source, destination) = headingRegex.matchEntire(mappingLines[0])!!.destructured

                this[source] = Pair(
                    destination,
                    buildList {
                        mappingLines.drop(1).filter { it.isNotBlank() }.forEach { line ->
                            val (startDestination, startSource, length) = line.split(' ')

                            add(
                                Pair(
                                    startSource.toLong()..<startSource.toLong() + length.toLong(),
                                    startDestination.toLong()
                                )
                            )
                        }
                    }
                )
            }
        }

        fun findMinFor(source: String, index: Int, sourceRange: LongRange): Long {
            val (destination, mapping) = mappings[source] ?: return sourceRange.start
            val (range, startDestination) = mapping.getOrNull(index) ?: return findMinFor(destination, 0, sourceRange)

            return when {
                range.contains(sourceRange.start) && range.contains(sourceRange.endInclusive) -> findMinFor(
                    source = destination,
                    index = 0,
                    sourceRange = startDestination + sourceRange.start - range.start..(startDestination + sourceRange.start - range.start) + sourceRange.endInclusive - sourceRange.start
                )

                range.contains(sourceRange.start) && range.contains(sourceRange.endInclusive).not() -> min(
                    findMinFor(
                        source = destination,
                        index = 0,
                        sourceRange = startDestination + (sourceRange.start - range.start)..
                                (startDestination + (sourceRange.start - range.start)) + range.endInclusive - sourceRange.start
                    ),
                    findMinFor(
                        source = source,
                        index = index + 1,
                        sourceRange = range.endInclusive + 1..sourceRange.endInclusive
                    )
                )

                range.contains(sourceRange.start).not() && range.contains(sourceRange.endInclusive) -> min(
                    findMinFor(
                        source = source,
                        index = index + 1,
                        sourceRange = sourceRange.start..<range.start
                    ),
                    findMinFor(
                        source = destination,
                        index = 0,
                        sourceRange = startDestination..startDestination + sourceRange.endInclusive - range.start
                    ),
                )

                range.contains(sourceRange.start).not() &&
                        range.contains(sourceRange.endInclusive).not() &&
                        (sourceRange.endInclusive < range.start ||
                                sourceRange.start > range.endInclusive) -> findMinFor(
                    source = source,
                    index = index + 1,
                    sourceRange = sourceRange
                )

                else -> min(
                    min(
                        findMinFor(
                            source = source,
                            index = index + 1,
                            sourceRange = sourceRange.start..<range.start
                        ),
                        findMinFor(
                            source = destination,
                            index = 0,
                            sourceRange = startDestination..startDestination + range.endInclusive - range.start
                        ),
                    ),
                    findMinFor(
                        source = source,
                        index = index + 1,
                        sourceRange = range.endInclusive + 1..sourceRange.endInclusive
                    ),
                )
            }
        }

        return seeds
            .chunked(2)
            .minOf { (startSource, length) ->
                findMinFor(
                    source = "seed",
                    index = 0,
                    sourceRange = startSource.toLong()..<startSource.toLong() + length.toLong(),
                )
            }
    }

    val testInput1 = readFile("Day05_1_test")
    check(part1(testInput1) == 35L)
    check(part2(testInput1) == 46L)

    val input = readFile("Day05")
    part1(input).println()
    part2(input).println()
}
