package day08

import getIdByXY
import runDaySolutions

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun getNodesMap(input: List<String>): Map<Char, List<Pair<Int, Int>>> {
        val map = mutableMapOf<Char, MutableList<Pair<Int, Int>>>()

        for ((i, line) in input.withIndex()) {
            for ((j, char) in line.withIndex()) {
                if (char == '.') {
                    continue
                }

                if (!map.containsKey(char)) {
                    map[char] = mutableListOf()
                }

                map[char]!!.add(Pair(i, j))
            }
        }

        return map
    }

    fun inArea(rows: Int, cols: Int, anti: Pair<Int, Int>): Boolean {
        return anti.first >= 0 && anti.second >= 0 && anti.first < rows && anti.second < cols
    }

    fun getAntiNode(a: Pair<Int, Int>, b: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(b.first + (b.first - a.first), b.second + (b.second - a.second))
    }

    fun part1(input: List<String>): Long {
        var result = 0L
        val antiSet = mutableSetOf<String>()

        val rows = input.size
        val cols = input[0].length

        val nodesMap = getNodesMap(input)

        //val matrix: Array<Array<Char>> = Array(rows) { Array(cols) {'.'} }

        for ((_, nodes) in nodesMap) {
            for (i in nodes.indices) {
                for (j in nodes.indices) {
                    if (i == j) {
                        continue
                    }

                    val anti = getAntiNode(nodes[i], nodes[j])
                    val id = getIdByXY(anti.first, anti.second)

                    if (inArea(rows, cols, anti) && !antiSet.contains(id)) {
                        result += 1
                        antiSet.add(id)
                    }
                }
            }
        }

//        for (row in matrix) {
//            println(row.joinToString(""))
//        }

        return result
    }

    fun part2(input: List<String>): Long {
        var result = 0L
        val antiSet = mutableSetOf<String>()

        val rows = input.size
        val cols = input[0].length

        val nodesMap = getNodesMap(input)

        //val matrix: Array<Array<Char>> = Array(rows) { Array(cols) {'.'} }

        for ((_, nodes) in nodesMap) {
            for (i in nodes.indices) {
                for (j in nodes.indices) {
                    if (i == j) {
                        continue
                    }

                    if (!antiSet.contains(getIdByXY(nodes[i].first, nodes[i].second))) {
                        result += 1
                        antiSet.add(getIdByXY(nodes[i].first, nodes[i].second))
                    }

                    var first = nodes[i]
                    var second = nodes[j]

                    while (inArea(rows, cols, second)) {
                        val idSecond = getIdByXY(second.first, second.second)

                        if (!antiSet.contains(idSecond)) {
                            result += 1
                            antiSet.add(idSecond)
                        }

                        val temp = getAntiNode(first, second)
                        first = second
                        second = temp
                    }
                }
            }
        }

        return result
    }

    runDaySolutions(day, ::part1, ::part2)
}
