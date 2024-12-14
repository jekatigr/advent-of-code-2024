package day13

import runDaySolutions

class Prize(s: String) {
    val x: Double
    val y: Double
    val xpt2: Double
    val ypt2: Double

    init {
        val prizeStr = s.split(": ")
        val coordsStr = prizeStr[1].split(", ")
        this.x = coordsStr[0].split("=")[1].toDouble()
        this.y = coordsStr[1].split("=")[1].toDouble()
        this.xpt2 = this.x + 10000000000000
        this.ypt2 = this.y + 10000000000000
    }
}

class Button(s: String) {
    val xIncrement: Double;
    val yIncrement: Double;

    init {
        val buttonStr = s.split(": ")
        val coordsStr = buttonStr[1].split(", ")
        this.xIncrement = coordsStr[0].split("+")[1].toDouble()
        this.yIncrement = coordsStr[1].split("+")[1].toDouble()
    }
}

class Machine(a: String, b: String, p: String) {
    private val a: Button = Button(a)
    private val b: Button = Button(b)
    private val prize: Prize = Prize(p)

    private fun getPrice(prizeX: Double, prizeY: Double): Pair<Boolean, Long> {
        val B = (a.yIncrement * prizeX - prizeY * a.xIncrement) / (a.yIncrement * b.xIncrement - a.xIncrement * b.yIncrement)

        val A = (prizeX - B * b.xIncrement) / a.xIncrement

        val possible = !((A - A.toLong() > 0) || (B - B.toLong() > 0))

        return Pair(possible, A.toLong() * 3 + B.toLong())
    }

    fun getPrizePrice(): Pair<Boolean, Long> { // possible or not and prise
        return this.getPrice(prize.x, prize.y)
    }

    fun getPrizePriceForCorrectCoordinates(): Pair<Boolean, Long> {
        return this.getPrice(prize.xpt2, prize.ypt2)
    }
}

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun getMachines(input: List<String>): List<Machine> {
        val result = mutableListOf<Machine>()
        for (i in 0..input.size) {
            if (i < input.size && input[i].isNotBlank()) {
                continue
            }

            result += Machine(input[i - 3], input[i - 2], input[i - 1])
        }

        return result
    }

    fun part1(input: List<String>): Long {
        var result = 0L

        val machines = getMachines(input)

        for (m in machines) {
            val (possible, cost) = m.getPrizePrice()

            if (possible) {
                result += cost
            }
        }

        return result
    }

    fun part2(input: List<String>): Long {
        var result = 0L

        val machines = getMachines(input)

        for (m in machines) {
            val (possible, cost) = m.getPrizePriceForCorrectCoordinates()

            if (possible) {
                result += cost
            }
        }

        return result
    }

    runDaySolutions(day, ::part1, ::part2)
}
