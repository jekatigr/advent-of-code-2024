package day05

import runDaySolutions

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

//    fun createGraph(rules: List<Pair<Int, Int>>): HashMap<Int, ArrayList<Int>> {
//        val graph = HashMap<Int, ArrayList<Int>>()
//
//        for ((left, right) in rules) {
//            if (left  !in graph) {
//                graph[left] = ArrayList()
//            }
//
//            graph[left]!!.add(right)
//        }
//
//        return graph
//    }
//
//    fun hasPath(graph: HashMap<Int, ArrayList<Int>>, from: Int, to: Int): Boolean {
//        var queue: MutableList<Int> = arrayListOf(from)
//        val visited: MutableList<Int> = arrayListOf()
//
//        while (queue.isNotEmpty()) {
//            val newQueue: MutableList<Int> = arrayListOf()
//
//            for (node in queue) {
//                if (node == to) {
//                    return true
//                }
//
//                visited.add(node)
//
//                if (node !in graph) {
//                    continue
//                }
//
//                for (conn in graph[node]!!) {
//                    if (visited.contains(conn)) {
//                        continue
//                    }
//
//                    newQueue.add(conn)
//                }
//            }
//
//            queue = newQueue
//        }
//
//        return false
//    }
//
//    fun part3(input: List<String>): Int {
//        var isRules = true
//
//        val rules: MutableList<Pair<Int, Int>> = ArrayList()
//        val updates: MutableList<List<Int>> = ArrayList()
//
//        for (line in input) {
//            if (line.isBlank()) {
//                isRules = false
//
//                continue
//            }
//
//            if (isRules) {
//                rules.add(Pair(line.split('|')[0].toInt(), line.split('|')[1].toInt()))
//            } else {
//                updates.add(line.split(",").map { it.toInt() })
//            }
//        }
//
//        val graph = createGraph(rules)
//
//        var result = 0
//
//        for (update in updates) {
//            check(update.size % 2 == 1) { "Update array has even length" }
//
//            var valid = true
//
//            for (i in 1..<update.size) {
//                if (!hasPath(graph, update[i - 1], update[i])) {
//                    valid = false
//                    break
//                }
//            }
//
//            if (!valid) {
//                continue
//            }
//
//            val mid = update.size / 2
//
//            result += update[mid]
//        }
//
//        return result
//    }

    fun checkUpdate(rules: HashSet<String>, update: List<Int>): Boolean {
        for (i in update.indices) {
            for (j in i + 1..<update.size) {
                val pair = "${update[j]}|${update[i]}"

                if (pair in rules) {
                    return false
                }
            }
        }

        return true
    }

    fun parseInput(input: List<String>): Pair<HashSet<String>, MutableList<MutableList<Int>>> {
        var isRules = true

        val rules: HashSet<String> = HashSet()
        val updates: MutableList<MutableList<Int>> = ArrayList()

        for (line in input) {
            if (line.isBlank()) {
                isRules = false

                continue
            }

            if (isRules) {
                rules.add(line)
            } else {
                updates.add(line.split(",").map { it.toInt() }.toMutableList())
            }
        }

        return Pair(rules, updates)
    }

    fun part1(input: List<String>): Long {
        val (rules, updates) = parseInput(input)

        var result = 0L

        for (update in updates) {
            if (checkUpdate(rules, update)) {
                val mid = update.size / 2

                result += update[mid]
            }
        }

        return result
    }

    fun swap(update: MutableList<Int>, i: Int, j: Int) {
        val temp = update[i]
        update[i] = update[j]
        update[j] = temp
    }

    fun updateUpdate(rules: HashSet<String>, update: MutableList<Int>) {
        for (i in update.indices) {
            var j = i + 1

            while (j < update.size) {
                val pair = "${update[j]}|${update[i]}"

                if (pair !in rules) {
                    j += 1
                    continue
                }

                swap(update, i, j)
                j = i + 1
            }
        }
    }

    fun part2(input: List<String>): Long {
        val (rules, updates) = parseInput(input)

        var result = 0L

        for (update in updates) {
            if (checkUpdate(rules, update)) {
                continue
            }

            updateUpdate(rules, update)

            val mid = update.size / 2

            result += update[mid]
        }

        return result
    }

    runDaySolutions(day, ::part1, ::part2)
}
