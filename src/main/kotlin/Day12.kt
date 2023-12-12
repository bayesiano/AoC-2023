object Day12 {
    private const val debug = false
    private const val day = "day12"

    @JvmStatic
    fun main(args: Array<String>) {
        runProblemSeq("$day/example_1.txt", problem = "$day.Example 1", solution = 21L, ::solveProblem1) // 6:40
        runProblemSeq("$day/problem_1.txt", problem = "$day.Problem 1", solution = 7047L, ::solveProblem1)

        runProblemSeq("$day/example_1.txt", problem = "$day.Example 1", solution = 525152L, ::solveProblem2)
        runProblemSeq("$day/problem_1.txt", problem = "$day.Problem 1", solution = 17391848518844L, ::solveProblem2)

        println("cache entries = ${cache2.size}")
    }

    private fun solveProblem1(lines: Sequence<String>): Long {
        val total = lines.mapIndexed { i, line ->
            val (springs, groupsStr) = line.split(' ')
            val groups = groupsStr.split(',').map { it.toInt() }
            val res = arrangements2(Problem2(springs, 0, null, groups))
            log { "RES $springs = $res ----------------------------------" }
//            println("RES $i-$springs = $res ----------------------------------")
            res
        }.sum()
        return total
    }

    // ***************************************************
    // ***
    // ***      PART 2
    // ***

    private fun solveProblem2(lines: Sequence<String>): Long {
        val total = lines.mapIndexed { i, line ->
            val (springs, groupsStr) = line.split(' ')
            val springsN = (1..<5).fold( springs) { acc, _ -> "$acc?$springs" }
            val groupsStrN = (1..<5).fold( groupsStr) { acc, _ -> "$acc,$groupsStr" }
            val groupsN = groupsStrN.split(',').map { it.toInt() }
//            println("$i-$springsN - %groupsStr5 =  ----------------------------------")
            val res = arrangements2(Problem2(springsN, 0, null, groupsN))
            log { "RES $springsN = $res ----------------------------------" }
//            println("RES $i-$springsN - $groupsStrN = $res - (cache=${cache2.keys.size}) ----------------------------------")
            res
        }.sum()
        return total
    }

    data class Problem2(val springs: String, val iSpring: Int, val current: Int?, val groups: List<Int>)

    private val cache2 = mutableMapOf<Problem2, Long>()

    private val blanks = Regex("""\.+""")

    private fun arrangements2(p: Problem2): Long {
        // Cache check
        cache2[p]?.let { return it }

        // Help variables
//        val previous = p.springs.substring(0, p.iSpring)
        val nextSprings = p.springs.substring(p.iSpring)

        // End conditions check
        if (nextSprings.trim('.', '?').isEmpty() && p.groups.isEmpty() && (p.current == null || p.current == 0))
            return 1
        if (nextSprings.trim('.').isEmpty() && (p.groups.isNotEmpty() || (p.current != null && p.current > 0)))
            return 0

        // Tricks to discard impossible branches. Reduce cache from 762106 to 427578 but increase time from +-275.06 ms to +-592.977 ms
        //
        if (nextSprings.contains('#') && (p.groups.isEmpty() && (p.current == null || p.current == 0))) return 0

        val minChars = p.groups.sumOf { it + 1 } - 1
        if (nextSprings.length < minChars) return 0

        val minNotEmpty = p.groups.sumOf { it }
        if (nextSprings.filter { it != '.' }.length < minNotEmpty) return 0

        val maxPossibleBlock = nextSprings.split(blanks).maxOf { it.length }
        if (p.groups.any { it > maxPossibleBlock }) return 0

        // Main checks and recursive call
        val res2 = when (p.springs[p.iSpring]) {
            '.' -> when (p.current) {
                        null -> arrangements2(Problem2(p.springs, p.iSpring + 1, null, p.groups))
                        0 -> arrangements2(Problem2(p.springs, p.iSpring + 1, null, p.groups))
                        else -> 0
                    }


            '#' -> if (p.current == null) {
                        if (p.groups.isEmpty()) 0
                        else arrangements2(Problem2(p.springs, p.iSpring + 1, p.groups[0] - 1, p.groups.drop(1)))
                    }
                    else if (p.current == 0) 0
                    else arrangements2(Problem2(p.springs, p.iSpring + 1, p.current - 1, p.groups))

            '?' -> when (p.current) {
                        null -> arrangements2(Problem2(p.springs, p.iSpring + 1, null, p.groups)) +
                                if (p.groups.isEmpty()) 0
                                else arrangements2(Problem2(p.springs, p.iSpring + 1, p.groups[0] - 1, p.groups.drop(1)))

                        0 -> arrangements2(Problem2(p.springs, p.iSpring + 1, null, p.groups))
                        else -> arrangements2(Problem2(p.springs, p.iSpring + 1, p.current - 1, p.groups))
                    }
            else -> 0
        }
        // Caches the result
        cache2[p] = res2
        return res2
    }

    private fun log(s: () -> String) {
        if (debug) println(s())
    }
}
