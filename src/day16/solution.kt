package day16

import Side
import checkIfInMatrixArea
import getDirections
import getIdByXY
import runDaySolutions
import java.util.*
import kotlin.collections.ArrayDeque

data class QueueItem(val score: Long, val y: Int, val x: Int, val look: Side)
data class QueueItem2(val score: Long, val id: String)

class Maze(private val area: Array<Array<Char>>, private val start: Pair<Int, Int>, private val end: Pair<Int, Int>) {
    private fun traverseArea(): Pair<Array<Array<Long>>, Array<Array<MutableList<Pair<Int, Int>>>>> {
        val rows = area.size
        val cols = area[0].size

        val dp: Array<Array<Long>> = Array(rows) { Array(cols) { Long.MAX_VALUE } }
        dp[start.first][start.second] = 0

        val pathArea = Array(rows) { Array(cols) { mutableListOf<Pair<Int, Int>>() } }

        val queue = PriorityQueue<QueueItem> { item1, item2 -> item1.score.compareTo(item2.score) }
        queue += QueueItem(0, start.first, start.second, Side.RIGHT)

        while (queue.isNotEmpty()) {
            val (score, y, x, look) = queue.poll()

            if (dp[y][x] < score) {
                continue
            }

            val directions = getDirections(y, x)

            for ((i, j, side) in directions) {
                if (!checkIfInMatrixArea(area, i, j) || area[i][j] == '#') {
                    continue
                }

                val newScore = score + (if (look == side) 1 else 1001)

                if (newScore <= dp[i][j]) {
                    dp[i][j] = newScore
                    pathArea[i][j] += Pair(x, y)

                    queue += QueueItem(newScore, i, j, side)
                }
            }
        }

        return Pair(dp, pathArea)
    }

    fun findScore(): Long {
        val (dp, _) = traverseArea()

        return dp[end.first][end.second]
    }

    private fun getIdByXYAndSide(i: Int, j: Int, side: Side) = "$i-$j-${side.name}"

    fun buildGraph(): MutableMap<String, MutableMap<String, Long>> {
        val graph: MutableMap<String, MutableMap<String, Long>> = mutableMapOf()

        for (y in area.indices) {
            for (x in area[0].indices) {
                if (area[y][x] == '#') {
                    continue
                }

                val directions = getDirections(y, x)

                for ((i, j, side) in directions) {
                    if (!checkIfInMatrixArea(area, i, j) || area[i][j] == '#') {
                        continue
                    }

                    val sourceId = getIdByXYAndSide(y, x, side)
                    val targetId = getIdByXYAndSide(i, j, side)

                    if (sourceId !in graph) {
                        graph[sourceId] = mutableMapOf()
                    }

                    graph[sourceId]!![targetId] = 1
                }

                for (currentSide in Side.entries) {
                    val sourceId = getIdByXYAndSide(y, x, currentSide)

                    for (newSide in Side.entries) {
                        if (newSide == currentSide || newSide == currentSide.opposite()) {
                            continue
                        }

                        val targetId = getIdByXYAndSide(y, x, newSide)

                        if (sourceId !in graph) {
                            graph[sourceId] = mutableMapOf()
                        }

                        graph[sourceId]!![targetId] = 1000
                    }
                }
            }
        }

        return graph
    }

    private fun traverseGraph(): Pair<MutableMap<String, Long>, MutableMap<String, MutableSet<String>>> {
        val graph = buildGraph()

        val startId = getIdByXYAndSide(start.first, start.second, Side.RIGHT)
        val dp = mutableMapOf<String, Long>()

        for (key in graph.keys) {
            dp[key] = Long.MAX_VALUE
        }

        dp[startId] = 0L

        val pathArea = mutableMapOf<String, MutableSet<String>>()

        val queue = PriorityQueue<QueueItem2> { item1, item2 -> item1.score.compareTo(item2.score) }
        queue += QueueItem2(0, startId)

        while (queue.isNotEmpty()) {
            val (score, itemId) = queue.poll()

            if (dp[itemId] != null && dp[itemId]!! < score) {
                continue
            }

            val directions = graph[itemId]!!

            for ((targetNodeId, price) in directions) {
                val newScore = score + price

                if (dp[targetNodeId] != null && newScore > dp[targetNodeId]!!) {
                    continue
                }

                dp[targetNodeId] = newScore
                queue += QueueItem2(newScore, targetNodeId)

                if (pathArea[targetNodeId] == null) {
                    pathArea[targetNodeId] = mutableSetOf()
                }

                if (newScore < dp[targetNodeId]!!) { // clean up in case better path
                    pathArea[targetNodeId] = mutableSetOf()
                }

                pathArea[targetNodeId]!! += itemId
            }
        }

        return Pair(dp, pathArea)
    }

