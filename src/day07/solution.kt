package day07

import runDaySolutions

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun getEquationsInput(input: List<String>): List<Pair<Long, List<Long>>> {
        val equations = mutableListOf<Pair<Long, List<Long>>>()

        for (line in input) {
            val array = line.split(": ");

            equations.add(Pair(array[0].toLong(), array[1].split(" ").map { it.toLong() }))
        }

        return equations
    }

    fun checkEquation(res: Long, numbers: List<Long>, withConcat: Boolean = false): Pair<Boolean, String> {
        // queue for operator index, result number and operators string
        // index in [0, numbers.size - 2] // 1 2 3 [size = 3, max index = 1] => 0, 1
        val queue = mutableListOf(Triple(0, numbers[0], ""))

        val maxIndex = numbers.size - 2

        while (queue.isNotEmpty()) {
            val (operatorIndex, currentValue, operatorsString) = queue.removeAt(0)

            // sum

            val sum: Long = currentValue + numbers[operatorIndex + 1]

            if (sum == res && operatorIndex == maxIndex) {
                return Pair(true, "$operatorsString,+".trim(','))
            }

            if (sum <= res && operatorIndex != maxIndex) {
                queue.add(Triple(operatorIndex + 1, sum, "$operatorsString,+"))
            }

            // mult

            val mult = currentValue * numbers[operatorIndex + 1]

            if (mult == res && operatorIndex == maxIndex) {
                return Pair(true, "$operatorsString,*".trim(','))
            }

            if (mult <= res && operatorIndex != maxIndex) {
                queue.add(Triple(operatorIndex + 1, mult, "$operatorsString,*"))
            }

            // concat
            if (!withConcat) {
                continue
            }

            val concat = (currentValue.toString() + numbers[operatorIndex + 1].toString()).toLong()

            if (concat == res && operatorIndex == maxIndex) {
                return Pair(true, "$operatorsString,||".trim(','))
            }

            if (concat <= res && operatorIndex != maxIndex) {
                queue.add(Triple(operatorIndex + 1, concat, "$operatorsString,||"))
            }
        }

        return Pair(false, "")
    }

    fun additionalCheck(expected: Long, numbers: List<Long>, operators: List<String>): Boolean {
        var result = numbers[0]

        for (i in 1..<numbers.size) {
            if (operators[i-1] == "+") {
                result += numbers[i]
                continue
            }
            if (operators[i-1] == "*") {
                result *= numbers[i]
                continue
            }
            if (operators[i-1] == "||") {
                result = (result.toString() + numbers[i].toString()).toLong()
                continue
            }
        }

        return result == expected
    }

    fun part1(input: List<String>): Long {
        var result = 0L

        val equations = getEquationsInput(input)

        for ((res, numbers)  in equations) {
            val (possible, operators) = checkEquation(res, numbers)

            if (possible) {
                check(numbers.size - 1 == operators.split(',').size) { "Part 1: wrong number of operators for $numbers (operator $operators)" }
                check(additionalCheck(res, numbers, operators.split(","))) { "Part 1: failed additional check for $numbers (operator $operators)" }

                result += res
            }
        }

        return result
    }

    fun part2(input: List<String>): Long { // wrong: 106016739583593, 106012871998381
        var result = 0L

        val equations = getEquationsInput(input);

        var i = 0
        for ((res, numbers) in equations) {
            if (equations.size > 100 && (i == 1 || (i > 0 && i % 100 == 0))) {
                println("Evaluating ${i}/${equations.size}")
            }

            i += 1

            val (possible, operators) = checkEquation(res, numbers, true)

            if (possible) {
                check(numbers.size - 1 == operators.split(',').size) { "Part 2: wrong number of operators for $numbers (operator $operators)" }
                check(additionalCheck(res, numbers, operators.split(","))) { "Part 2: failed additional check for $numbers (operator $operators)" }

                result += res
            }
        }

        return result
    }

    runDaySolutions(day, ::part1, ::part2)
}
