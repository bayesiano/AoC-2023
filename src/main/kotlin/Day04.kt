import java.util.*
import kotlin.math.min

object Day04 {

    data class Card( val cardNumber: Int, val winningNumbers: Set<Int>, val cardNumbers: Set<Int>) {
        val wins = winningNumbers.intersect(cardNumbers).size
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runProblem("day04/example_1.txt", problem="Day04.Example 1", solution=13, ::solveProblem1)
        runProblem("day04/problem_1.txt", problem="Day04.Problem 1", solution=21485, ::solveProblem1)
        runProblem("day04/example_1.txt", problem="Day04.Example 2", solution=30, ::solveProblem2)
        runProblem("day04/problem_1.txt", problem="Day04.Problem 2", solution=11024379, ::solveProblem2)
    }

    private fun solveProblem1(lines: List<String>): Int {
        val cards = readCards(lines)
        return cards.map { powerOf(it.wins) }.sum()
    }

    private fun solveProblem2(lines: List<String>): Int {
        val cards = readCards(lines)

        val cardCopies = Array<Int>( cards.size) {1}
        cards.forEachIndexed { i, card ->
            (i + 1..min(i+card.wins,cardCopies.size-1)).forEach { j ->
                cardCopies[j] += cardCopies[i]
            }
        }
        return cardCopies.sum()
    }

    private fun readCards(lines: List<String>) =
        lines.map { line ->
            val scanner  = Scanner(line).useDelimiter("[ :]+")
            scanner.next() // Card
            val cardNumber = scanner.nextInt()
            val winningNumbers = mutableSetOf<Int>()
            while( scanner.hasNextInt()) winningNumbers += scanner.nextInt()
            scanner.next() // |
            val cardNumbers = mutableSetOf<Int>()
            while( scanner.hasNextInt()) cardNumbers += scanner.nextInt()

            Card( cardNumber, winningNumbers, cardNumbers)
        }

    private fun powerOf( exp: Int): Int {
        if( exp == 0) return 0
        var res = 1
        (2..exp).forEach { res *= 2 }
        return res
    }

}
