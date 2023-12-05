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

        val memo = mutableMapOf<Pair<String, Long>, Long>()
        fun findMappingFor(source: String, id: Long): Long {
            return memo.getOrPut(Pair(source, id)) {
                val (destination, mapping) = mappings[source] ?: return id
                findMappingFor(
                    source = destination,
                    id = mapping
                        .firstOrNull { (sourceRange) -> sourceRange.contains(id) }
                        ?.let { (sourceRange, startDestination) -> (id - sourceRange.start) + startDestination }
                        ?: id
                )
            }
        }

        return seeds
            .chunked(2)
            .asSequence()
            .flatMap { (startSource, length) ->
                (startSource.toLong()..<startSource.toLong() + length.toLong()).map {
                    findMappingFor("seed", it)
                }
            }
            .min()
    }

    val testInput1 = readFile("Day05_1_test")
    check(part1(testInput1) == 35L)
    check(part2(testInput1) == 46L)

    val input = readFile("Day05")
    part1(input).println()
//    part2(input).println()
}
