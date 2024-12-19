import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("./src/$name.txt").readText().trim().lines()

/**
 * Creates String id by coordinates
 */
fun getIdByXY(x: Int, y: Int) = "$x-$y"

enum class Side {
    UP, DOWN, LEFT, RIGHT;

    fun opposite(): Side {
        return when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
    }
}

fun <T> printMatrix(matrix: Array<Array<T>>) {
    for (row in matrix) {
        println(row.joinToString(" "))
    }
}

/**
 * Returns vertical and horizontal neighbor coordinates
 */
fun getDirections(i: Int, j: Int): Array<Triple<Int, Int, Side>> {
    return arrayOf(
        Triple(i - 1, j, Side.UP),
        Triple(i + 1, j, Side.DOWN),
        Triple(i, j - 1, Side.LEFT),
        Triple(i, j + 1, Side.RIGHT),
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
inline fun <reified T>testSolution(part: Int, testCaseNumber: Int, solution: (input: List<String>) -> T, testCase: TestCase<T>): Boolean {
    val solutionTestResult = solution(testCase.input)

    if (solutionTestResult == testCase.getTypedExpected<T>()) {
        return true
    }

    val redColor = "\u001b[31m"
    val reset = "\u001b[0m"

    println("${redColor}Part $part, testcase $testCaseNumber:$reset returned value is $solutionTestResult instead of ${testCase.expectedString}")

    return false
}

const val UNIT_TESTS_DIVIDER = "==="

class TestCase<T>(var input: List<String>, val expectedString: String) {
    inline fun <reified T> getTypedExpected(): T {
        return when(T::class) {
            Long::class -> expectedString.toLong()
            Int::class -> expectedString.toInt()
            String::class -> expectedString
            else -> error("Converter unavailable for ${T::class}")
        } as T
    }
}
class TestCases<P1, P2> {
    val part1 = mutableListOf<TestCase<P1>>()
    val part2 = mutableListOf<TestCase<P2>>()

    fun addToPart1(testCase: TestCase<P1>) {
        part1.add(testCase)
    }

    fun addToPart2(testCase: TestCase<P2>) {
        part2.add(testCase)
    }
}
/**
 * Function will split testcases from a raw input
 */
fun <P1, P2>getTestCases(testInput: List<String>): TestCases<P1, P2> {
    val isVer2 = testInput[0] == "v2"

    val testCases = TestCases<P1, P2>()

    if (!isVer2) {
        var testCaseRaw: Triple<MutableList<String>, String, String> = Triple(mutableListOf(), "", "")
        var parseType = 1 // 1 - expected part 1, 2 - expected part 2, 3 - test case input strings

        for (line in testInput) {
            if (parseType == 1) {
                testCaseRaw = testCaseRaw.copy(second = line)
                parseType = 2
                continue
            }

            if (parseType == 2) {
                testCaseRaw = testCaseRaw.copy(third = line)
                parseType = 3
                continue
            }

            if (line == UNIT_TESTS_DIVIDER) {
                testCases.addToPart1(TestCase(testCaseRaw.first, testCaseRaw.second))
                testCases.addToPart2(TestCase(testCaseRaw.first, testCaseRaw.third))
                testCaseRaw = Triple(mutableListOf(), "", "")
                parseType = 1
                continue
            }

            testCaseRaw.first.add(line)
        }

        testCases.addToPart1(TestCase(testCaseRaw.first, testCaseRaw.second))
        testCases.addToPart2(TestCase(testCaseRaw.first, testCaseRaw.third))
    }

    return testCases
}

/**
 * Run all test cases for test input
 */
inline fun <reified T>testSolutions(part: Int, solution: (input: List<String>) -> T, testCases: List<TestCase<T>>) {
    var passed = 0

    for ((index, testCase) in testCases.withIndex()) {
        if (testSolution(part, index + 1, solution, testCase)) {
            passed += 1
        }
    }

    check(passed == testCases.size) { "Part $part: $passed (out of ${testCases.size}) testcases passed." }
}

/**
 * Run all tests and solutions
 */
inline fun <reified P1, reified P2>runDaySolutions(day: Int, solutionPart1: (input: List<String>) -> P1, solutionPart2: (input: List<String>) -> P2, skipPart2: Boolean = false, skipTests: Boolean = false) {
    val dayStr = day.toString().padStart(2, '0')

    println()
    println("======= AoC: Day $day =======")
    println()

    val testInput = readInput("day$dayStr/tests")
    val input = readInput("day$dayStr/main")

    val testCases = getTestCases<P1, P2>(testInput)

    println("------- Part 1 -------")
    if (!skipTests) {
        testSolutions(1, solutionPart1, testCases.part1)
        println("${testCases.part1.size} testcase(s) passed")
    } else {
        println("${testCases.part1.size} testcase(s) skipped")
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
        testSolutions(2, solutionPart2, testCases.part2)
        println("${testCases.part2.size} testcase(s) passed")
    } else {
        println("${testCases.part2.size} testcase(s) skipped")
    }

    val part2Result = solutionPart2(input)
    println()
    println("=> Result: $part2Result")

    println()
    println("------- All done -------")
}
