package day22

import runDaySolutions

const val MODULO = 16777216

class Sequence (priceA: Int, priceB: Int, priceC: Int, priceD: Int, priceE: Int) {
    val id = "${priceB - priceA} ${priceC - priceB} ${priceD - priceC} ${priceE - priceD}"
    val bananas = priceE
}

class Price(private val initialPrice: Long, private val targetPlaceInTheSequence: Int) {
    private var currentPlaceInTheSequence = 0

    private fun step1(price: Long): Long {
        val mult = price * 64
        val newPrice = price xor mult
        return newPrice % MODULO
    }

    private fun step2(price: Long): Long {
        val div = price / 32
        val newPrice = price xor div
        return newPrice % MODULO
    }

    private fun step3(price: Long): Long {
        val mult = price * 2048
        val newPrice = price xor mult
        return newPrice % MODULO
    }

    private fun calculateNextPrice(price: Long): Long {
        var newPrice = step1(price)
        newPrice = step2(newPrice)
        newPrice = step3(newPrice)

        return newPrice
    }

    fun findFinalPrice(): Long {
        var price = initialPrice

        while (currentPlaceInTheSequence < targetPlaceInTheSequence) {
            price = calculateNextPrice(price)

            currentPlaceInTheSequence += 1
        }

        return price
    }

    fun getSequences(): MutableMap<String, Sequence> {
        val queue = ArrayDeque<Int>(5)
        queue.add((initialPrice % 10).toInt())

        val map = mutableMapOf<String, Sequence>()
        var currentSecretNumber = initialPrice

        while (currentPlaceInTheSequence < targetPlaceInTheSequence) {
            currentSecretNumber = step1(currentSecretNumber)
            currentSecretNumber = step2(currentSecretNumber)
            currentSecretNumber = step3(currentSecretNumber)

            queue.add((currentSecretNumber % 10).toInt())

            if (queue.size == 5) {
                val seq = Sequence(queue[0], queue[1], queue[2], queue[3], queue[4])

                if (seq.id !in map) {
                    map[seq.id] = seq
                }

                queue.removeFirst()
            }

            currentPlaceInTheSequence += 1
        }

        return map
    }
}

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun parseInput(input: List<String>): List<Price> {
        val targetSequenceLen = input[0].toInt()

        return input.stream().skip(1).map { Price(it.toLong(), targetSequenceLen) }.toList()
    }


    fun part1(input: List<String>): Long {
        val initialNumbers = parseInput(input)

        val finalPrices = initialNumbers.map { it.findFinalPrice() }

        return finalPrices.sumOf { it-> it }
    }

    fun part2(input: List<String>): Int {
        val initialNumbers = parseInput(input)

        val bananasMap = mutableMapOf<String, Int>() // seq id, sum of bananas

        for (price in initialNumbers) {
            val sequences = price.getSequences()

            for ((id, sequence) in sequences) {
                if (id !in bananasMap) {
                    bananasMap[id] = 0
                }

                bananasMap[id] = bananasMap[id]!! + sequence.bananas
            }
        }

        return bananasMap.values.max()
    }

    runDaySolutions(day, ::part1, ::part2)
}
