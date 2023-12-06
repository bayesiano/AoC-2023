import kotlin.math.max


object Day02 {

    @JvmStatic
    fun main(args: Array<String>) {
        runProblem("day02/in_example1.txt", problem="Day02.Example 1", solution = 8) { lines: List<String> ->
            solveProblem1(
                lines, mapOf(
                    "red" to 12, "green" to 13, "blue" to 14
                )
            )
        }

        runProblem("day02/in_problem_1.txt", problem="Day02.Problem 1", solution = 2377) { lines: List<String> ->
            solveProblem1(
                lines, mapOf(
                    "red" to 12, "green" to 13, "blue" to 14
                )
            )
        }

        runProblem("day02/in_example1.txt", problem="Day02.Example 2", solution = 2286, ::solveProblem2)
        runProblem("day02/in_problem_1.txt", problem="Day02.Problem 2", solution = 71220, ::solveProblem2)
    }


    private fun solveProblem1(lines: List<String>, cubesInBag: Map<String, Int>): Int {
        val games = lines.map { line ->
            val parts = line.split(':', ';')
//            println("parts=$parts")
            val numGame = parts[0].substring(5).toInt()
            val hands = parts.subList(1, parts.size).map { round ->
                val cubesHand = round.split(',').associate {
                    //println("hand=$it")
                    it.trim().split(' ').let { it[1] to it[0].toInt() }
                }.toMutableMap()
                cubesHand
            }
//            println("hands=$hands")
            val acc = hands.reduce { acc, hand ->
                hand.forEach { (color, num) ->
                    acc[color] = max(acc.getOrDefault(color, 0), num)
                }
                acc
            }

//            println("acc=$acc")
            Game(numGame, acc)
        }
        val gamesFiltered = games.filter { game -> game.isCompatible(cubesInBag) }

//        println("games filtered = " + gamesFiltered.joinToString { it.numGame.toString() })
        return gamesFiltered.sumOf { it.numGame }
    }

    private fun solveProblem2(lines: List<String>): Int {
        val games = lines.map { line ->
            val parts = line.split(':', ';')
//            println("parts=$parts")
            val numGame = parts[0].substring(5).toInt()
            val hands = parts.subList(1, parts.size).map { round ->
                val cubesHand = round.split(',').associate {
                    //println("hand=$it")
                    it.trim().split(' ').let { it[1] to it[0].toInt() }
                }.toMutableMap()
                cubesHand
            }
//            println("hands=$hands")
            val acc = hands.reduce { acc, hand ->
                hand.forEach { (color, num) ->
                    acc[color] = max(acc.getOrDefault(color, 0), num)
                }
                acc
            }

//            println("acc=$acc")
            Game(numGame, acc)
        }
        //val gamesFiltered = games.filter { game -> game.isCompatible( cubesInBag) }
//        games.forEach { game -> println("game ${game.numGame}, cubes = ${game.minCubes}, power = ${game.power()}") }

        return games.sumOf { it.power() }
    }

    data class Game(val numGame: Int, val minCubes: Map<String, Int>) {
        fun isCompatible(cubesInBag: Map<String, Int>) =
            minCubes.entries.all { e ->
                e.value <= cubesInBag.getOrDefault(e.key, 0)
            }

        fun power() = minCubes.values.reduce { acc, n -> acc * n }
    }

}

