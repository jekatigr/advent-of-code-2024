package day23

import runDaySolutions

data class Cycle(val nodes: Collection<String>) {
    val id: String
        get() {
            return nodes.sorted().fold("") { acc, id -> acc + id }
        }
    val hasT: Boolean
    get() = nodes.any { it.startsWith("t") }
}

class Cycles {
    val length: Int
        get() = map.size
    val map = mutableMapOf<String, Cycle>()

    fun add(cycle: Cycle) {
        map[cycle.id] = cycle
    }
}

class Graph {
    private val graph = mutableMapOf<String, MutableSet<String>>()

    fun add(node1: String, node2: String) {
        if (node1 !in graph) {
            graph[node1] = mutableSetOf()
        }

        graph[node1]!!.add(node2)

        if (node2 !in graph) {
            graph[node2] = mutableSetOf()
        }

        graph[node2]!!.add(node1)
    }

    /**
     * Returns list of nodes from a cycle or null in case no cycle found
     */
    private fun dfs(startNode: String, cycleLength: Int, parent: String? = null, visited: MutableMap<String, Int> = mutableMapOf(), step: Int = 0): List<MutableSet<String>>? {
        if (step >= cycleLength) {
            return null
        }

        visited[startNode] = step

        val cyclesNodesList: MutableList<MutableSet<String>> = mutableListOf()

        for (node in graph[startNode]!!) {
            if (node in visited) {
                if (node != parent && step - visited[node]!! == cycleLength - 1) { // found cycle
                    cyclesNodesList.add(mutableSetOf())
                }

                continue
            }

            val cyclesNodesListInternal = dfs(node, cycleLength, startNode, visited, step + 1)

            if (cyclesNodesListInternal != null) {
                cyclesNodesList.addAll(cyclesNodesListInternal)
            }
        }

        visited.remove(startNode)

        cyclesNodesList.forEach { set -> set.add(startNode) }

        return cyclesNodesList
    }

    fun findCycles(cycleLength: Int): Cycles {
        val nodes = graph.keys
        val cycles = Cycles()

        for (node in nodes) {
            val list = dfs(node, cycleLength)

            if (list != null) {
                for (cycleNodes in list) {
                    cycles.add(Cycle(cycleNodes))
                }
            }
        }

        return cycles
    }

    private fun findMaxParty(): Set<String> {
        var maxSet = setOf<String>()
        var maxSetLength = 0

        for (node in graph.keys) {
            val children = graph[node]!!

            val freq = mutableMapOf<String, Int>()
            var freqMax = 0

            for (child in children) {
                var connected = 0

                for (lanPartyNode in children) {
                    if (child == lanPartyNode) {
                        continue
                    }

                    if (child in graph[lanPartyNode]!!) {
                        connected += 1
                    }
                }

                freq[child] = connected
                freqMax = freqMax.coerceAtLeast(freq[child]!!)
            }

            if (freqMax >= maxSetLength - 1) {
                val set = mutableSetOf(node)

                for ((key, value) in freq) {
                    if (value == freqMax) {
                        set.add(key)
                    }
                }

                if (set.size >= maxSetLength) {
                    maxSet = set
                    maxSetLength = set.size
                }
            }
        }

        return maxSet
    }

    fun findLANPartyPassword(): String {
        val party = findMaxParty()

        return party.sorted().joinToString(",")
    }
}

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun parseInput(input: List<String>): Graph {
        val graph = Graph()
        for (line in input) {
            val (from, to) = line.split("-")

            graph.add(from, to)
        }

        return graph
    }

    fun part1(input: List<String>): Int {
        val graph = parseInput(input)

        val cycles = graph.findCycles(3)
        val list = cycles.map.values.toList()

        return list.filter { cycle -> cycle.hasT }.size
    }

    fun part2(input: List<String>): String {
        val graph = parseInput(input)

        return graph.findLANPartyPassword()
    }

    runDaySolutions(day, ::part1, ::part2)
}
