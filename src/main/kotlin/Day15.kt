object Day15 {
    private const val debug = false
    private const val day = "day15"

    @JvmStatic
    fun main(args: Array<String>) {
        log{ "HASH test=" + hash( "HASH") }
        runProblemRaw("$day/example_1.txt", problem = "$day.Example 1", solution = 1320, ::solveProblem1)
        runProblemRaw("$day/problem_1.txt", problem = "$day.Problem 1", solution = 516804, ::solveProblem1)

        runProblemRaw("$day/example_1.txt", problem = "$day.Example 2", solution = 145, ::solveProblem2)
        runProblemRaw("$day/problem_1.txt", problem = "$day.Problem 2", solution = 231844, ::solveProblem2)
    }

    private fun solveProblem1(input: String): Int {
        val hashes = input.split(",").map { hash(it) }
//        log { "hashes=$hashes" }
        return hashes.sum()
    }

    fun hash( str:String): Int =
        str.fold(0) { acc, c -> ((acc + c.code) * 17 ) % 256}

    private fun solveProblem2(input: String): Int {
        val boxes = Array(256) { mutableListOf<Lens>() }
        input.split(",").forEach { op ->
            val (label, focalLength) = op.split('=', '-')
            val box = hash(label)

            if( op.contains('-')) boxes[box].removeIf { l -> l.label == label}
            else boxes[box].indexOfFirst{ it.label == label}.let { i ->
                if (i >= 0) boxes[box][i].focalLength = focalLength.toInt()
                else boxes[box] += Lens(label, focalLength.toInt())
            }
        }
//        boxes.forEachIndexed { i, b -> if( b.isNotEmpty()) log{"Box $i = $b"} }
        return boxes.mapIndexed { i, lenses -> lenses.mapIndexed { slot, l -> (i + 1) * (slot + 1) * l.focalLength }.sum() }.sum()
    }

    data class Lens( val label: String, var focalLength: Int)


    private fun log(s: () -> String) {
        if (debug) println(s())
    }

}
