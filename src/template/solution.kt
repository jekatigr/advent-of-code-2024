package template

import runDaySolutions

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun part1(input: List<String>): Long {
        var result = 0L

        return result
    }

    fun part2(input: List<String>): Long {
        var result = 0L

        return result
    }

    runDaySolutions(day, ::part1, ::part2,  setOf(Skip.PART2_TESTS, Skip.PART2_SOLUTION))
}
