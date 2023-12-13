import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

object Day13c {
    private const val debug = false
    private const val day = "day13"

    @JvmStatic
    fun main(args: Array<String>) {
        runProblemRaw("$day/example_1.txt", problem = "$day b.Example 1", solution = 405, ::solveProblem1)
        runProblemRaw("$day/problem_1.txt", problem = "$day b.Problem 1", solution = 37718, ::solveProblem1)

        runProblemRaw("$day/example_1.txt", problem = "$day b.Example 2", solution = 400) { input ->
            solveProblem1( input, 1)
        }
        runProblemRaw("$day/problem_1.txt", problem = "$day b.Problem 2", solution = 40995) { input ->
            solveProblem1( input, 1)
        }

        println()
    }

    private fun solveProblem1(input: String, maxSmudges: Int = 0): Int = runBlocking {
        input.split("\n\n").map { mirror ->
            async(Dispatchers.Default) {
                Pattern.readPattern( mirror.split('\n'))
                .findReflection(maxSmudges) }
        }.fold(0) { total, r -> total + r.await()}
    }

    class Pattern(private val patternH: Array<List<Int>>, private val patternV: Array<List<Int>>) {
        private val width = patternH.size
        private val height = patternV.size

        fun findReflection( maxSmudges: Int = 0): Int {
            (0..<height-1).forEach { j -> if( isReflection( patternV, j, j+1, height, maxSmudges)) return (j+1) }
            (0..< width-1).forEach { i -> if( isReflection( patternH, i, i+1, width, maxSmudges)) return (i+1)*100 }
            log{ "ERROR: reflection not found"}
            return 0
        }

        private fun isReflection( pattern: Array<List<Int>>, j1: Int, j2: Int, maxCoord:Int, maxSmudges: Int): Boolean {
            if( j1 < 0 || j2 >= maxCoord) return maxSmudges == 0
            val diffs = maxSmudges - distinct( pattern[j1], pattern[j2])
            return  if( diffs < 0 ) return false
                    else isReflection( pattern, j1-1, j2+1, maxCoord, diffs)
        }

        private fun distinct( l1: List<Int>, l2: List<Int>) = l1.count { it !in l2 } + l2.count { it !in l1 }

        companion object {
            fun readPattern(lines: List<String>): Pattern {
                val patternH = Array<List<Int>>( lines.size) { j ->
                    mutableListOf<Int>().apply {
                        lines[j].forEachIndexed { i, c -> if (c == '#') add(i) }
                    }
                }
                val patternV = Array<List<Int>>( lines[0].length) {  i ->
                    mutableListOf<Int>().apply {
                        lines.indices.forEach { j ->
                            if (lines[j][i] == '#')  add(j)
                        }
                    }

                }
                return Pattern(patternH, patternV)
            }
        }
    }

    private fun log(s: () -> String) {
        if (debug) println(s())
    }
}
