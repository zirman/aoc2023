import kotlin.system.measureTimeMillis

fun main() {
    fun part1(input: List<String>): Long {
        fun String.hash(): Long {
            return fold(0L) { acc, c -> ((acc + c.code) * 17).mod(256L) }
        }

        input
            .flatMap { line -> line.split(",") }
            .sumOf { it.hash() }
            .run { return this }
    }

    fun part2(input: List<String>): Long {
        fun String.hash(): Int {
            return fold(0) { acc, c -> ((acc + c.code) * 17).mod(256) }
        }

        val boxes = (1..256).map { mutableMapOf<String, String>() }

        input
            .flatMap { line -> line.split(",") }
            .forEach { inst ->
                val (hashCode, op) = inst.partition { it.isLetter() }

                when (op[0]) {
                    '-' -> {
                        boxes[hashCode.hash()].remove(hashCode)
                    }

                    '=' -> {
                        val focalLengthStr = op.substringAfter("=")
                        if (boxes[hashCode.hash()].replace(hashCode, focalLengthStr) == null) {
                            boxes[hashCode.hash()][hashCode] = focalLengthStr
                        }
                    }
                }
            }

        boxes
            .mapIndexed { boxIndex, mutableMap ->
                mutableMap
                    .toList()
                    .mapIndexed { slotIndex, (_, focalLength) ->
                        (boxIndex + 1) * (slotIndex + 1) * focalLength.toLong()
                    }
                    .sum()
            }
            .sum()
            .run { return this }
    }

    val testInput1 = readLines("Day15_1_test")
    check(part1(testInput1) == 1320L)
    check(part2(testInput1) == 145L)

    val input = readLines("Day15")
    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    measureTimeMillis { part2(input).println() }.also { println("time: $it") }
}
