import kotlin.system.measureTimeMillis

data class Rule(val category: Char, val op: String, val value: Int, val destination: String)
data class Workflow(val rules: List<Rule>, val finalDestination: String)
data class CategoryRange(val category: Char, val range: IntRange)

fun toRange(op: String, value: Int): IntRange = when (op) {
    "<" -> Int.MIN_VALUE..<value
    ">" -> value + 1..Int.MAX_VALUE
    else -> never()
}

fun intersectRange(a: IntRange, b: IntRange): IntRange {
    @Suppress("EmptyRange")
    if (a.isEmpty() || b.isEmpty()) return 0..-1
    return if (a.contains(b.first)) {
        if (a.contains(b.last)) {
            b
        } else {
            b.first..a.last
        }
    } else if (a.contains(b.last)) {
        a.first..b.last
    } else if (b.contains(a.first)) {
        if (b.contains(a.last)) {
            a
        } else {
            a.first..b.last
        }
    } else {
        @Suppress("EmptyRange")
        0..-1
    }
}

fun main() {
    fun part1(input: String): Long {
        val (rulesStr, partsStr) = input.trim('\n').split("\n\n")
        val rules = rulesStr.split('\n').associate { ruleStr ->
            val ruleName = ruleStr.takeWhile { it != '{' }
            val ruleParts = ruleStr.dropWhile { it != '{' }.trimStart('{').trimEnd('}').split(',')
            val rules = ruleParts.dropLast(1).map {
                val (predicateStr, destination) = it.split(':')
                val (category, op, valueStr) = """(\w+)([<>])(\w+)""".toRegex().matchEntire(predicateStr)!!.destructured
                Rule(
                    category = category[0],
                    op = op,
                    value = valueStr.toInt(),
                    destination = destination,
                )
            }
            Pair(ruleName, Pair(rules, ruleParts.last()))
        }

        val parts = partsStr.split('\n').map {
            it.trimStart('{').trimEnd('}').split(',').associate {
                val (category, valueStr) = it.split('=')
                Pair(category[0], valueStr.toInt())
            }
        }

        parts
            .map { part ->
                tailrec fun nextDestination(i: Int, rules: List<Rule>, finalDestination: String): String {
                    if (i == rules.size) return finalDestination
                    val rule = rules[i]
                    when (rule.op) {
                        ">" -> if (part[rule.category]!! > rule.value) {
                            return rule.destination
                        }

                        "<" -> if (part[rule.category]!! < rule.value) {
                            return rule.destination
                        }

                        else -> never()
                    }

                    return nextDestination(i + 1, rules, finalDestination)
                }


                tailrec fun recur(destination: String): Long {
                    return when (destination) {
                        "A" -> {
                            part.values.sum().toLong()
                        }

                        "R" -> {
                            0L
                        }

                        else -> {
                            val x = rules[destination]!!
                            recur(nextDestination(0, x.first, x.second))
                        }
                    }
                }

                recur("in")
            }
            .sum()
            .run { return this }
    }

    fun part2(input: String): Long {
        val (workflowsStr, _) = input.trim('\n').split("\n\n")
        val workflows = workflowsStr.split('\n').associate { ruleStr ->
            val workflowName = ruleStr.takeWhile { it != '{' }
            val ruleParts = ruleStr.dropWhile { it != '{' }.trimStart('{').trimEnd('}').split(',')
            val rules = ruleParts.dropLast(1).map {
                val (predicateStr, destination) = it.split(':')
                val (category, op, valueStr) = """(\w+)([<>])(\w+)""".toRegex().matchEntire(predicateStr)!!.destructured
                Rule(
                    category = category[0],
                    op = op,
                    value = valueStr.toInt(),
                    destination = destination,
                )
            }
            Pair(workflowName, Workflow(rules, ruleParts.last()))
        }

        fun Rule.categoryRanges(): Pair<CategoryRange, CategoryRange> {
            return Pair(
                CategoryRange(category = category, range = toRange(op, value)),
                when (op) {
                    ">" -> CategoryRange(category = category, range = toRange("<", value + 1))
                    "<" -> CategoryRange(category = category, range = toRange(">", value - 1))
                    else -> never()
                },
            )
        }

        fun search(ruleIndex: Int, workflow: Workflow): List<List<CategoryRange>> {
            if (ruleIndex == workflow.rules.size) {
                return when (workflow.finalDestination) {
                    "A" -> {
                        listOf(emptyList())
                    }

                    "R" -> {
                        emptyList()
                    }

                    else -> search(0, workflows[workflow.finalDestination]!!)
                }
            }

            val rule = workflow.rules[ruleIndex]

            val ruleTrue = when (rule.destination) {
                "A" -> {
                    listOf(emptyList())
                }

                "R" -> {
                    emptyList()
                }

                else -> search(0, workflows[rule.destination]!!)
            }

            val ruleFalse = search(ruleIndex + 1, workflow)

            val (categoryRange1, categoryRange2) = rule.categoryRanges()

            return buildList {
                addAll(ruleTrue.map { it + categoryRange1 })
                addAll(ruleFalse.map { it + categoryRange2 })
            }
        }


        return search(0, workflows["in"]!!).sumOf { categoryRangeList ->
            val c = categoryRangeList
                .groupBy { categoryRange -> categoryRange.category }
                .mapValues { (_, value) -> value.map { it.range } }

            tailrec fun recur(i: Int, l: List<IntRange>, r: IntRange): IntRange {
                if (i == l.size) return r
                if (r.isEmpty()) return r
                return recur(i + 1, l, intersectRange(l[i], r))
            }

            "xmas"
                .map { recur(0, c[it] ?: emptyList(), 1..4000) }
                .fold(1L) { a, b -> b.count() * a }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readFile("Day19_1_test")
    check(part1(testInput1) == 19114L)
    check(part2(testInput1) == 167409079868000L)

    val input = readFile("Day19")
    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    measureTimeMillis { part2(input).println() }.also { println("time: $it") }
}
