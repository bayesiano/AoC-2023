

object Day01 {

    @JvmStatic
    fun main(args: Array<String>) {
        val example1 = runProblem("day01/in_example_1.txt", ::solveProblem1)
        assert(example1 == 142) { System.err.println("ERROR at Example 1") }

        val problem1 = runProblem("day01/in_problem_1.txt", ::solveProblem1)
        assert(problem1 == 53080) { System.err.println("ERROR at Problem 1") }

        val example2 = runProblem("day01/in_example_2.txt", ::solveProblem2)
        assert(example2 == 281) { System.err.println("ERROR at Example 2") }

        val problem2 = runProblem("day01/in_problem_1.txt", ::solveProblem2)
        assert(problem2 == 53268) { System.err.println("ERROR at Example 2") }

    }


    private fun solveProblem1(lines: List<String>): Int {
        val codes = lines.map { line ->
            val firstDigit = line.find { it.isDigit() }!!.digitToInt()
            val lasttDigit = line.findLast { it.isDigit() }!!.digitToInt()
            //println( "$firstDigit-$lasttDigit")
            firstDigit * 10 + lasttDigit
        }
        println("codes=$codes")
        return codes.sum()
    }

    val strDigits = listOf(
        "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5,
        "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9,
        "0" to 0, "1" to 1, "2" to 2, "3" to 3, "4" to 4,
        "5" to 5, "6" to 6, "7" to 7, "8" to 8, "9" to 9
    )

    private fun solveProblem2(lines: List<String>): Int {
        val codes = lines.map { line ->
            val firstDigit = searchFirst(line)
            val lastDigit = searchLast(line)
            println("$firstDigit-$lastDigit")
            firstDigit * 10 + lastDigit
        }
        println("codes=$codes")
        return codes.sum()
    }

    private fun searchFirst(line: String) =
        strDigits.mapNotNull { digit ->
            val i = line.indexOf(digit.first)
            if (i < 0) null
            else i to digit.second
        }.minBy { it.first }.second

    private fun searchLast(line: String) =
        strDigits.mapNotNull { digit ->
            val i = line.lastIndexOf(digit.first)
            if (i < 0) null
            else i to digit.second
        }.maxBy { it.first }.second

}