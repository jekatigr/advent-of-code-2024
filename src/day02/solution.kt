package day02

import runDaySolutions
import kotlin.math.abs

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

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

    fun part1(input: List<String>): Long {
        val list = input.map { it.split(" ").map { it2 -> it2.toInt() } }

        var safe = 0L

        for (report in list) {
            if (checkReport1(report)) {
                safe += 1
            }
        }

        return safe
    }

    fun part2(input: List<String>): Long {
        val list = input.map { it.split(" ").map { it2 -> it2.toInt() } }

        var safe = 0L

        for (report in list) {
            if (checkReport2(report)) {
                safe += 1
            }
        }

        return safe
    }

    runDaySolutions(day, ::part1, ::part2)
}
