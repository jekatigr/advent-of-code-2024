package day21

import Side
import checkIfInMatrixArea
import getDirections
import getIdByXY
import getIdByXYZ
import runDaySolutions
import kotlin.math.min

val numericKeypad: Array<Array<Char>> = arrayOf(
    arrayOf('7', '8', '9'),
    arrayOf('4', '5', '6'),
    arrayOf('1', '2', '3'),
    arrayOf(' ', '0', 'A'),
)

val buttonKeypad: Array<Array<Char>> = arrayOf(
    arrayOf(' ', '^', 'A'),
    arrayOf('<', 'v', '>'),
)

fun keyToCoords(key: Char, isNumericKeypad: Boolean = true): Pair<Int, Int> {
    if (isNumericKeypad) {
        return when(key) {
            '7' -> Pair(0, 0)
            '8' -> Pair(0, 1)
            '9' -> Pair(0, 2)
            '4' -> Pair(1, 0)
            '5' -> Pair(1, 1)
            '6' -> Pair(1, 2)
            '1' -> Pair(2, 0)
            '2' -> Pair(2, 1)
            '3' -> Pair(2, 2)
            ' ' -> Pair(3, 0)
            '0' -> Pair(3, 1)
            'A' -> Pair(3, 2)
            else -> {
                throw Error("Impossible")
            }
        }
    }

    return when(key) {
        ' ' -> Pair(0, 0)
        '^' -> Pair(0, 1)
        'A' -> Pair(0, 2)
        '<' -> Pair(1, 0)
        'v' -> Pair(1, 1)
        '>' -> Pair(1, 2)
        else -> {
            throw Error("Impossible")
        }
    }
}

class Paths {
    val list = mutableSetOf<String>()
    var minimum = -1

    fun add(currentPath: String) {
        if (minimum != -1 && currentPath.length > minimum) {
            return
        }

        if (currentPath.length < minimum) {
            list.clear()
        }

        list += currentPath
        minimum = currentPath.length
    }
}

class Solution(private val input: List<String>, private val keypadsLayers: Int) {
    private val memo = mutableMapOf<String, Long>()

    private fun getForbiddenSides(from: Pair<Int, Int>, to: Pair<Int, Int>): Set<Side> {
        val yDiff = to.first - from.first
        val xDiff = to.second - from.second

        val result = mutableSetOf<Side>()

        if (yDiff >= 0) {
            result += Side.UP
        }
        if (yDiff <= 0) {
            result += Side.DOWN
        }
        if (xDiff >= 0) {
            result += Side.LEFT
        }
        if (xDiff <= 0) {
            result += Side.RIGHT
        }

        return result
    }

    private fun convertSideToChar(side: Side): Char {
        return when (side) {
            Side.UP -> '^'
            Side.DOWN -> 'v'
            Side.LEFT -> '<'
            Side.RIGHT -> '>'
        }
    }

    private fun findMinimumPathsDfs(
        area: Array<Array<Char>>,
        start: Pair<Int, Int>,
        to: Char,
        visited: MutableSet<String>,
        currentPath: String,
        paths: Paths,
        forbiddenSides: Set<Side>
    ) {
        if (paths.minimum != -1 && currentPath.length > paths.minimum) {
            return
        }

        val (y, x) = start

        if (area[y][x] == to) {
            paths.add(currentPath)
        }

        val directions = getDirections(y, x)

        for ((i, j, side) in directions) {
            if (!checkIfInMatrixArea(area, i, j)) {
                continue
            }

            if (side in forbiddenSides) {
                continue
            }

            if (area[i][j] == ' ') {
                continue
            }

            val id = getIdByXY(i, j)

            if (id in visited) {
                continue
            }

            visited += id
            findMinimumPathsDfs(area, Pair(i, j), to, visited, "${currentPath}${convertSideToChar(side)}", paths, forbiddenSides)
            visited -= id
        }
    }

    private fun getOptimalPathOnKeypad(from: Char, to: Char, keypadsLeft: Int, isFirstLayer: Boolean = false): Long {
        val id = getIdByXYZ(from, to, keypadsLeft)

        if (id in memo) {
            return memo[id]!!
        }

        val paths = Paths()
        val forbiddenSides = getForbiddenSides(keyToCoords(from, isFirstLayer), keyToCoords(to, isFirstLayer))

        val keypad = if (isFirstLayer) numericKeypad else buttonKeypad

        findMinimumPathsDfs(keypad, keyToCoords(from, isFirstLayer), to, mutableSetOf(), "", paths, forbiddenSides)

        if (keypadsLeft == 0) {
            return paths.minimum.toLong() + 1
        }

        var minimumLen = -1L

        for (path in paths.list) {
            val len = getOptimalSequenceLengthForCode(path + "A", keypadsLeft - 1)

            minimumLen = if (minimumLen == -1L) len else min(len, minimumLen)
        }

        memo[id] = minimumLen

        return minimumLen
    }

    private fun getOptimalSequenceLengthForCode(code: String, keypadsLeft: Int): Long {
        var len = 0L

        var prev = 'A'
        for (char in code) {
            len += getOptimalPathOnKeypad(prev, char, keypadsLeft, keypadsLeft == keypadsLayers)

            prev = char
        }

        return len
    }

    fun calculateResult(): Long {
        var result = 0L

        for (code in input) {
            val len = getOptimalSequenceLengthForCode(code, keypadsLayers)

            result += code.removeSuffix("A").toLong() * len
        }

        return result
    }
}

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun part1(input: List<String>): Long {
        val solution = Solution(input, 2)

        return solution.calculateResult()
    }

    fun part2(input: List<String>): Long {
        val solution = Solution(input, 25)

        return solution.calculateResult()
    }

    runDaySolutions(day, ::part1, ::part2)
}
