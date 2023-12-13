object Day13 {
    private const val debug = false
    private const val day = "day13"

    @JvmStatic
    fun main(args: Array<String>) {
        runProblemSeq("$day/example_1.txt", problem = "$day.Example 1", solution = 405, ::solveProblem1)
        runProblemSeq("$day/problem_1.txt", problem = "$day.Problem 1", solution = 37718, ::solveProblem1)

        runProblemSeq("$day/example_1.txt", problem = "$day.Example 2", solution = 400, ::solveProblem2)
        runProblemSeq("$day/problem_1.txt", problem = "$day.Problem 2", solution = 40995, ::solveProblem2) // 1974, 1695
    }

    private fun solveProblem1(lines: Sequence<String>): Int {
        val patterns = Pattern.readPatterns(lines).toList()
        val res = patterns.mapIndexed{ i, pattern ->
            val res = pattern.findReflection()
            log { "Pattern $i - res=$res" }
            res
        }.sum()
        return res
    }

    data class Pattern( val rocks: List<String>) {
        fun findReflection(): Int {
            (0..rocks.size-2).forEach { j -> if( isVertialReflection( j, j+1)) return (j+1)*100 }
            (0..rocks[0].length-2).forEach { i -> if( isHorizontalReflection( i, i+1)) return i+1 }
            log{ "ERROR: reflection not found"}
            rocks.forEach { r -> log{ r} }
            return 0
        }

        private fun isHorizontalReflection(i1: Int, i2: Int): Boolean {
            if( i1 < 0 || i2 >= rocks[0].length) return true
            rocks.indices.forEach { j ->
                if( rocks[j][i1] != rocks[j][i2]) return false
            }
            return isHorizontalReflection(i1-1, i2+1)
        }

        private fun isVertialReflection(j1: Int, j2:Int): Boolean {
            if( j1 < 0 || j2 >= rocks.size) return true
            if( rocks[j1] != rocks[j2]) return false
            return isVertialReflection(j1-1, j2+1)
        }

        fun findReflectionSmudged(): Int {
            (0..rocks.size-2).forEach { j -> if( isVertialReflectionSmudged( j, j+1)) return (j+1)*100 }
            (0..rocks[0].length-2).forEach { i -> if( isHorizontalReflectionSmudged( i, i+1)) return i+1 }
            log{ "ERROR: reflection not found"}
            rocks.forEach { r -> log{ r} }
            return 0
        }

        private fun isHorizontalReflectionSmudged(i1: Int, i2: Int, smudged: Boolean = false): Boolean {
            var _smudged = smudged
            if( i1 < 0 || i2 >= rocks[0].length) return smudged
            rocks.indices.forEach { j ->
                if( rocks[j][i1] != rocks[j][i2]) {
                    if( _smudged) return false
                    _smudged = true
                }
            }
            return isHorizontalReflectionSmudged(i1-1, i2+1, _smudged)
        }

        private fun isVertialReflectionSmudged(j1: Int, j2: Int, smudged: Boolean = false): Boolean {
            var _smudged = smudged
            if( j1 < 0 || j2 >= rocks.size) return smudged
            (0..<rocks[0].length).forEach { i ->
                if (rocks[j1][i] != rocks[j2][i]) {
                    if (_smudged) return false
                    _smudged = true
                }
            }
            return isVertialReflectionSmudged(j1-1, j2+1, _smudged)
        }

        companion object {
            fun readPatterns(lines: Sequence<String>): List<Pattern> {
                val paterns = mutableListOf<Pattern>()
                var group = mutableListOf<String>()
                lines.forEach { line ->
                    if (line.isEmpty()) {
                        paterns += Pattern(group)
                        group = mutableListOf<String>()
                    } else group += line
                }
                if (group.isNotEmpty()) {
                    paterns += Pattern(group)
                }
                return paterns
            }
//
//            fun readPatterns2(lines: Sequence<String>) = sequence {
//                val lastOne = lines.fold( listOf<String>()) { acc, line ->
//                    if (line.isEmpty()) {
//                        yield( Pattern(acc))
//                        listOf<String>()
//                    }
//                    else acc + line
//                }
//                yield (Pattern(lastOne))
//            }
        }
    }




    // ***************************************************
    // ***
    // ***      PART 2
    // ***

    private fun solveProblem2(lines: Sequence<String>): Int {
        val patterns = Pattern.readPatterns(lines)
        val res = patterns.mapIndexed{ i, pattern ->
            val res = pattern.findReflectionSmudged()
            log { "Pattern $i - res=$res" }
            res
        }.sum()
        return res
    }

    private fun log(s: () -> String) {
        if (debug) println(s())
    }
}
