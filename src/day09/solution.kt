package day09

import runDaySolutions

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun defrag(places: MutableList<String>): Int {
        var left = 0
        var right = places.size - 1

        while (right >= 0 && places[right] == ".") {
            right -= 1
        }

        while (left <= right) {
            while (left < places.size && places[left] != ".") {
                left += 1
            }

            if (left == places.size) {
                return places.size
            }

            places[left] = places[right]
            right -= 1

            while (right >= left && places[right] == ".") {
                right -= 1
            }
        }

        return right + 2
    }

    fun findWordLen(places: MutableList<String>, lastIndex: Int): Int {
        for (i in lastIndex - 1 downTo 0) {
            if (places[i] != places[i + 1]) {
                return lastIndex - i
            }
        }

        return lastIndex + 1
    }

    /**
     * returns left index of free space, -1 otherwise
     */
    fun findFreeSpace(places: MutableList<String>, requiredSpace: Int, maxIndex: Int): Int {
        var free = 0

        for (i in 0 until maxIndex) {
            if (places[i] == ".") {
                free += 1
            } else {
                free = 0
            }

            if (free == requiredSpace) {
                return i - free + 1
            }
        }

        return -1
    }

    fun moveWord(places: MutableList<String>, freeSpaceIndex: Int, wordIndex: Int, length: Int) {
        for (i in 0 until length) {
            places[freeSpaceIndex + i] = places[wordIndex + i]
            places[wordIndex + i] = "."
        }
    }

    fun defrag2(places: MutableList<String>): Int {
        var left = 0
        var right = places.size - 1

        while (right >= 0 && places[right] == ".") {
            right -= 1
        }

        /**
         * 1. right stays on last char of word
         * 2. find word length
         * 3. find free space
         * 4. move word
         */

        while (right >= 0) {
            val wordLen = findWordLen(places, right)
            val freeSpaceIndex = findFreeSpace(places, wordLen, right)

            if (freeSpaceIndex == -1) {
                right -= wordLen
            } else {
                moveWord(places, freeSpaceIndex, right - wordLen + 1, wordLen)
            }

            while (right >= 0 && places[right] == ".") {
                right -= 1
            }
        }

        return right + 2
    }

    fun createPlaces(input: String): MutableList<String> {
        val places = mutableListOf<String>()

        var isFile = true // free space otherwise
        var id = 0

        for (char in input) {
            val len = char.toString().toInt()

            if (isFile) {
                for (i in 0..<len) {
                    places.add(id.toString())
                }

                id += 1
                isFile = false
            } else {
                for (i in 0..<len) {
                    places.add(".")
                }

                isFile = true
            }
        }

        return places
    }

    fun part1(input: List<String>): Long {
        var result = 0L

        val places = createPlaces(input[0])

        val len = defrag(places)

        for (i in 0..<len) {
            result += places[i].toLong() * i
        }

        return result
    }

    fun part2(input: List<String>): Long {
        var result = 0L

        val places = createPlaces(input[0])

        defrag2(places)

        for (i in places.indices) {
            if (places[i] == ".") {
                continue
            }

            result += places[i].toLong() * i
        }

        return result
    }

    runDaySolutions(day, ::part1, ::part2)
}
