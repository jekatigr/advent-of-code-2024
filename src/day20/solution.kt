package day20

import checkIfInMatrixArea
import getDirections
import getIdByXY
import runDaySolutions
import kotlin.math.abs

class RaceTrack(private val area: Array<Array<Char>>, private val start: Pair<Int, Int>, private val end: Pair<Int, Int>) { // y,x everywhere
    private val rows = area.size
    private val cols = area[0].size

    private fun timeMapBfs(start: Pair<Int, Int>): Array<Array<Int>> {
        var queue = mutableListOf<Pair<Int, Int>>() // y, x
        queue += Pair(start.first, start.second)

        val visited = mutableSetOf<String>()
        visited += getIdByXY(end.first, end.second)

        val memo: Array<Array<Int>> = Array(rows) { Array(cols) { -1 } }

        var time = 0

        while (queue.isNotEmpty()) {
            val newQueue = mutableListOf<Pair<Int, Int>>() // y, x

            for ((y, x) in queue) {

                memo[y][x] = time

                val directions = getDirections(y, x)

                for ((i, j) in directions) {
                    if (!checkIfInMatrixArea(area, i, j) || area[i][j] == '#') {
                        continue
                    }

                    val id = getIdByXY(i, j)

                    if (id in visited) {
                        continue
                    }

                    newQueue.add(Pair(i, j))
                    visited.add(id)
                }
            }

            queue = newQueue
            time += 1
        }

        return memo
    }

    private fun checkIfPossibleToCheat(p1: Pair<Int, Int>, p2: Pair<Int, Int>): Boolean {
        return checkIfInMatrixArea(area, p1)
            && checkIfInMatrixArea(area, p2)
            && area[p1.first][p1.second] == '.'
            && area[p2.first][p2.second] == '.'
    }

    fun findCheats2(minimumToSave: Int): Int {
        val timeMap = timeMapBfs(end)

        var count = 0

        for (i in 0..<rows) {
            for (j in 0..<cols) {
                if (area[i][j] != '#') {
                    continue
                }

                val up = Side.UP.next(i, j)
                val down = Side.DOWN.next(i, j)

                if (checkIfPossibleToCheat(up, down)) {
                    val diff = abs(timeMap[up.first][up.second] - timeMap[down.first][down.second]) - 1 // minus one to additional picosecond to step

                    if (diff >= minimumToSave) {
                        count += 1
                    }
                }

                val left = Side.LEFT.next(i, j)
                val right = Side.RIGHT.next(i, j)

                if (checkIfPossibleToCheat(left, right)) {
                    val diff = abs(timeMap[left.first][left.second] - timeMap[right.first][right.second]) - 1 // minus one to additional picosecond to step

                    if (diff >= minimumToSave) {
                        count += 1
                    }
                }
            }
        }

        return count
    }

    fun findCheatsWide(minimumToSave: Int, maximumSteps: Int): Int {
        val timeMap = timeMapBfs(end)

        var count = 0

        val cheats = mutableSetOf<String>()

        for (startI in 0..<rows) {
            for (startJ in 0..<cols) {
                if (area[startI][startJ] == '#') {
                    continue
                }

                var queue = mutableListOf<Pair<Int, Int>>() // y, x
                queue += Pair(startI, startJ)

                val visited = mutableSetOf<String>()
                visited += getIdByXY(startI, startJ)

                var step = 1

                while (queue.isNotEmpty() && step <= maximumSteps) {
                    val newQueue = mutableListOf<Pair<Int, Int>>() // y, x

                    for ((y, x) in queue) {
                        val directions = getDirections(y, x)

                        for ((i, j) in directions) {
                            if (!checkIfInMatrixArea(area, i, j)) {
                                continue
                            }

                            val id = getIdByXY(i, j)

                            if (id in visited) {
                                continue
                            }

                            if (area[i][j] == '.') {
                                val diff = abs(timeMap[startI][startJ] - timeMap[i][j]) - step

                                val idRTL = "${getIdByXY(startI, startJ)}-${getIdByXY(i, j)}"
                                val idLTR = "${getIdByXY(i, j)}-${getIdByXY(startI, startJ)}"

                                if (diff >= minimumToSave && idRTL !in cheats && idLTR !in cheats) {
                                    cheats += idRTL
                                    cheats += idLTR

                                    count += 1
                                }
                            }

                            newQueue.add(Pair(i, j))
                            visited.add(id)
                        }
                    }

                    queue = newQueue
                    step += 1
                }
            }
        }

        return count
    }
}

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun parseInput(input: List<String>): Triple<RaceTrack, Int, Int> {
        val minimumToSave = input[0].toInt()
        val maximumStepsPt2 = input[1].toInt()

        val matrix = mutableListOf<Array<Char>>()
        var start: Pair<Int, Int>? = null
        var end: Pair<Int, Int>? = null

        for (lineIndex in 2..<input.size) {
            val line = input[lineIndex]

            val matrixLine = mutableListOf<Char>()

            for ((index, c) in line.withIndex()) {
                if (c == 'S') {
                    start = Pair(lineIndex - 1, index)
                    matrixLine.add('.')
                    continue
                }

                if (c == 'E') {
                    end = Pair(lineIndex - 1, index)
                    matrixLine.add('.')
                    continue
                }

                matrixLine += c
            }

            matrix += matrixLine.toTypedArray()
        }

        return Triple(RaceTrack(matrix.toTypedArray(), start!!, end!!), minimumToSave, maximumStepsPt2)
    }

    fun part1(input: List<String>): Int {
        val (racetrack, minimumToSave) = parseInput(input)

        val total2 = racetrack.findCheats2(minimumToSave)

        return total2
    }

    fun part2(input: List<String>): Int {
        val (racetrack, minimumToSave, maximumStepsPt2) = parseInput(input)

        return racetrack.findCheatsWide(minimumToSave, maximumStepsPt2)
    }

    runDaySolutions(day, ::part1, ::part2)
}
