package day14

import checkIfInMatrixArea
import getDirections
import printMatrix
import runDaySolutions

const val enableDebugPrint = true;

fun println(string: String) {
    if (!enableDebugPrint) {
        return
    }

    kotlin.io.println(string)
}

class Robot(r: String) {
    private var x: Int
    private var y: Int
    private val vector: Pair<Int, Int>

    init {
        val robotStr = r.split(" ") //p=0,4 v=3,-3
        val positionStr = robotStr[0].split("=")[1].split(",")
        val vectorStr = robotStr[1].split("=")[1].split(",")

        x = positionStr[0].toInt()
        y = positionStr[1].toInt()
        vector = Pair(vectorStr[0].toInt(), vectorStr[1].toInt())
    }

    fun makeStep(area: Pair<Int, Int>) {
        x = (x + vector.first + area.first) % area.first
        y = (y + vector.second + area.second) % area.second
    }

    fun inQuadrant(quadrant: Pair<IntRange, IntRange>): Boolean {
        return (x in quadrant.first && y in quadrant.second)
    }

    fun getPosition() = Pair(y, x)
}

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun parseInput(input: List<String>): Pair<Pair<Int, Int>, List<Robot>> {
        val area = Pair(input[0].toInt(), input[1].toInt());
        val robots = mutableListOf<Robot>()

        for (i in 2 until input.size) {
            robots += Robot(input[i])
        }

        return Pair(area, robots)
    }

    fun calculateQuadrants(fieldArea: Pair<Int, Int>, robots: List<Robot>): Long {
        val n = fieldArea.first
        val m = fieldArea.second

        val quadrants = listOf(
            Pair(0..<n/2, 0..<m/2),
            Pair(n/2+1..<n, 0..<m/2),
            Pair(0..<n/2, m/2+1..<m),
            Pair(n/2+1..<n, m/2+1..<m),
        )

        val map = mutableMapOf(
            0 to 0L,
            1 to 0L,
            2 to 0L,
            3 to 0L,
        ) // quadrant, count

        for (robot in robots) {
            for (i in quadrants.indices) {
                if (robot.inQuadrant(quadrants[i])) {
                    map[i] = map[i]!! + 1
                    continue
                }
            }
        }

        return map[0]!! * map[1]!! * map[2]!! * map[3]!!
    }

    fun createMatrixOfRobots(robots: List<Robot>, area: Pair<Int, Int>): Array<Array<Char>> {
        val rows = area.second
        val cols = area.first

        val matrix: Array<Array<Char>> = Array(rows) { Array(cols) {'.'} }

        for (robot in robots) {
            val (x, y) = robot.getPosition()

            if (matrix[x][y] == '.') {
                matrix[x][y] = '1'
            } else {
                matrix[x][y] = (matrix[x][y].toString().toInt() + 1).toString()[0]
            }
        }

        return matrix
    }

    fun part1(input: List<String>): Long {
        val (fieldArea, robots) = parseInput(input);

        for (i in 0..<100) {
            for (robot in robots) {
                robot.makeStep(fieldArea)
            }
        }

        return calculateQuadrants(fieldArea, robots)
    }

    fun checkHeuristics(matrix: Array<Array<Char>>, robotsCount: Int): Boolean {
        var neighbors = 0
        var threshold = 0.5

        for (i in matrix.indices) {
            for (j in matrix[0].indices) {
                if (matrix[i][j] == '.') {
                    continue
                }

                val directions = getDirections(i, j)

                for ((x, y) in directions) {
                    if (!checkIfInMatrixArea(matrix, x, y)) {
                        continue
                    }

                    if (matrix[x][y] != '.') {
                        neighbors += 1
                    }
                }
            }
        }

        return (neighbors.toDouble() / robotsCount) > threshold
    }

    fun part2(input: List<String>): Long {
        val (fieldArea, robots) = parseInput(input);

        for (i in 1..<30000) {
            for (robot in robots) {
                robot.makeStep(fieldArea)
            }

            val matrix = createMatrixOfRobots(robots, fieldArea)

            if (checkHeuristics(matrix, robots.size)) {

                println();
                println("current second is $i");
                printMatrix(createMatrixOfRobots(robots, fieldArea))
                println("current second is $i");
            }
        }

        return 0L
    }

    runDaySolutions(day, ::part1, ::part2)
}