    fun findBestTiles_backup(): Long {
        val (dp, path) = traverseArea()

        val queue = ArrayDeque<Pair<Int, Int>>() // y, x
        queue += Pair(end.first, end.second)

        val visited = mutableSetOf(getIdByXY(end.first, end.second))

        while (queue.isNotEmpty()) {
            val (y, x) = queue.removeFirst()

            for ((j, i) in path[y][x]) {
                val id = getIdByXY(i, j)

                if (id in visited) {
                    continue
                }

                queue.add(Pair(i, j))
                visited.add(id)
            }
        }

        printAreaTiles(visited)

        return visited.size.toLong()
    }

    fun findBestTiles(): Long {
        val (dp, paths) = traverseGraph()

        val queue = ArrayDeque<String>()
        val visited = mutableSetOf<String>()

        var targetScore = Long.MAX_VALUE

        for (side in Side.entries) {
            val id = getIdByXYAndSide(end.first, end.second, side)
            targetScore = targetScore.coerceAtMost(dp[id]!!)
        }

        for (side in Side.entries) {
            val id = getIdByXYAndSide(end.first, end.second, side)

            if (dp[id] != targetScore) {
                continue
            }

            queue += id
            visited += id
        }

        while (queue.isNotEmpty()) {
            val nodeId = queue.removeFirst()

            if (paths[nodeId] == null) {
                continue
            }

            val connectedNodes = paths[nodeId]!!

            for (targetNodeId in connectedNodes) {

                if (targetNodeId in visited) {
                    continue
                }

                queue.add(targetNodeId)
                visited.add(targetNodeId)
            }
        }

        printAreaTiles(visited)

        val visitedStripped = visited.map { "${it.split("-")[0]}-${it.split("-")[1]}" }

        return visitedStripped.toSet().size.toLong()
    }


    fun findBestTiles2(): Long {
        val targetScore = findScore()

        val visited = mutableSetOf(getIdByXY(start.second, start.first))
        val resultTiles = mutableSetOf<String>()

        dfs(visited, 0, targetScore, resultTiles, start.first, start.second, Side.RIGHT)

        return resultTiles.size.toLong()
    }

    fun dfs(visited: MutableSet<String>, score: Long, targetScore: Long, returnTiles: MutableSet<String>, y: Int, x: Int, look: Side) {
        if (score > targetScore) {
            println("useless return, ${visited.size}")
            printAreaTiles(visited)

            return
        }

        if (y == end.first && x == end.second) {
            println("end but not that ${visited.size}")
        }

        if (y == end.first && x == end.second && score == targetScore) {
            println(visited.size)
            returnTiles.addAll(visited)
        }

        val directions = getDirections(y, x)

        for ((i, j, side) in directions) {
            val id = getIdByXY(j, i)

            if (!checkIfInMatrixArea(area, i, j) || area[i][j] == '#' || id in visited) {
                continue
            }

            val newScore = score + (if (look == side) 1 else 1001)

            visited.add(id)
            dfs(visited, newScore, targetScore, returnTiles, i, j, side)
            visited.remove(id)
        }
    }

    private fun printAreaTiles(visitedTiles: Set<String>) {
        for ((index, line) in area.withIndex()) {
            for ((j, c) in line.withIndex()) {
                val id = getIdByXY(index, j)

                if (id in visitedTiles) {
                    print("0 ")
                } else {
                    if (c == '.') {
                        print("  ")
                    } else {
                        print("$c ")
                    }
                }
            }
            println()
        }
    }
}

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun parseInput(input: List<String>): Maze {
        val matrix = mutableListOf<Array<Char>>()
        var start: Pair<Int, Int>? = null
        var end: Pair<Int, Int>? = null

        for (lineIndex in input.indices) {
            val line = input[lineIndex]

            val matrixLine = mutableListOf<Char>()

            for ((index, c) in line.withIndex()) {
                if (c == 'S') {
                    start = Pair(lineIndex, index)
                }

                if (c == 'E') {
                    end = Pair(lineIndex, index)
                }

                matrixLine += c
            }

            matrix += matrixLine.toTypedArray()
        }

        return Maze(matrix.toTypedArray(), start!!, end!!)
    }

    fun part1(input: List<String>): Long {
        val maze = parseInput(input)

        return maze.findScore()
    }

    fun part2(input: List<String>): Long {
        val maze = parseInput(input)

        return maze.findBestTiles()
    }

    runDaySolutions(day, ::part1, ::part2)
}
