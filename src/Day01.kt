import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val list = input.map { it.split("   ").map { it2 -> it2.toInt() } }

        val left = list.map { it[0] }.toTypedArray()
        val right = list.map { it[1] }.toTypedArray()

        left.sort()
        right.sort()

        var sum = 0

        for (i in left.indices) {
            sum += abs(left[i] - right[i])
        }

        return sum
    }

    fun part2(input: List<String>): Int {
        val list = input.map { it.split("   ").map { it2 -> it2.toInt() } }

        val left = list.map { it[0] }.toTypedArray()
        val right = list.map { it[1] }.toTypedArray()

        val freq = right.groupingBy { it }.eachCount()

        var sum = 0

        for (num in left) {
            sum += num * freq.getOrDefault(num, 0)
        }

        return sum
    }

//    // Test if implementation meets criteria from the description, like:
//    check(part1(listOf("test_input")) == 1)
//
    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 11)
    check(part2(testInput) == 31)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
