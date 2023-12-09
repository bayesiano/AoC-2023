object Day09 {
    private const val debug = false
    const val day = "day09"

    @JvmStatic
    fun main(args: Array<String>) {
        runProblem("$day/example_1.txt", problem = "$day.Example 1", solution = 114, ::solveProblem1) // 6:02
        runProblem("$day/problem_1.txt", problem = "$day.Problem 1", solution = 1702218515, ::solveProblem1) // 6:42, 6266 1162
        // 1167888682955405 No

        runProblem("$day/example_2.txt", problem = "$day.Example 2", solution = 5, ::solveProblem2) //
        runProblem("$day/problem_1.txt", problem = "$day.Problem 2", solution = 925, ::solveProblem2) // 6:50, 7134 1013
    }

    private fun solveProblem1(lines: List<String>): Long {
        val sequences = lines.map { it.split(" ").map { it.toLong() } }

        val nextsInSequence = sequences.map { seq ->
            log{"---------------------------------------------"}
            searchNextPreviousInSequence( seq, PreviousNext.NEXT)
        }
        log{ "nextsInSequence=$nextsInSequence"}

        return nextsInSequence.sum()
    }

    // ***************************************************
    // ***
    // ***      PART 2
    // ***

    private fun solveProblem2(lines: List<String>): Long {
        val sequences = lines.map { it.split(" ").map { it.toLong() } }

        val previousInSequence = sequences.map { seq ->
            log{"---------------------------------------------"}
            searchNextPreviousInSequence( seq, PreviousNext.PREVIOUS)
        }
        log{ "previousInSequence=$previousInSequence"}

        return previousInSequence.sum()
    }

    enum class PreviousNext {
        PREVIOUS, NEXT;

        fun getValue( seq: List<Long>, previousNextValue: Long) =
            if (this == PREVIOUS) seq.first() - previousNextValue
            else seq.last() + previousNextValue

    }

    private fun searchNextPreviousInSequence( seq: List<Long>, previousNext: PreviousNext): Long {
        if( seq.all { it == 0L }) return 0
        val newSeq = seq.windowed(2).map { it[1] - it[0] }
        val value = searchNextPreviousInSequence( newSeq, previousNext)
        log{ "${seq.first() - value}, seq=$seq,   previous=$value"}
        return previousNext.getValue( seq, value)
    }

    fun log(s: () -> String) {
        if (debug) println(s())
    }
}
