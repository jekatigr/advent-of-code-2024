import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("./src/$name.txt").readText().trim().lines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

/**
 * Creates String id by coordinates
 */
fun getIdByXY(x: Int, y: Int) = "$x-$y"

enum class Side {
    UP, DOWN, LEFT, RIGHT
}

fun <T> printMatrix(matrix: Array<Array<T>>) {
    for (row in matrix) {
        println(row.joinToString(" "))
    }
}

/**
 * Returns vertical and horizontal neighbor coordinates
 */
fun getDirections(x: Int, y: Int): Array<Triple<Int, Int, Side>> {
    return arrayOf(
        Triple(x - 1, y, Side.UP),
        Triple(x + 1, y, Side.DOWN),
        Triple(x, y - 1, Side.LEFT),
        Triple(x, y + 1, Side.RIGHT),
    )
}

fun getSideFromChar(char: Char): Side {
    when (char) {
        '^' -> return Side.UP
        '>' -> return Side.RIGHT
        '<' -> return Side.LEFT
        'v' -> return Side.DOWN
    }

    throw Error("Invalid char '$char'")
}

/**
 * Checks if coordinates of a cell is in matrix area
 */
fun checkIfInMatrixArea(matrix: Array<Array<Char>>, i: Int, j: Int): Boolean {
    return !(i < 0 || j < 0 || i >= matrix.size || j >= matrix[0].size)
}

/**
 * Run solution for test-case
 */
fun testSolution(part: Int, testCase: Int, solution: (input: List<String>) -> Long, testInput: List<String>, expectedTestResult: Long) {
    val solutionTestResult = solution(testInput)
    check(solutionTestResult == expectedTestResult) { "Part $part, testcase $testCase: returned value is $solutionTestResult instead of $expectedTestResult" }
}

const val UNIT_TESTS_DIVIDER = "==="

/**
 * Function will split testcases from a raw input
 */
fun getTestCases(testInput: List<String>): List<Triple<List<String>, Long, Long>> {
    val testCases = mutableListOf<Triple<List<String>, Long, Long>>()

    var testCase: Triple<MutableList<String>, Long, Long> = Triple(mutableListOf(), 0L, 0L)
    var parseType = 1 // 1 - expected part 1, 2 - expected part 2, 3 - test case input strings

    for (line in testInput) {
        if (parseType == 1) {
            testCase = testCase.copy(second = line.toLong())
            parseType = 2
            continue
        }

        if (parseType == 2) {
            testCase = testCase.copy(third = line.toLong())
            parseType = 3
            continue
        }

        if (line == UNIT_TESTS_DIVIDER) {
            testCases.add(testCase)
            testCase = Triple(mutableListOf(), 0L, 0L)
            parseType = 1
            continue
        }

        testCase.first.add(line)
    }

    testCases.add(testCase)

    return testCases
}

/**
 * Run all test cases for test input
 */
fun testSolutions(part: Int, solution: (input: List<String>) -> Long, testCases: List<Triple<List<String>, Long, Long>>) {
    for ((index, value) in testCases.withIndex()) {
        val (inputStrings, expectedPt1, expectedPt2) = value

        val expected = if (part == 1) expectedPt1 else expectedPt2

        testSolution(part, index + 1, solution, inputStrings, expected)
    }
}

/**
 * Run all tests and solutions
 */
fun runDaySolutions(day: Int, solutionPart1: (input: List<String>) -> Long, solutionPart2: (input: List<String>) -> Long, skipPart2: Boolean = false, skipTests: Boolean = false) {
    val dayStr = day.toString().padStart(2, '0')

    println()
    println("======= AoC: Day $day =======")
    println()

    val testInput = readInput("day$dayStr/tests")
    val input = readInput("day$dayStr/main")

    val testCases = getTestCases(testInput);

    println("------- Part 1 -------")
    if (!skipTests) {
        testSolutions(1, solutionPart1, testCases)
        println("${testCases.size} testcase(s) passed")
    } else {
        println("${testCases.size} testcase(s) skipped")
    }

    val part1Result = solutionPart1(input)
    println()
    println("=> Result: $part1Result")

    if (skipPart2) {
        println()
        println("Part 2 skipped")
        println()
        println("------- All done -------")

        return
    }

    println()
    println("------- Part 2 -------")
    if (!skipTests) {
        testSolutions(2, solutionPart2, testCases)
        println("${testCases.size} testcase(s) passed")
    } else {
        println("${testCases.size} testcase(s) skipped")
    }

    val part2Result = solutionPart2(input)
    println()
    println("=> Result: $part2Result")

    println()
    println("------- All done -------")
}
