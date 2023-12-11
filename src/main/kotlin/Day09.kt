import kotlin.system.measureNanoTime

object Day09 {
    private const val debug = false
    const val day = "day09"

    @JvmStatic
    fun main(args: Array<String>) {
        runProblem("$day/example_1.txt", problem = "$day.Example 1", solution = 114, ::solveProblem1) // 6:02
        runProblem(
            "$day/problem_1.txt",
            problem = "$day.Problem 1",
            solution = 1702218515,
            ::solveProblem1
        ) // 6:42, 6266 1162

        val t1 = measureNanoTime {
            (1..100).forEach {
                runProblem(
                    "$day/problem_1.txt",
                    problem = "$day.Problem 1",
                    solution = 1702218515,
                    ::solveProblem1
                ) // 6:42, 6266 1162
            }
        }
        // 1167888682955405 No
        println( "t1=${t1/100_000_000.0}")
        // t1=2.2709345
        // t1=2.06130599
        // t1=1.99002117
        // t1=1.37572613
        // t1=1.34806805

        runProblem("$day/example_2.txt", problem = "$day.Example 2", solution = 5, ::solveProblem2) //
        val t2 = measureNanoTime {
            (1..100).forEach {
                runProblem(
                    "$day/problem_1.txt",
                    problem = "$day.Problem 2",
                    solution = 925,
                    ::solveProblem2
                ) // 6:50, 7134 1013
            }
        }
        println( "t2=${t2/100_000_000.0}")
    }
    // t2=1.49324834
    // t2=1.44216908
    // t2=1.5012199
    // t2=1.04478074
    // t2=1.20821276

    private fun solveProblem1(lines: List<String>): Long {
        val sequences = lines.map { it.split(" ").map { it.toLong() } }

        val nextsInSequence = sequences.map { seq ->
            log{"---------------------------------------------"}
            searchNextInSequence( seq)
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
            searchNextInSequence( seq.reversed())
        }
        log{ "previousInSequence=$previousInSequence"}

        return previousInSequence.sum()
    }


    private tailrec fun searchNextInSequence(seq: List<Long>, acc: Long = 0L): Long =
        if( seq.all { it == 0L }) acc
        else searchNextInSequence( seq.zipWithNext { a, b -> b - a }, acc + seq.last())


    fun log(s: () -> String) {
        if (debug) println(s())
    }
}
