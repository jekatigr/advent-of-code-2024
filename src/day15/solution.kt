package day15

import Side
import checkIfInMatrixArea
import getSideFromChar
import printMatrix
import runDaySolutions

data class Robot(var x: Int, var y: Int) {
    fun move(nextX: Int, nextY: Int) {
        x = nextX
        y = nextY
    }
}

class Warehouse(private val robot: Robot, private val area: Array<Array<Char>>) {
    fun calculateBoxesGPS(): Long {
        var sum = 0L

        for (i in area.indices) {
            for (j in area[0].indices) {
                if (area[i][j] != 'O') {
                    continue
                }

                sum += 100 * i + j
            }
        }

        return sum
    }

    private fun getCoordsBySide(side: Side, x: Int, y: Int): Pair<Int, Int> {
        return when (side) {
            Side.UP -> Pair(x, y - 1)
            Side.DOWN -> Pair(x, y + 1)
            Side.RIGHT -> Pair(x + 1, y)
            Side.LEFT -> Pair(x - 1, y)
        }
    }

    /**
     * Move box if possible.
     */
    private fun moveBox(side: Side, boxX: Int, boxY: Int): Boolean {
        val (nextX, nextY) = getCoordsBySide(side, boxX, boxY)

        if (!checkIfInMatrixArea(area, nextY, nextX) || area[nextY][nextX] == '#') {
            return false
        }

        if (area[nextY][nextX] == '.' || moveBox(side, nextX, nextY)) {
            area[nextY][nextX] = 'O'
            area[boxY][boxX] = '.'

            return true
        }

        return false
    }

    fun makeRobotMove(move: Side) {
        val (nextX, nextY) = getCoordsBySide(move, robot.x, robot.y)

        if (!checkIfInMatrixArea(area, nextY, nextX)) {
            return
        }

        if (area[nextY][nextX] == '.') {
            robot.move(nextX, nextY)

            return
        }

        if (area[nextY][nextX] == '#') {
            return
        }

        if (moveBox(move, nextX, nextY)) {
            robot.move(nextX, nextY)
        }
    }

    fun paint() {
        area[robot.y][robot.x] = '@'
        printMatrix(area)
        area[robot.y][robot.x] = '.'
    }
}

class WideWarehouse(private val robot: Robot, private val area: Array<Array<Char>>) {
    fun calculateBoxesGPS(): Long {
        var sum = 0L

        for (i in area.indices) {
            for (j in area[0].indices) {
                if (area[i][j] != '[') {
                    continue
                }

                sum += 100 * i + j
            }
        }

        return sum
    }

    private fun getCoordsBySide(side: Side, x: Int, y: Int): Pair<Int, Int> {
        return when (side) {
            Side.UP -> Pair(x, y - 1)
            Side.DOWN -> Pair(x, y + 1)
            Side.RIGHT -> Pair(x + 1, y)
            Side.LEFT -> Pair(x - 1, y)
        }
    }

    private fun getNextBoxCoordsBySide(side: Side, boxParts: Pair<Pair<Int, Int>, Pair<Int, Int>>): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val (p1, p2) = boxParts

