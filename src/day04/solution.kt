package day04

import runDaySolutions

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    val letters = arrayOf('X', 'M', 'A', 'S')

    fun getId(i: Int, j: Int): String {
        return "$i-$j"
    }

    fun getCoordsFromId(id: String): Pair<Int, Int> {
        val arr = id.split("-");
        return Pair(arr[0].toInt(), arr[1].toInt());
    }

    fun check(matrix: Array<Array<Char>>, getCoords: (Int) -> Pair<Int, Int>): Array<String>? {
        val result = Array<String>(4) {""}

        for (i in 0..<letters.size) {
            val (row, col) = getCoords(i)

            if (row < 0 || row >= matrix.size || col < 0 || col >= matrix[0].size) {
                return null
            }

            if (letters[i] != matrix[row][col]) {
                return null
            }

            result[i] = getId(row, col)
        }

        return result
    }

    // returns string ids of letters of a word in array [X id, M id, A id, S id] or null
    fun getWordIds(matrix: Array<Array<Char>>, x: Int, y: Int): Array<String>? {
        val current = matrix[x][y];

        if (current == '.') {
            return null
        }

        val currentIndex = letters.indexOf(current)

        val functions: Array<(Int) -> Pair<Int, Int>> = arrayOf(
            { letterIndex -> Pair(x, y - currentIndex + letterIndex) }, // ltr
            { letterIndex -> Pair(x, y + currentIndex - letterIndex) }, // rtl
            { letterIndex -> Pair(x + currentIndex - letterIndex, y) }, // ttb
            { letterIndex -> Pair(x - currentIndex + letterIndex, y) }, // btt
            { letterIndex -> Pair(x - currentIndex + letterIndex, y - currentIndex + letterIndex) }, // diag ltr btt
            { letterIndex -> Pair(x - currentIndex + letterIndex, y + currentIndex - letterIndex) }, // diag rtl btt
            { letterIndex -> Pair(x + currentIndex - letterIndex, y - currentIndex + letterIndex) }, // diag ltr ttb
            { letterIndex -> Pair(x + currentIndex - letterIndex, y + currentIndex - letterIndex) }, // diag rtl ttb
        )

        val ids = mutableListOf<String>()
        var count = 0

        for (func in functions) {
            var letterIds = check(matrix, func)

            if (letterIds != null) {
                ids.add(letterIds.joinToString(";"))
            }
        }

        return ids.toTypedArray()
    }

    fun createMatrix(input: List<String>): Array<Array<Char>> {
        val rows = input.size
        val cols = input[0].length

        val matrix: Array<Array<Char>> = Array(rows) { Array(cols) {'.'} }

        for ((i, line) in input.withIndex()) {
            for ((j, char) in line.toCharArray().withIndex()) {
                if (char in letters) {
                    matrix[i][j] = char
                }
            }
        }

        return matrix
    }

    fun checkMAS(matrix: Array<Array<Char>>, x: Int, y: Int): Boolean {
        val map = mutableMapOf<Char, Int>()

        val coords = listOf(
            Pair(x - 1, y - 1),
            Pair(x - 1, y + 1),
            Pair(x + 1, y - 1),
            Pair(x + 1, y + 1),
        )

        for ((i, j) in coords) {
            if (i < 0 || j < 0 || i >= matrix.size || j >= matrix[0].size) {
                return false
            }

            map[matrix[i][j]] = (map[matrix[i][j]] ?: 0) + 1
        }

        return map['M'] == 2 && map['S'] == 2 && matrix[coords[0].first][coords[0].second] != matrix[coords[3].first][coords[3].second]
    }

    fun part1(input: List<String>): Long {

        val set = mutableSetOf<String>() // set of words

        val matrix = createMatrix(input)

        for ((i, row) in matrix.withIndex()) {
            for ((j, char) in row.withIndex()) {
                val ids = getWordIds(matrix, i, j) ?: continue

                set.addAll(ids)
            }
        }

        return set.size.toLong()
    }

    fun part2(input: List<String>): Long {
        var result = 0L

        val matrix = createMatrix(input)

        for ((i, row) in matrix.withIndex()) {
            for ((j, char) in row.withIndex()) {
                if (char != 'A' || !checkMAS(matrix, i, j)) {
                    continue
                }

                result += 1
            }
        }

        return result
    }

    runDaySolutions(day, ::part1, ::part2)
}
