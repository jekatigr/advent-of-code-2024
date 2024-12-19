package day17

import UNIT_TESTS_DIVIDER
import readInput
import testSolutions
import kotlin.math.pow

data class Command(val opcode: Long, val operand: Long)

class Computer(var A: Long, var B: Long, var C: Long, programString: String) {
    private val program: List<Command> = parseProgram(programString)

    private val output = mutableListOf<String>()

    private fun parseProgram(program: String): List<Command> {
        val arr = program.split(",")

        val commands = mutableListOf<Command>()

        var i = 0
        while (i <= arr.lastIndex) {
            commands += Command(arr[i].toLong(), arr[i + 1].toLong())

            i += 2
        }

        return commands
    }

    fun getComboOperandValue(operand: Long): Long {
        return when (operand) {
            in 0..3 -> operand
            4L -> A
            5L -> B
            6L -> C
            else -> {throw Error("Impossible")}
        }
    }

    fun getOutputForOneNumber(a: Long): Long {
        var b = a % 8
        b = b xor 4
        val c = a.toDouble() / 2.toDouble().pow(b.toDouble())
        b = b xor c.toLong()
        b = b xor 4

        return b % 8
    }

    fun bruteForce(start: Long, target: Int, slice: List<String>, fullProgram: String): Long {
        val expectedSlice = slice.reversed().joinToString(",")

        for (a in start until 9000000000000000L) {

            val result = getOutputForOneNumber(a)

            if (result.toInt() == target) {
                //println("target $target, a $a")

                var aTemp = a / 8
                var fits = true

                val computer = Computer(a, 0L, 0L, fullProgram)
                computer.runProgram()
                val output = computer.getOutput()

                if (output == expectedSlice) {
                    println("works for previous, a = $a")
                    println(expectedSlice)

                    return a
                }

//                for (i in slice.size - 2 downTo 0) {
//                    val value = getOutputForOneNumber(aTemp)
//
//                    if (value != slice[i].toLong()) {
//                        fits = false
//
//                        break
//                    }
//
//                    aTemp /= 8
//                }
//
//                if (fits) {
//                    println("works for previous, a = $a")
//                    println(slice.joinToString(","))
//
//                    return a
//                }
            }
        }

        throw Error("Undefined")
    }

    fun runBruteForce(programString: String) {
        val reversed = programString.split(",").reversed()

        var result = 0L

        for ((index, target) in reversed.withIndex()) {
            result *= 8

            val temp = bruteForce(result, target.toInt(), reversed.slice(0..index), programString)

            result = temp
        }

        println("result $result !!!!!")

        val computer = Computer(result, 0L, 0L, programString)
        computer.runProgram()
        println()
        println(programString)
        println(computer.getOutput())
    }

    fun runBruteForce2(programString: String) { // 35 and 39 is an answer for "3,0"
        val reversed = programString.split(",").reversed()

        println("start brute force 2")

        for (a in 1..200L) {
            val computer = Computer(a, 0L, 0L, programString)
            computer.runProgram()

            val output = computer.getOutput()

            if (output == "3,0") {
                println("a: $a")
                println(programString)
                println(output)
            } else {
//                println(output)
            }
        }
    }

    fun runProgram() {
        var instructionPointer = 0

        while (instructionPointer < program.size) {
            val command = program[instructionPointer]

            when (command.opcode) {
                0L -> {
                    val operand = getComboOperandValue(command.operand)
                    val temp = A.toDouble() / (2.toDouble().pow(operand.toDouble()))

                    A = temp.toLong()

                    instructionPointer += 1
                }
                1L -> {
                    B = B xor command.operand

                    instructionPointer += 1
                }
                2L -> {
                    val operand = getComboOperandValue(command.operand)

                    B = operand % 8

                    instructionPointer += 1
                }
                3L -> {
                    if (A == 0L) {
                        instructionPointer += 1

                        continue
                    }

                    instructionPointer = (command.operand / 2).toInt()
                }
                4L -> {
                    B = B xor C

                    instructionPointer += 1
                }
                5L -> {
                    val operand = getComboOperandValue(command.operand)

                    output += (operand % 8).toString()

                    instructionPointer += 1
                }
                6L -> {
                    val operand = getComboOperandValue(command.operand)
                    val temp = A.toDouble() / (2.toDouble().pow(operand.toDouble()))

                    B = temp.toLong()

                    instructionPointer += 1
                }
                7L -> {
                    val operand = getComboOperandValue(command.operand)
                    val temp = A.toDouble() / (2.toDouble().pow(operand.toDouble()))

                    C = temp.toLong()

                    instructionPointer += 1
                }
            }
        }
    }

