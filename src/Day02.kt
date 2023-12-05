fun main() {
    val gameRegex = """Game (\d+)""".toRegex()

    fun String.toCubesMap(): Pair<Int, Map<String, List<Int>>> {
        val (game, cubes) = split(": ")
        val (gameNumber) = gameRegex.matchEntire(game)!!.destructured

        cubes
            .split("; ")
            .flatMap { it.split(", ") }
            .map {
                val (count, color) = it.split(" ")
                Pair(color, count.toInt())
            }
            .groupBy { (color) -> color }
            .mapValues { (_, cubeCounts) -> cubeCounts.map { (_, count) -> count } }
            .run { return Pair(gameNumber.toInt(), this) }
    }

    fun part1(input: List<String>): Int {
        input
            .map { line ->
                val (gameNumber, cubesMap) = line.toCubesMap()
                cubesMap
                    .mapValues { (_, cubeCounts) -> cubeCounts.max() }
                    .let { Pair(gameNumber, it) }
            }
            .filter { (_, cubeCountMap) ->
                cubeCountMap["red"]!! <= 12 && cubeCountMap["green"]!! <= 13 && cubeCountMap["blue"]!! <= 14
            }
            .sumOf { (gameNumber) -> gameNumber }
            .run { return this }
    }

    fun part2(input: List<String>): Int {
        input
            .map { line ->
                val (gameNumber, cubesMap) = line.toCubesMap()
                cubesMap
                    .mapValues { (_, cubesCount) -> cubesCount.max() }
                    .values.reduce { count1, count2 -> count1 * count2 }
                    .let { Pair(gameNumber, it) }
            }
            .sumOf { (_, cubesCount) -> cubesCount }
            .run { return this }
    }

    val testInput1 = readLines("Day02_1_test")
    check(part1(testInput1) == 8)
    check(part2(testInput1) == 2286)

    val input = readLines("Day02")
    part1(input).println()
    part2(input).println()
}
