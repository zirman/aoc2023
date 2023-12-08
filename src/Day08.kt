import kotlin.system.measureTimeMillis

fun main() {
    fun part1(input: String): Int {
        val regex = """(\w\w\w) = \((\w\w\w), (\w\w\w)\)""".toRegex()
        val (instructionsString, networkStrings) = input.split("\n\n")

        val map = networkStrings.split('\n').filter { it.isNotEmpty() }.associate { line ->
            val (node, leftNode, rightNode) = regex.matchEntire(line)!!.destructured
            Pair(node, Pair(leftNode, rightNode))
        }

        val instructionsList = instructionsString.toList()
        sequence { while (true) yieldAll(instructionsList) }
            .scan("AAA") { location, direction ->
                when (direction) {
                    'L' -> map[location]!!.first
                    'R' -> map[location]!!.second
                    else -> never()
                }
            }
            .takeWhile { location -> location != "ZZZ" }
            .count()
            .run { return this }
    }

    fun part2(input: String): Long {
        val regex = """(\w\w\w) = \((\w\w\w), (\w\w\w)\)""".toRegex()
        val (instructionsString, networkStrings) = input.split("\n\n")

        val map = networkStrings.split('\n').filter { it.isNotEmpty() }.associate { line ->
            val (node, leftNode, rightNode) = regex.matchEntire(line)!!.destructured
            Pair(node, Pair(leftNode, rightNode))
        }

        val instructionsList = instructionsString.toList()

        fun search(startLocation: String): List<Long> {
            val visited = mutableSetOf<String>()
            val endings = mutableListOf<Long>()
            var depth = 0L
            var location = startLocation
            val directionIterator = sequence { while (true) yieldAll(instructionsList) }.iterator()

            while (true) {
                if (visited.contains(location)) {
                    return endings
                }

                if (location.endsWith('Z')) {
                    endings.add(depth)
                    visited.add(location)
                }

                location = when (directionIterator.next()) {
                    'L' -> map[location]!!.first
                    'R' -> map[location]!!.second
                    else -> never()
                }

                depth++
            }
        }

        val x = map.keys
            .filter { it.endsWith('A') }
            .map { search(it).last().toLong() }
        val xmax = x.max()
        return (1L..Long.MAX_VALUE).first {
            val q = it * xmax
            x.all { q % it == 0L }
        } * xmax
    }

    val testInput1 = readFile("Day08_1_test")
    check(part1(testInput1) == 6)
    val testInput2 = readFile("Day08_2_test")
    check(part2(testInput2) == 6L)

    val input = readFile("Day08")
    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    measureTimeMillis { part2(input).println() }.also { println("time: $it") }
}
