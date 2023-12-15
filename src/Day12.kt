import kotlinx.coroutines.coroutineScope

suspend fun main() {
    fun part1(input: List<String>): Long {
        input
            .map { line ->
                val (springsString, runLengths) = line.split(' ')
                val groups = runLengths.split(',')
                Pair(
                    springsString.toList(),
                    groups.map { it.toInt() },
                )
            }
            .sumOf { (springs, brokenLengths) ->
                fun recur(index: Int, brokenLengths: List<Int>): Long {
                    if (brokenLengths.isEmpty()) {
                        return if ((index..<springs.size).all { springs[it] != '#' }) 1 else 0
                    }
                    val brokenLength = brokenLengths[0]
                    val restMinLength = brokenLength + brokenLengths.subList(1, brokenLengths.size).sumOf { it + 1 }
                    (index..((index..springs.size - restMinLength)
                        .find { springs[it] == '#' }
                        ?: (springs.size - restMinLength)))
                        .dropWhile { springs[it] == '.' }
                        .sumOf { index ->
                            if (springs.subList(index, index + brokenLength).all { it != '.' } &&
                                (index + brokenLength == springs.size || springs[index + brokenLength] != '#'))
                                recur(index + brokenLength + 1, brokenLengths.drop(1)) else 0
                        }
                        .run { return this }
                }

                recur(0, brokenLengths)
            }
            .run { return this }
    }

    suspend fun part2(input: List<String>): Long = coroutineScope {
        input
            .map { line ->
                val (springsString, runLengths) = line.split(' ')
                val groups = runLengths.split(',')

                Pair(
                    generateSequence { springsString }
                        .take(5)
                        .joinToString("?")
                        .split("""\.+""".toRegex())
                        .map { it.toList() },
                    buildList { repeat((1..5).count()) { addAll(groups.map { it.toInt() }) } },
                )
            }
            .sumOf { (springs, brokenLengths) ->
                fun matchGroup(
                    springs: List<Char>,
                    brokenLengths: List<Int>
                ): List<Pair<Long, List<Int>>> {
//                    println("matchGroup $springs $brokenLengths")
                    if (springs.isEmpty()) {
                        return listOf(Pair(1, brokenLengths))
                    }
                    if (brokenLengths.isEmpty()) {
                        return if (springs.all { it == '?' }) {
                            listOf(Pair(1, emptyList()))
                        } else {
                            listOf()
                        }
                    }
//                    println("wat $brokenLengths")
                    (0..brokenLengths.size)
                        .map {
                            Pair(
                                brokenLengths.slice(0..<it),
                                brokenLengths.slice(it..<brokenLengths.size),
                            )
                        }
                        .mapNotNull { (brokenLengths, remainingBrokenLengths) ->
//                            println("wat $brokenLengths $remainingBrokenLengths")
//
                            fun List<Char>.match(brokenLengths: List<Int>): Long {
//                                println("match $this $brokenLengths")
                                return if (brokenLengths.isEmpty()) {
                                    if (all { it == '?' }) 1 else 0
                                } else {
                                    var x = indexOfFirst { it == '#' }
                                    if (x != -1) {
                                        x = kotlin.math.min(x, size - (brokenLengths[0] + brokenLengths.drop(1).sumOf { it + 1 }))
                                    } else {
                                        x = size - (brokenLengths[0] + brokenLengths.drop(1).sumOf { it + 1 })
                                    }
                                    (0..x)
                                        .filter {
//                                            println("lala ${it} ${brokenLengths[0]} ${it + brokenLengths[0] == size} ${this[it + brokenLengths[0]] == '?'}")
                                            it + brokenLengths[0] == size || this[it + brokenLengths[0]] == '?'
                                        }
                                        .sumOf { slice(it + brokenLengths[0] + 1..<size).match(brokenLengths.drop(1)) }
                                }
                            }

//                            println("bazy $brokenLengths $remainingBrokenLengths")
                            springs
                                .match(brokenLengths)
                                .takeIf { it > 0 }
                                ?.let {
                                    Pair(
                                        it,
                                        remainingBrokenLengths
                                    )//.also { println("baz $brokenLengths $remainingBrokenLengths") }
                                }
                        }
//                        .also { println("matchGroup return $it") }
                        .run { return this }
                }

                fun foo(group: List<List<Char>>, brokenLengths: List<Int>): Long {
//                    println("foo $group $brokenLengths")
                    return if (group.isEmpty()) {
                        if (brokenLengths.isEmpty()) 1 else 0
                    } else {
                        matchGroup(group.first(), brokenLengths).sumOf { (matches, brokenLengths) ->
//                            println("a $matches $brokenLengths")
                            matches * foo(group.drop(1), brokenLengths)
                        }
                    }
                }

                foo(springs, brokenLengths)//.also { println("foo return $it") }
            }
            .run { return@coroutineScope this }
    }

    val testInput1 = readLines("Day12_1_test")
//    check(part1(testInput1) == 21L)
//    check(part2(listOf("???.### 1,1,3")) == 1L)
//    check(part2(listOf(".??..??...?##. 1,1,3")) == 4L)
//    check(part2(listOf("#?## 1,2")) == 1L)
//    check(part2(listOf("????.#...#... 4,1,1")) == 1L)
//    check(part2(listOf("????.######..#####. 1,6,5")) == 4L)
//    check(part2(listOf("??????? 2,1")) == 10L)
//    check(part2(testInput1) == 21L)
//    check(part2(listOf("??.?? 2,2")) == 1L)
//    check(part2(listOf("???.### 1,1,3")) == 1L)
//    check(part2(listOf(".??..??...?##. 1,1,3")) == 16384L)
//    check(part2(listOf("#?##?# 1,2,1")) == 1L)
    check(part2(listOf("????.#...#... 4,1,1")) == 16L)
    check(part2(listOf("????.######..#####. 1,6,5")) == 2500L)
    check(part2(listOf("?###???????? 3,2,1")) == 506250L)
//    check(part2(testInput1) == 525152L)
    println("wat")

    val input = readLines("Day12")
//    part1(input).println()
    part2(input).println()
}
