import kotlinx.collections.immutable.*
import kotlin.system.measureTimeMillis

sealed interface Module {
    val outputs: List<String>

    data class Broadcaster(override val outputs: List<String>) : Module
    data class FlipFlop(override val outputs: List<String>, val state: Boolean = false) : Module

    data class Conjunction(
        override val outputs: List<String>,
        val inputs: PersistentMap<String, Boolean> = persistentMapOf(),
    ) : Module
}

data class Pulse(val from: String, val to: String, val isHigh: Boolean)

fun main() {
    fun part1(input: List<String>): Long {
        val inputs = input
            .flatMap { line ->
                val (inputName, outputsStr) = line.split(" -> ")
                outputsStr
                    .split(", ")
                    .map { outputName ->
                        Pair(
                            if (inputName == "broadcaster") inputName else inputName.drop(1),
                            outputName,
                        )
                    }
            }
            .groupBy { (_, outputName) -> outputName }
            .mapValues { (_, inoutPuts) -> inoutPuts.map { (inputName) -> inputName } }

//        println(inputs)

        var modules = input
            .associate { line ->
                val (nameStr, outputsStr) = line.split(" -> ")
                val outputs = outputsStr.split(", ")

                if (nameStr == "broadcaster") {
                    Pair(nameStr, Module.Broadcaster(outputs))
                } else {
                    val name = nameStr.drop(1)
                    when (nameStr[0]) {
                        '%' -> Pair(name, Module.FlipFlop(outputs))
                        '&' -> Pair(
                            name,
                            Module.Conjunction(outputs, inputs[name]!!.associateWith { false }.toPersistentMap())
                        )

                        else -> never()
                    }
                }
            }
            .toPersistentMap()

        var highPulsesCount = 0L
        var lowPulsesCount = 0L
        tailrec fun PersistentList<Pulse>.recur() {
            if (isEmpty()) return
            val (from, to, isHigh) = first()
//            println("$from -${if (isHigh) "high" else "low"}-> $to")

            if (isHigh) {
                highPulsesCount++
            } else {
                lowPulsesCount++
            }

            when (val module = modules[to]) {
                is Module.Broadcaster -> {
                    removeAt(0).addAll(module.outputs.map { Pulse(to, it, false) })
                }

                is Module.FlipFlop -> {
                    if (isHigh.not()) {
                        modules = modules.put(to, module.copy(state = module.state.not()))

                        builder()
                            .apply {
                                removeAt(0)
                                addAll(module.outputs.map { Pulse(to, it, module.state.not()) })
                            }
                            .build()
                    } else {
                        removeAt(0)
                    }
                }

                is Module.Conjunction -> {
                    val state = module.inputs.put(from, isHigh)
                    modules = modules.put(to, module.copy(inputs = state))
                    val output = state.all { (_, state) -> state }.not()

                    builder()
                        .apply {
                            removeAt(0)
                            addAll(module.outputs.map { Pulse(to, it, output) })
                        }
                        .build()
                }

                null -> {
//                    println("output $")
                    this.removeAt(0)
                }
            }.recur()
        }

        repeat(1000) { persistentListOf(Pulse("button", "broadcaster", false)).recur() }
        return highPulsesCount * lowPulsesCount
    }

    fun part2(input: List<String>): Long {
        val inputs = input
            .flatMap { line ->
                val (inputName, outputsStr) = line.split(" -> ")
                outputsStr
                    .split(", ")
                    .map { outputName ->
                        Pair(
                            if (inputName == "broadcaster") inputName else inputName.drop(1),
                            outputName,
                        )
                    }
            }
            .groupBy { (_, outputName) -> outputName }
            .mapValues { (_, inoutPuts) -> inoutPuts.map { (inputName) -> inputName } }

        println(inputs)

        var modules = input
            .associate { line ->
                val (nameStr, outputsStr) = line.split(" -> ")
                val outputs = outputsStr.split(", ")

                if (nameStr == "broadcaster") {
                    Pair(nameStr, Module.Broadcaster(outputs))
                } else {
                    val name = nameStr.drop(1)
                    when (nameStr[0]) {
                        '%' -> Pair(name, Module.FlipFlop(outputs))
                        '&' -> Pair(
                            name,
                            Module.Conjunction(outputs, inputs[name]!!.associateWith { false }.toPersistentMap())
                        )

                        else -> never()
                    }
                }
            }
            .toPersistentMap()

        var highPulsesCount = 0L
        var lowPulsesCount = 0L
        tailrec fun PersistentList<Pulse>.recur(): Boolean {
            if (isEmpty()) return false
            val (from, to, isHigh) = first()
//            println("$from -${if (isHigh) "high" else "low"}-> $to")

            if (isHigh) {
                highPulsesCount++
            } else {
                lowPulsesCount++
            }

            if (to == "rx" && isHigh.not()) {
                return true
            }

            return when (val module = modules[to]) {
                is Module.Broadcaster -> {
                    removeAt(0).addAll(module.outputs.map { Pulse(to, it, false) })
                }

                is Module.FlipFlop -> {
                    if (isHigh.not()) {
                        modules = modules.put(to, module.copy(state = module.state.not()))

                        builder()
                            .apply {
                                removeAt(0)
                                addAll(module.outputs.map { Pulse(to, it, module.state.not()) })
                            }
                            .build()
                    } else {
                        removeAt(0)
                    }
                }

                is Module.Conjunction -> {
                    val state = module.inputs.put(from, isHigh)
                    modules = modules.put(to, module.copy(inputs = state))
                    val output = state.all { (_, state) -> state }.not()

                    builder()
                        .apply {
                            removeAt(0)
                            addAll(module.outputs.map { Pulse(to, it, output) })
                        }
                        .build()
                }

                null -> {
//                    println("output $")
                    this.removeAt(0)
                }
            }.recur()
        }

        fun foo(module: Module): String {
            return when (module) {
                is Module.Broadcaster -> "broadcaster"
                is Module.Conjunction -> "&${
                    module.inputs.toList().joinToString("") { if (it.second) "1" else "0" }
                }"

                is Module.FlipFlop -> "%${if (module.state) "1" else "0"}"
            }
        }

        repeat(Int.MAX_VALUE) {
//            println(it)
//            val oldModules = modules
            if (persistentListOf(Pulse("button", "broadcaster", false)).recur()) {
                println(it)
                throw IllegalStateException()
                return it.toLong()
            }
            println("${foo(modules["zh"]!!)} ${foo(modules["sx"]!!)} ${foo(modules["jt"]!!)} ${foo(modules["kb"]!!)} ${foo(modules["ks"]!!)}")
//            oldModules.forEach { (name, module) ->
//                if (module != modules[name]) {
//                    println("$name ${foo(module)} -> ${foo(modules[name]!!)}")
//                }
////                val x =
////
////                println("$name $x")
//            }
        }
        TODO()
    }

    val testInput1 = readLines("Day20_1_test")
//    check(part1(testInput1) == 32000000L)
    val testInput2 = readLines("Day20_2_test")
//    check(part1(testInput2) == 11_687_500L)

    val input = readLines("Day20")
//    measureTimeMillis { part1(input).println() }.also { println("time: $it") }
    part2(input).println()
}