    fun getOutput(): String {
        return output.joinToString(",")
    }

    fun cleanOutput() {
        output.clear()
    }
}

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun parseInput(input: List<String>): Computer {
        val a = input[0].removePrefix("Register A: ").toLong()
        val b = input[1].removePrefix("Register B: ").toLong()
        val c = input[2].removePrefix("Register C: ").toLong()

        val program = input[4].removePrefix("Program: ")

        return Computer(a, b, c, program)
    }

    fun part1(input: List<String>): String {
        val computer = parseInput(input)

        computer.runProgram()

        val output = computer.getOutput()

        println(output)

        return output
    }

    fun part2(input: List<String>): Long {
        val computer = parseInput(input)
        val initialProgram = input[4].removePrefix("Program: ")

//        for (a in 16247842866690..16247842866890) {
//            val comp1 = Computer(a, 0L, 0L, initialProgram)
//            comp1.runProgram()
//            println("$a -> ${comp1.getOutput()}")
//        }

        computer.runBruteForce(initialProgram)

        //computer.runBruteForce2(initialProgram)

//        var i = 1000000000000L
//        var prev = 0L
//        while (i < 100000000000000) {
//
//
//            computer.A = i
//            computer.cleanOutput()
//            computer.runProgram()
//            val output = computer.getOutput()
//
//            val value = output.replace(",", "").toLong()
//
//            if (value < prev) {
//                println("next value is less than prev, $prev, $value")
//            }
//
//            prev = value
//
//            //if (i % 10000 == 0L) {
//                println("$i $output ${output.split(",").size}")
//            //}
//
//            if (output == initialProgram) {
//                println("Result: $i")
//
//                break
//            }
//
//            i += 1
//        }

        return 0
    }

    //runDaySolutions(day, ::part1, ::part2, true)

    val dayStr = day.toString().padStart(2, '0')
    val testInput = readInput("day$dayStr/tests")

    //testSolutions(1, ::part1, getTestCases(testInput))

    // wrong: 15242076030203, 156985291375867, 1255882331006936, 121936608241624, 30666538968950, 17266599607643, 129982742933520, almost: 16247842866690, 288926726555138

    val mainInput = readInput("day17/main")

    val part1Result = part1(mainInput)
    println()
    println("=> Result: $part1Result")

    part2(mainInput)
}

/**
 * Function will split testcases from a raw input
 */
fun getTestCases(testInput: List<String>): List<Triple<List<String>, String, String>> {
    val testCases = mutableListOf<Triple<List<String>, String, String>>()

    var testCase: Triple<MutableList<String>, String, String> = Triple(mutableListOf(), "", "")
    var parseType = 1 // 1 - expected part 1, 2 - expected part 2, 3 - test case input strings

    for (line in testInput) {
        if (parseType == 1) {
            testCase = testCase.copy(second = line)
            parseType = 2
            continue
        }

        if (parseType == 2) {
            testCase = testCase.copy(third = line)
            parseType = 3
            continue
        }

        if (line == UNIT_TESTS_DIVIDER) {
            testCases.add(testCase)
            testCase = Triple(mutableListOf(), "", "")
            parseType = 1
            continue
        }

        testCase.first.add(line)
    }

    testCases.add(testCase)

    return testCases
}