import kotlin.math.roundToInt

object Day08 {
    private const val debug = true
    const val day = "day08"

    @JvmStatic
    fun main(args: Array<String>) {
        runProblem("$day/example_1.txt", problem = "$day.Example 1", solution = 2, ::solveProblem1) // 6:52
        runProblem("$day/example_1b.txt", problem = "$day.Example 1b", solution = 6, ::solveProblem1) // 6:52
        runProblem(
            "$day/problem_1.txt",
            problem = "$day.Problem 1",
            solution = 13939,
            ::solveProblem1
        ) // 7:16  6127, 6501
////
        runProblem("$day/example_2.txt", problem = "$day.Example 2", solution = 6, ::solveProblem2)
        runProblem("$day/problem_1.txt", problem = "$day.Problem 2", solution = 8906539031197, ::solveProblem2)  // 8:09, 9242, 7036
    }

    private fun solveProblem1(lines: List<String>): Int {
        val pat = lines[0]

        val steps = lines.drop(2).map { line ->
            val (step, stepL, stepR) = line.split(" = (", ", ", ")")
            step to Pair(stepR, stepL)
        }.toMap()

        var curStep = "AAA"
        var totalSteps = 0
        while (curStep != "ZZZ") {
            pat.forEach { turn ->
                curStep = if (turn == 'R') steps[curStep]!!.first else steps[curStep]!!.second
                totalSteps++
            }
        }

        return totalSteps
    }


    // ***************************************************
    // ***
    // ***      PART 2
    // ***

    private fun solveProblem2(lines: List<String>): Long {
        val path = lines[0]

        val steps = lines.drop(2).map { line ->
            val (step, stepL, stepR) = line.split(" = (", ", ", ")")
            step to Pair(stepR, stepL)
        }.toMap()

        var totalSteps = 0L
        var curSteps = steps.keys.filter { it.endsWith('A') }.toMutableList()
        val zeds = curSteps.map { curStep ->
            var nextStep = curStep
            val alreadyVisit = mutableListOf<String>()
            val zeds = mutableListOf<Long>()
            var partialSteps = 1L
            while (nextStep !in alreadyVisit && !nextStep.endsWith('Z')) {
                alreadyVisit += nextStep
                path.forEach { turn ->
                    nextStep = if (turn == 'R') steps[nextStep]!!.first else steps[nextStep]!!.second
                    if( nextStep.endsWith('Z')) zeds.add(partialSteps)
                    partialSteps++
                }
            }
            log{ "partialSteps=$partialSteps"}
            zeds[0]
        }
        val gcd = zeds.reduce { acc, ints ->
            findLCM( acc, ints)
        }
        log{"zeds = $zeds, gcd=$gcd"}
//        var pathIndex = 0
//        while (curSteps.any { !it.endsWith('Z') }) {
//            val turn = path[pathIndex]
//            curSteps.indices.forEach { i ->
//                curSteps[i] = if (turn == 'R') steps[curSteps[i]]!!.first else steps[curSteps[i]]!!.second
//                totalSteps++
//            }
//            //log{"nextStep=$nextStep"}
//            pathIndex = (pathIndex + 1) % path.length
//            log { "curSteps=$curSteps" }
//        }



        return gcd
    }

    fun findLCM(a: Long, b: Long): Long {
        val larger = if (a > b) a else b
        val maxLcm = a * b
        var lcm = larger
        while (lcm <= maxLcm) {
            if (lcm % a == 0L && lcm % b == 0L) {
                return lcm
            }
            lcm += larger
        }
        return maxLcm
    }

    fun log(s: () -> String) {
        if (debug) println(s())
    }
}
