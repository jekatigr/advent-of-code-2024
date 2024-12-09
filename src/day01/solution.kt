package day01

import runDaySolutions
import kotlin.math.abs

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun part1(input: List<String>): Long {
        val list = input.map { it.split("   ").map { it2 -> it2.toInt() } }

        val left = list.map { it[0] }.toTypedArray()
        val right = list.map { it[1] }.toTypedArray()

        left.sort()
        right.sort()

        var sum = 0L

        for (i in left.indices) {
            sum += abs(left[i] - right[i])
        }

        return sum
    }

    fun part2(input: List<String>): Long {
        val list = input.map { it.split("   ").map { it2 -> it2.toInt() } }

        val left = list.map { it[0] }.toTypedArray()
        val right = list.map { it[1] }.toTypedArray()

        val freq = right.groupingBy { it }.eachCount()

        var sum = 0L

        for (num in left) {
            sum += num * freq.getOrDefault(num, 0)
        }

        return sum
    }

    runDaySolutions(day, ::part1, ::part2)
}
