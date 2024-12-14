package day12

import Side
import checkIfInMatrixArea
import getDirections
import getIdByXY
import runDaySolutions

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun getMatrix(input: List<String>): Array<Array<Char>> {
        val rows = input.size
        val cols = input[0].length

        val matrix: Array<Array<Char>> = Array(rows) { Array(cols) {'.'} }

        for ((i, line) in input.withIndex()) {
            for ((j, char) in line.toCharArray().withIndex()) {
                matrix[i][j] = char
            }
        }

        return matrix

    }

    fun getPriceOfArea(matrix: Array<Array<Char>>, visited: MutableSet<String>, startI: Int, startJ: Int): Long {
        val queue = mutableListOf(Pair(startI, startJ))
        visited += getIdByXY(startI, startJ)

        var area = 0L
        var perimeter = 0L

        while (queue.isNotEmpty()) {
            val (x, y) = queue.removeAt(0)
            area += 1
            perimeter += 4

            val dirs = getDirections(x, y)

            for ((i, j) in dirs) {
                if (!checkIfInMatrixArea(matrix, i, j)) {
                    continue
                }

                if (matrix[i][j] != matrix[startI][startJ]) {
                    continue
                }

                perimeter -= 1 // from current to neighbor

                val id = getIdByXY(i, j)

                if (id in visited) {
                    continue
                }

                visited += id
                queue.add(Pair(i, j))
            }
        }

        return area * perimeter
    }

    fun part1(input: List<String>): Long {
        var result = 0L

        val matrix = getMatrix(input);

        val visited = mutableSetOf<String>()

        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                if (visited.contains(getIdByXY(i, j))) {
                    continue;
                }

                result += getPriceOfArea(matrix, visited, i, j);
            }
        }

        return result
    }

    fun getFenceId(x: Int, y: Int, side: Side) = "$x-$y-$side"

    fun countFencesSides(fences: MutableMap<String, Triple<Int, Int, Side>>): Long {
        var sides = 0L

        val visited = mutableSetOf<String>()

        for (fenceId in fences.keys) {
            if (fenceId in visited) {
                continue
            }

            sides += 1
            visited.add(fenceId)

            val queue = mutableListOf(fences[fenceId])

            while (queue.isNotEmpty()) {
                val (x, y, side) = queue.removeAt(0)!!

                if (side == Side.UP || side == Side.DOWN) {
                    val neighborId1 = getFenceId(x, y - 1, side)

                    if (neighborId1 in fences && neighborId1 !in visited) {
                        visited.add(neighborId1)
                        queue.add(Triple(x, y - 1, side))
                    }

                    val neighborId2 = getFenceId(x, y + 1, side)

                    if (neighborId2 in fences && neighborId2 !in visited) {
                        visited.add(neighborId2)
                        queue.add(Triple(x, y + 1, side))
                    }
                } else {
                    val neighborId1 = getFenceId(x - 1, y, side)

                    if (neighborId1 in fences && neighborId1 !in visited) {
                        visited.add(neighborId1)
                        queue.add(Triple(x - 1, y, side))
                    }

                    val neighborId2 = getFenceId(x + 1, y, side)

                    if (neighborId2 in fences && neighborId2 !in visited) {
                        visited.add(neighborId2)
                        queue.add(Triple(x + 1, y, side))
                    }
                }
            }
        }

        return sides
    }

    fun getPriceOfArea2(matrix: Array<Array<Char>>, visited: MutableSet<String>, startI: Int, startJ: Int): Long {
        val queue = mutableListOf(Pair(startI, startJ))
        visited += getIdByXY(startI, startJ)

        val fences = mutableMapOf<String, Triple<Int, Int, Side>>()

        var area = 0L

        while (queue.isNotEmpty()) {
            val (x, y) = queue.removeAt(0)
            area += 1

            val dirs = getDirections(x, y)

            for ((i, j, side) in dirs) {
                if (!checkIfInMatrixArea(matrix, i, j) || matrix[i][j] != matrix[startI][startJ]) {
                    val fenceId = getFenceId(x, y, side)
                    fences[fenceId] = Triple(x, y, side)

                    continue
                }

                val id = getIdByXY(i, j)

                if (id in visited) {
                    continue
                }

                visited += id
                queue.add(Pair(i, j))
            }
        }

        return area * countFencesSides(fences)
    }

    fun part2(input: List<String>): Long {
        var result = 0L

        val matrix = getMatrix(input);

        val visited = mutableSetOf<String>()

        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                if (visited.contains(getIdByXY(i, j))) {
                    continue;
                }

                result += getPriceOfArea2(matrix, visited, i, j);
            }
        }

        return result
    }

    runDaySolutions(day, ::part1, ::part2)
}
