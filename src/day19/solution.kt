package day19

import runDaySolutions

data class TrieNode(val children: MutableMap<Char, TrieNode>, var isPattern: Boolean)

class Trie(patterns: List<String>) {
    private val root: TrieNode

    init {
        root = buildTrie(patterns)
    }

    private fun buildTrie(patterns: List<String>): TrieNode {
        val root = TrieNode(mutableMapOf(), false)

        for (pattern in patterns) {
            var currentNode = root

            for ((index, char) in pattern.withIndex()) {
                val isLast = index == pattern.length - 1

                if (char !in currentNode.children) {
                    currentNode.children[char] = TrieNode(mutableMapOf(), false)
                }

                if (isLast) {
                    currentNode.children[char]!!.isPattern = true
                }

                currentNode = currentNode.children[char]!!
            }
        }

        return root
    }

    fun checkDesign(design: String): Long {
        return checkPartOfDesign(design, 0, mutableMapOf())
    }

    private fun checkPartOfDesign(design: String, startIndex: Int, memo: MutableMap<Int, Long>): Long {
        if (startIndex in memo) {
            return memo[startIndex]!!
        }

        var count = 0L

        var currentNode = root

        for (i in startIndex..<design.length) {
            val char = design[i]
            val isLast = i == design.length - 1

            if (char !in currentNode.children) {
                break
            }

            val nextNode = currentNode.children[char]!!

            if (nextNode.isPattern) {
                if (isLast) {
                    count += 1
                } else {
                    count += checkPartOfDesign(design, i + 1, memo)
                }
            }

            currentNode = nextNode
        }

        memo[startIndex] = count

        return count
    }
}

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun parseInput(input: List<String>): Pair<Trie, List<String>> {
        val patterns = input[0].split(", ")
        val designs = mutableListOf<String>()

        for (i in 2..<input.size) {
            designs += input[i]
        }

        return Pair(Trie(patterns), designs)
    }

    fun part1(input: List<String>): Long {
        val (trie, designs) = parseInput(input)

        var result = 0L

        for (design in designs) {
            if (trie.checkDesign(design) > 0) {
                result += 1
            }
        }

        return result
    }

    fun part2(input: List<String>): Long {
        val (trie, designs) = parseInput(input)

        var result = 0L

        for (design in designs) {
            result += trie.checkDesign(design)
        }

        return result
    }

    runDaySolutions(day, ::part1, ::part2)
}
