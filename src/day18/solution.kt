package day18

import checkIfInMatrixArea
import getDirections
import runDaySolutions
import java.util.*

data class QueueItem(val score: Int, val y: Int, val x: Int)
data class Byte(val x: Int, val y: Int)

class MemorySpace(val size: Pair<Int, Int>, val bytes: List<Byte>) {
    private fun createMatrix(bytesLimit: Int): Array<Array<Char>> {
        val rows = size.second
        val cols = size.first

        val matrix: Array<Array<Char>> = Array(rows) { Array(cols) {'.'} }

        for (i in 0..<bytesLimit) {
            val (x, y) = bytes[i]

            matrix[y][x] = '#'
        }

        return matrix
    }

    fun findPath(matrix: Array<Array<Char>>, xEnd: Int, yEnd: Int): Long {
        val rows = matrix.size
        val cols = matrix[0].size

        val dp: Array<Array<Int>> = Array(rows) { Array(cols) { Int.MAX_VALUE } }
        dp[0][0] = 0

        val queue = PriorityQueue<QueueItem> { item1, item2 -> item1.score.compareTo(item2.score) }
        queue += QueueItem(0, 0, 0)

        while (queue.isNotEmpty()) {
            val (score, y, x) = queue.poll()

            if (dp[y][x] < score) {
                continue
            }

            val directions = getDirections(y, x)

            for ((i, j) in directions) {
                if (!checkIfInMatrixArea(matrix, i, j) || matrix[i][j] == '#') {
                    continue
                }

                val newScore = score + 1

                if (newScore < dp[i][j]) {
                    dp[i][j] = newScore

                    queue += QueueItem(newScore, i, j)
                }
            }
        }

        return dp[yEnd][xEnd].toLong()
    }

    fun findPathAfterFall(toFall: Int): Long {
        val matrix = createMatrix(toFall)

        return findPath(matrix, matrix[0].size - 1, matrix.size - 1)
    }

    fun findFirstByteWhichMakesExitUnreachable(initialFall: Int): String {
        val matrix = createMatrix(initialFall)

        for (i in initialFall..bytes.lastIndex) {
            val (x, y) = bytes[i]

            matrix[y][x] = '#'

            val result = findPath(matrix, matrix[0].size - 1, matrix.size - 1)

            if (result.toInt() == Int.MAX_VALUE) {
                return "$x,$y"
            }
        }

        throw Error("wrong initial input")
    }
}

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun parseInput(input: List<String>): Pair<MemorySpace, Int> {
        val sizeStr = input[0].removePrefix("Size: ").split(",")
        val fall = input[1].removePrefix("Fall: ").toInt()

        val size = Pair(sizeStr[0].toInt(), sizeStr[1].toInt())

        val bytes = mutableListOf<Byte>()

        for (i in 2..<input.size) {
            val byteStr = input[i].split(",")
            val x = byteStr[0].toInt()
            val y = byteStr[1].toInt()

            bytes += Byte(x, y)
        }

        return Pair(MemorySpace(size, bytes), fall)
    }

    fun part1(input: List<String>): Long {
        val (memorySpace, toFall) = parseInput(input)

        val result = memorySpace.findPathAfterFall(toFall)

        return result
    }

    fun part2(input: List<String>): String {
        val (memorySpace, toFall) = parseInput(input)

        val result = memorySpace.findFirstByteWhichMakesExitUnreachable(toFall)

        return result
    }

    runDaySolutions(day, ::part1, ::part2)
}
