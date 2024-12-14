package day11

import getIdByXY
import runDaySolutions

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun getStones(input: List<String>): LongArray {
        return input[0].split(" ").map { it.toLong() }.toLongArray()
    }

    fun blink(stones: LongArray): LongArray {
        val result = mutableListOf<Long>();

        for (stone in stones) {
            if (stone == 0L) {
                result.add(1)
                continue
            }

            val stoneStr = stone.toString();
            if (stoneStr.length % 2 == 0) {
                val half = stoneStr.length / 2;
                val part1 = stoneStr.substring(0, half);
                val part2 = stoneStr.substring(half);

                result.add(part1.toLong())
                result.add(part2.toLong())

                continue
            }

            result.add(stone * 2024)
        }

        return result.toLongArray()
    }

    fun part1(input: List<String>): Long {
        var stones = getStones(input)

        for (i in 0..<25) {
            stones = blink(stones);
        }

        return stones.size.toLong()
    }

    val memo = mutableMapOf<String, Long>()

    fun recursiveBlink(stone: Long, blinksLeft: Int): Long {
        val newBlinksLeft = blinksLeft - 1
        val id = getIdByXY(stone.toInt(), blinksLeft)

        if (id in memo) {
            return memo[id]!!
        }

        if (blinksLeft == 0) {
            return 1
        }

        if (stone == 0L) {
            memo[id] = recursiveBlink(1, newBlinksLeft);

            return memo[id]!!
        }

        val stoneStr = stone.toString();
        if (stoneStr.length % 2 == 0) {
            val half = stoneStr.length / 2;
            val part1 = stoneStr.substring(0, half);
            val part2 = stoneStr.substring(half);

            memo[id] = recursiveBlink(part1.toLong(), newBlinksLeft) + recursiveBlink(part2.toLong(), newBlinksLeft)

            return memo[id]!!
        }

        memo[id] = recursiveBlink(stone * 2024, newBlinksLeft)

        return memo[id]!!
    }

    fun part2(input: List<String>): Long {
        var stones = getStones(input)

        var sum = 0L

        for (stone in stones) {
            sum += recursiveBlink(stone, 75)
        }

        return sum
    }

    runDaySolutions(day, ::part1, ::part2)
}