        return when (side) {
            Side.UP -> Pair(Pair(p1.first, p1.second - 1), Pair(p2.first, p2.second - 1))
            Side.DOWN -> Pair(Pair(p1.first, p1.second + 1), Pair(p2.first, p2.second + 1))
            Side.RIGHT -> Pair(Pair(p1.first + 1, p1.second), Pair(p2.first + 1, p2.second))
            Side.LEFT -> Pair(Pair(p1.first - 1, p1.second), Pair(p2.first - 1, p2.second))
        }
    }

    private fun getBoxFromPart(boxX: Int, boxY: Int): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        if (area[boxY][boxX] == '[') {
            return Pair(Pair(boxX, boxY), Pair(boxX + 1, boxY))
        } else { // ]
            return Pair(Pair(boxX - 1, boxY), Pair(boxX, boxY))
        }
    }

    /**
     * Move box if possible.
     */
    private fun moveBox(side: Side, boxX: Int, boxY: Int, actuallyMove: Boolean = false): Boolean {
        val boxParts: Pair<Pair<Int, Int>, Pair<Int, Int>> = getBoxFromPart(boxX, boxY)


        val (part1, part2) = getNextBoxCoordsBySide(side, boxParts)

        if (!checkIfInMatrixArea(area, part1.second, part1.first) || !checkIfInMatrixArea(area, part2.second, part2.first)) {
            return false
        }

        if (area[part1.second][part1.first] == '#' || area[part2.second][part2.first] == '#') {
            return false
        }

        val canMovePart1 = area[part1.second][part1.first] == '.' || (part1.first == boxParts.second.first && part1.second == boxParts.second.second) || moveBox(side, part1.first, part1.second)
        val canMovePart2 = area[part2.second][part2.first] == '.' || (part2.first == boxParts.first.first && part2.second == boxParts.first.second) || moveBox(side, part2.first, part2.second)

        if (!canMovePart1 || !canMovePart2) {
            return false
        }

        if (!actuallyMove) {
            return true
        }

        if (area[part1.second][part1.first] != '.' && !(part1.first == boxParts.second.first && part1.second == boxParts.second.second)) {
            moveBox(side, part1.first, part1.second, true)
        }

        if (area[part2.second][part2.first] != '.' && !(part2.first == boxParts.first.first && part2.second == boxParts.first.second)) {
            moveBox(side, part2.first, part2.second, true)
        }

        area[boxParts.second.second][boxParts.second.first] = '.'
        area[boxParts.first.second][boxParts.first.first] = '.'

        area[part1.second][part1.first] = '['
        area[part2.second][part2.first] = ']'

        return true
    }

    fun makeRobotMove(move: Side) {
        val (nextX, nextY) = getCoordsBySide(move, robot.x, robot.y)

        if (!checkIfInMatrixArea(area, nextY, nextX)) {
            return
        }

        if (area[nextY][nextX] == '.') {
            robot.move(nextX, nextY)

            return
        }

        if (area[nextY][nextX] == '#') {
            return
        }

        if (moveBox(move, nextX, nextY)) {
            moveBox(move, nextX, nextY, true)
            robot.move(nextX, nextY)
        }
    }

    fun paint() {
        area[robot.y][robot.x] = '@'
        printMatrix(area)
        area[robot.y][robot.x] = '.'
    }
}

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun parseInput(input: List<String>): Pair<Warehouse, List<Side>> {
        var isMoves = false

        val moves = mutableListOf<Side>()
        var robot: Robot? = null
        val matrix = mutableListOf<Array<Char>>()

        for (lineIndex in input.indices) {
            val line = input[lineIndex]

            if (line.isEmpty()) {
                isMoves = true

                continue
            }

            if (isMoves) {
                for (c in line) {
                    moves += getSideFromChar(c)
                }

                continue
            }

            if ('@' in line) {
                val index = line.indexOf('@');
                robot = Robot(index, lineIndex)
            }

            matrix += line.toCharArray().toTypedArray()
        }

        matrix[robot!!.x][robot.y] = '.'

        return Pair(Warehouse(robot, matrix.toTypedArray()), moves)
    }

    fun part1(input: List<String>): Long {
        val (warehouse, moves) = parseInput(input)

        for (move in moves) {
            warehouse.makeRobotMove(move)
        }

        return warehouse.calculateBoxesGPS()
    }

    fun parseInput2(input: List<String>): Pair<WideWarehouse, List<Side>> {
        var isMoves = false

        val moves = mutableListOf<Side>()
        var robot: Robot? = null
        val matrix = mutableListOf<Array<Char>>()

        for (lineIndex in input.indices) {
            val line = input[lineIndex]

            if (line.isEmpty()) {
                isMoves = true

                continue
            }

            if (isMoves) {
                for (c in line) {
                    moves += getSideFromChar(c)
                }

                continue
            }

            if ('@' in line) {
                val index = line.indexOf('@');

            }

            val matrixLine = mutableListOf<Char>()

            for (c in line) {
                if (c == '@') {
                    robot = Robot(matrixLine.size, lineIndex);

                    matrixLine += '.'
                    matrixLine += '.'

                    continue
                }

                if (c == 'O') {
                    matrixLine += '['
                    matrixLine += ']'

                    continue
                }

                matrixLine += c
                matrixLine += c
            }

            matrix += matrixLine.toTypedArray()
        }

        return Pair(WideWarehouse(robot!!, matrix.toTypedArray()), moves)
    }

    fun part2(input: List<String>): Long {
        val (warehouse, moves) = parseInput2(input)

        for (move in moves) {
            //println("move to ${move.name}")
            warehouse.makeRobotMove(move)
            //warehouse.paint()
        }

        return warehouse.calculateBoxesGPS()
    }

    runDaySolutions(day, ::part1, ::part2)
}
