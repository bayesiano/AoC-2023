object Day13b {
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

    private fun solveProblem1(input: String, maxSmudges: Int = 0): Int {
        val patterns = input.split("\n\n").map { Pattern.readPattern( it.split('\n')) }
        val res = patterns.mapIndexed{ i, pattern ->
            val res = pattern.findReflection(maxSmudges)
            log { "Pattern $i - res=$res" }
            res
        }.sum()
        return res
    }

    class Pattern(val patternH: List<Set<Int>>, val patternV: List<Set<Int?>>) {
        private val width = patternH.size
        private val height = patternV.size

        fun findReflection( maxSmudges: Int = 0): Int {
            (0..<height-1).forEach { j -> if( isVerticalReflection( j, j+1, maxSmudges)) return (j+1) }
            (0..< width-1).forEach { i -> if( isHorizontalReflection( i, i+1, maxSmudges)) return (i+1)*100 }
            log{ "ERROR: reflection not found"}
//            rocks.forEach { r -> log{ r} }
            return 0
        }

        private fun isHorizontalReflection(i1: Int, i2: Int, maxSmudges: Int): Boolean {
            if( i1 < 0 || i2 >= width)
                return maxSmudges == 0
            val diffs = patternH[i1].union( patternH[i2]).size - patternH[i1].intersect( patternH[i2]).size
            if( diffs > maxSmudges) return false
            return isHorizontalReflection(i1-1, i2+1, maxSmudges-diffs)
        }

        private fun isVerticalReflection(j1: Int, j2: Int, maxSmudges: Int): Boolean {
            if( j1 < 0 || j2 >= height)
                return maxSmudges == 0
            val diffs =  patternV[j1].union( patternV[j2]).size - patternV[j1].intersect( patternV[j2]).size
            if( diffs > maxSmudges) return false
            return isVerticalReflection(j1-1, j2+1, maxSmudges-diffs)
        }

        companion object {
            fun readPattern(lines: List<String>): Pattern {
                val patternH = lines.map { line ->
                    line.mapIndexedNotNull { i, c ->
                        if (c == '#') i else null
                    }.toSet()
                }
                val patternV = lines[0].indices.map { i ->
                    lines.indices.mapNotNull { j ->
                        if (lines[j][i] == '#') j else null
                    }.toSet()
                }
                return Pattern(patternH, patternV)
            }


        }
    }


    private fun log(s: () -> String) {
        if (debug) println(s())
    }
}
