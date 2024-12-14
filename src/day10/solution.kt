package day10

import getDirections
import getIdByXY
import runDaySolutions

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun createMatrix(input: List<String>): Pair<Array<Array<Int>>, List<Pair<Int, Int>>> {
        val rows = input.size
        val cols = input[0].length

        val matrix: Array<Array<Int>> = Array(rows) { Array(cols) { 0 } }
        val starts = mutableListOf<Pair<Int, Int>>()

        for ((i, line) in input.withIndex()) {
            for ((j, char) in line.toCharArray().withIndex()) {
                matrix[i][j] = char.toString().toInt()

                if (matrix[i][j] == 0) {
                    starts.add(Pair(i, j))
                }
            }
        }

        return Pair(matrix, starts)
    }

    fun getReachableTails(matrix: Array<Array<Int>>, start: Pair<Int, Int>): Int {
        val visited = mutableSetOf<String>()
        val queue = mutableListOf(start)

        val resultSet = mutableSetOf<String>()

        while (queue.isNotEmpty()) {
            val (x, y) = queue.removeAt(0)
            val currentValue = matrix[x][y]

            if (currentValue == 9) {
                resultSet.add(getIdByXY(x, y))
            }

            val directions = getDirections(x, y)

            for ((i, j) in directions) {
                if (i < 0 || j < 0 || i >= matrix.size || j >= matrix[0].size) {
                    continue
                }

                val id = getIdByXY(i, j)
                if (matrix[i][j] != currentValue + 1) {
                    continue
                }

                if (id in visited) {
                    continue
                }

                visited.add(id)
                queue.add(Pair(i, j))
            }
        }

        return resultSet.size
    }

    fun getPathsToTails(matrix: Array<Array<Int>>, start: Pair<Int, Int>): Int {
        val queue = mutableListOf(start)

        var result = 0

        while (queue.isNotEmpty()) {
            val (x, y) = queue.removeAt(0)
            val currentValue = matrix[x][y]

            if (currentValue == 9) {
                result += 1

                continue
            }

            val directions = getDirections(x, y)

            for ((i, j) in directions) {
                if (i < 0 || j < 0 || i >= matrix.size || j >= matrix[0].size) {
                    continue
                }

                if (matrix[i][j] != currentValue + 1) {
                    continue
                }

                queue.add(Pair(i, j))
            }
        }

        return result
    }

    fun part1(input: List<String>): Long {
        var result = 0L

        val (matrix, starts) = createMatrix(input)

        for (start in starts) {
            result += getReachableTails(matrix, start)
        }

        return result
    }

    fun part2(input: List<String>): Long {
        var result = 0L

        val (matrix, starts) = createMatrix(input)

        for (start in starts) {
            result += getPathsToTails(matrix, start)
        }

        return result
    }

    runDaySolutions(day, ::part1, ::part2)
}
