import kotlin.math.roundToInt

object Day06 {
    private const val debug = false

    @JvmStatic
    fun main(args: Array<String>) {
       runProblem("day06/example_1.txt", problem="Day06.Example 1", solution=288, ::solveProblem1) // 6436, 2017
        runProblem("day06/problem_1.txt", problem="Day06.Problem 1", solution=303600, ::solveProblem1)

        runProblem("day06/example_1.txt", problem="Day06.Example 2", solution=71503, ::solveProblem2)
        runProblem("day06/problem_1.txt", problem="Day06.Problem 2", solution=23654842, ::solveProblem2) // 7937, 1748
    }

    private fun solveProblem1(lines: List<String>): Int {
        val times = lines[0].substring(11).trim().split(Regex("[ ]+")).map { it.toLong() }
        val distances = lines[1].substring(11).trim().split(Regex("[ ]+")).map { it.toLong() }
        val races = times.zip(distances).map { Race(it.first, it.second) }

        log{ "races=$races}"}
        val res = races.map { race -> race.validStrategies() }.reduce { acc, i ->  acc * i}

        return res
    }


    data class Race( val time: Long, val recordDistance: Long) {
        fun validStrategies(): Int {
//            val strategies = (1..<time)
//                .map { awaiting -> Pair(awaiting,(time - awaiting) * awaiting) }
//                .filter { strategy -> strategy.second > recordDistance }
//            log{ "strategies=$strategies"}
            var res1 = ( (-time + Math.sqrt( time*time - 4.0*recordDistance) ) / -2.0).roundToInt()
            var res2 = ( (-time - Math.sqrt( time*time- 4.0*recordDistance) ) / -2.0 ).toInt()

            if( (time - res1) * res1 <= recordDistance) res1++
            if( (time - res2) * res2 <= recordDistance) res2--
//            log{ "res = $res1..$res2  -> ${res2-res1+1}  /  ${strategies.size}"}

//            return strategies.size
            return res2 - res1 + 1
        }
    }


    // ***************************************************
    // ***
    // ***      PART 2
    // ***

    private fun solveProblem2(lines: List<String>): Int {
        val time = lines[0].substring(11).filter { it.isDigit() }.toLong()
        val distance = lines[1].substring(11).filter { it.isDigit() }.toLong()
        val race = Race(time, distance)
        log{ "race=$race}"}

        val res = race.validStrategies()

        return res
    }



    fun log(s: () -> String) {
        if(debug) println( s() )
    }
}
