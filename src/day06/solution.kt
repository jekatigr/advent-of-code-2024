package day06

import runDaySolutions

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun getNextDirection(direction: Direction): Direction {
        return when (direction) {
            Direction.UP -> Direction.RIGHT
            Direction.DOWN -> Direction.LEFT
            Direction.LEFT -> Direction.UP
            Direction.RIGHT -> Direction.DOWN
        }
    }

    fun getNextCellCoords(currentPosition: Pair<Int, Int>, direction: Direction): Pair<Int, Int> {
        val (row, col) = currentPosition

        return when (direction) {
            Direction.UP -> Pair(row - 1, col)
            Direction.DOWN -> Pair(row + 1, col)
            Direction.LEFT -> Pair(row, col - 1)
            Direction.RIGHT -> Pair(row, col + 1)
        }
    }

    fun getDirectionFromChar(char: Char): Direction? {
        when (char) {
            '^' -> return Direction.UP
            '>' -> return Direction.RIGHT
            '<' -> return Direction.LEFT
            'v' -> return Direction.DOWN
        }

        return null
    }

    // returns matrix with area and starting position
    fun createMatrixArea(input: List<String>): Triple<Array<Array<Char>>, Pair<Int, Int>, Direction> {
        val rows = input.size
        val cols = input[0].length

        val matrix: Array<Array<Char>> = Array(rows) { Array(cols) {'.'} }
        var start: Pair<Int, Int>? = null
        var direction: Direction? = null

        for ((i, line) in input.withIndex()) {
            for ((j, char) in line.toCharArray().withIndex()) {
                val dir = getDirectionFromChar(char)

                if (dir != null) {
                    direction = dir
                    start = Pair(i, j)
                    matrix[i][j] = '.'

                    continue
                }

                matrix[i][j] = char
            }
        }

        return Triple(matrix, start!!, direction!!)
    }

    fun inArea(matrix: Array<Array<Char>>, currentPosition: Pair<Int, Int>): Boolean {
        val (i, j) = currentPosition
        val rows = matrix.size
        val cols = matrix[0].size

        return !(i < 0 || i >= rows || j < 0 || j >= cols)
    }

    fun isObstacle(matrix: Array<Array<Char>>, currentPosition: Pair<Int, Int>): Boolean {
        return matrix[currentPosition.first][currentPosition.second] == '#'
    }

    fun part1(input: List<String>): Long {
        var result = 1L

        val (matrix, start, direction) = createMatrixArea(input)

        var currentPosition = start
        var currentDirection = direction
        matrix[currentPosition.first][currentPosition.second] = 'X'

        while (true) {
            val nextPosition = getNextCellCoords(currentPosition, currentDirection)

            if (!inArea(matrix, nextPosition)) {
                return result
            }

            if (isObstacle(matrix, nextPosition)) {
                currentDirection = getNextDirection(currentDirection)

                continue
            }

            if (matrix[nextPosition.first][nextPosition.second] != 'X') {
                result += 1
            }

            currentPosition = nextPosition
            matrix[currentPosition.first][currentPosition.second] = 'X'
        }
    }

    fun copyMatrix(matrix: Array<Array<Char>>): Array<Array<Char>> {
        return matrix.map { arrayOfCells -> arrayOfCells.map { it }.toTypedArray() }.toTypedArray()
    }

    fun getId(position: Pair<Int, Int>, direction: Direction): String {
        val (i, j) = position

        return "$i-$j-$direction"
    }

    fun checkCycle(matrix: Array<Array<Char>>, start: Pair<Int, Int>, direction: Direction): Boolean {
        var currentPosition = start
        var currentDirection = direction
        matrix[currentPosition.first][currentPosition.second] = 'X'

        val visited = mutableSetOf<String>()

        while (true) {
            val nextPosition = getNextCellCoords(currentPosition, currentDirection)

            if (!inArea(matrix, nextPosition)) {
                return false
            }

            if (isObstacle(matrix, nextPosition)) {
                currentDirection = getNextDirection(currentDirection)

                continue
            }

            if (visited.contains(getId(nextPosition, currentDirection))) {
                return true
            }

            currentPosition = nextPosition
            matrix[currentPosition.first][currentPosition.second] = 'X'
            visited.add(getId(currentPosition, currentDirection))
        }
    }

    fun part2(input: List<String>): Long {
        var result = 0L

        val (matrix, start, direction) = createMatrixArea(input)

        for (i in input.indices) {
            for (j in input[i].indices) {
                val newObstaclePosition = Pair(i, j)

                if (isObstacle(matrix, newObstaclePosition)) {
                    continue
                }

                if (newObstaclePosition == start) {
                    continue
                }

                val matrixCopy = copyMatrix(matrix)

                matrixCopy[newObstaclePosition.first][newObstaclePosition.second] = '#'

                if (checkCycle(matrixCopy, start, direction)) {
                    result += 1
                }
            }
        }

        return result
    }

    runDaySolutions(day, ::part1, ::part2)
}
