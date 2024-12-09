package day03

import runDaySolutions

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    val regex = Regex("mul\\([0-9]{1,3},[0-9]{1,3}\\)")
    val regex2 = Regex("mul\\([0-9]{1,3},[0-9]{1,3}\\)|do(?:n't)?\\(\\)")

    fun parseMul(mulStr: String): Int {
        val parts = mulStr.split(',');

        val num1 = parts[0].filter { it.isDigit() }.toInt()
        val num2 = parts[1].filter { it.isDigit() }.toInt()

        return num1 * num2;
    }

    fun part1(input: List<String>): Long {
        var result = 0L

        for (line in input) {
            val matches = regex.findAll(line);

            for (match in matches) {
                result += parseMul(match.value)
            }
        }

        return result
    }

    fun part2(input: List<String>): Long {
        var result = 0L

        for (line in input) {
            val matches = regex2.findAll(line);

            var works = true

            for (match in matches) {
                val str = match.value

                if (str.startsWith("mul")) {
                    if (works) {
                        result += parseMul(str)
                    }
                } else {
                    works = str == "do()"
                }
            }
        }

        return result
    }

    runDaySolutions(day, ::part1, ::part2)
}
