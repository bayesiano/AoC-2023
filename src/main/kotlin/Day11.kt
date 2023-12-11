import kotlin.math.abs

object Day11 {
    private const val debug = false
    private const val day = "day11"

    @JvmStatic
    fun main(args: Array<String>) {
        runProblemSeq("$day/example_1.txt", problem = "$day.Example 1", solution = 374, ::solveProblem1) // 6:03
        runProblemSeq("$day/problem_1.txt", problem = "$day.Problem 1", solution = 9639160, ::solveProblem1) // 7:05

        runProblemSeq("$day/example_1.txt", problem = "$day.Example 2a", solution = 1030) {
            solveProblem1( it, expansion = 10)
        }
        runProblemSeq("$day/example_1.txt", problem = "$day.Example 2b", solution = 8410) {
            solveProblem1( it, expansion = 100)
        }
        runProblemSeq("$day/problem_1.txt", problem = "$day.Problem 2", solution = 752936133304) {
            solveProblem1( it, expansion = 1_000_000)
        } // 7:24
    }

    private fun solveProblem1(lines: Sequence<String>, expansion: Long = 2L): Long
    {
        val expandedGalaxies = Galaxy.readGalaxies(lines, expansion).toList()
        log { "expandedGalaxies = $expandedGalaxies" }

        val pairs = expandedGalaxies.flatMapIndexed { i, g ->
            expandedGalaxies.drop(i + 1).map { g2 -> Route(g, g2) }
        }
        log { "expandedGalaxies.size=${expandedGalaxies.size}, pairs.size=${pairs.size},   ${expandedGalaxies.size * (expandedGalaxies.size - 1) / 2}" }
        return pairs.sumOf { it.distance }
    }


    data class Route(val g0: Galaxy, val g1: Galaxy) {
        val distance = abs(g0.x - g1.x) + abs(g0.y - g1.y)
    }

    data class Galaxy(val x: Long, val y: Long) {
        constructor(x: Int, y: Long) : this(x.toLong(), y)


        companion object {
            fun readGalaxies(lines: Sequence<String>, expansion: Long): List<Galaxy> {
                var y = 0L
                val galaxies = lines.mapNotNull { line ->
                    if (!line.contains('#')) {
//                log{ "Skipping $y"}
                        y += expansion
                        null
                    } else {
                        y++
                        line.mapIndexed { x, c -> if (c == '#') Galaxy(x, y) else null }
                    }
                }.flatMap { it }.filterNotNull().toList()

                val xs = galaxies.map { it.x }.toSortedSet().zipWithNext().filter { p -> p.second - p.first > 1 }
                    .flatMap { (it.first + 1..<it.second).toList() }
                    .toList()
                log { "xs = $xs" }

                return galaxies.map { g ->
                    var pos = 0
                    while (pos < xs.size && g.x > xs[pos]) pos++
                    val inc = pos
                    if (inc == 0) g
                    else Galaxy(g.x + inc*(expansion-1), g.y)
                }
            }
        }
    }

    // ***************************************************
    // ***
    // ***      PART 2
    // ***


    private fun log(s: () -> String) {
        if (debug) println(s())
    }
}
