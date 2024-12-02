import kotlin.math.abs

fun main() {
    fun checkReport1(report: List<Int>): Boolean {
        if (report[1] == report[0]) {
            return false;
        }

        var isIncreasing = false

        if (report[1] > report[0]) {
            isIncreasing = true
        }

        for (i in 1..<report.size) {
            val diff = abs(report[i] - report[i-1])

            if (diff < 1 || diff > 3) {
                return false
            }

            if (isIncreasing && report[i] < report[i - 1]) {
                return false
            }

            if (!isIncreasing && report[i] > report[i - 1]) {
                return false
            }
        }

        return true
    }

    fun checkReport2(report: List<Int>): Boolean {
        for (i in report.indices) {
            val rep = report.toMutableList()
            rep.removeAt(i)
            if (checkReport1(rep)) {
                return true
            }
        }

        return false
    }

    fun part1(input: List<String>): Int {
        val list = input.map { it.split(" ").map { it2 -> it2.toInt() } }

        var safe = 0

        for (report in list) {
            if (checkReport1(report)) {
                safe += 1
            }
        }

        return safe
    }

    fun part2(input: List<String>): Int {
        val list = input.map { it.split(" ").map { it2 -> it2.toInt() } }

        var safe = 0

        for (report in list) {
            if (checkReport2(report)) {
                safe += 1
            }
        }

        return safe
    }

//    // Test if implementation meets criteria from the description, like:
//    check(part1(listOf("test_input")) == 1)
//
    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
