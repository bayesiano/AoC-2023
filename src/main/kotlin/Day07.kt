import kotlin.math.roundToInt

object Day07 {
    private const val debug = true

    @JvmStatic
    fun main(args: Array<String>) {
       runProblem("day07/example_1.txt", problem="Day07.Example 1", solution=6440, ::solveProblem1)
        runProblem("day07/problem_1.txt", problem="Day07.Problem 1", solution=252656917, ::solveProblem1) // 7830, 3372
//
        runProblem("day07/example_1.txt", problem="Day07.Example 2", solution=5905, ::solveProblem2)
        runProblem("day07/problem_1.txt", problem="Day07.Problem 2", solution=23654842, ::solveProblem2) // 28359   6540
        // 253636299 too high
    }

    private fun solveProblem1(lines: List<String>): Int {
        val cardsBids = lines.map { line ->
            val (cardStr, bid) = line.split(" ")
            Pair( Card(cardStr), bid.toInt())
        }
        val cardsBidsSorted = cardsBids.sortedBy { it.first }
        cardsBidsSorted.forEach { log{ "cards=$it}"} }

        return cardsBidsSorted.mapIndexed { i, pair -> (i + 1) * pair.second}.sum()
    }

    class Card(private val _cardStr: String): Comparable<Card> {
        private val cardStr = _cardStr.toList().sortedBy { letterOrder.indexOf(it) }
        private val repetitions = cardStr.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }
        private val type = classify(cardStr)

        private fun classify(cardStr: List<Char>): HandType {
//            val maxRepetitions = repetitions.values.max()
            log{ "repetitions=$repetitions"}

            if( repetitions[0].value == 5) return HandType.FiveOfAKind
            if( repetitions[0].value == 4) return HandType.FourOfAKind
            if( repetitions.size == 2) return HandType.FullHouse
            if( repetitions[0].value == 3) return HandType.ThreeOfAKind
            if( repetitions.size == 3) return HandType.TwoPair
            if( repetitions[0].value == 2) return HandType.OnePair
            return HandType.HighCard
        }

        override fun compareTo(other: Card): Int {
            val typeComp = type.ordinal - other.type.ordinal
            if( typeComp != 0) return typeComp
            _cardStr.forEachIndexed { i, c ->
                val higherCardComp = letterOrder.indexOf(other._cardStr[i]) - letterOrder.indexOf(c)
                if( higherCardComp != 0) return higherCardComp
            }
            return 0
        }

        override fun toString(): String {
            return "Card(_cardStr=$_cardStr, cardStr=$cardStr, type=$type)"
        }

        companion object {
            enum class HandType {
                HighCard, OnePair, TwoPair, ThreeOfAKind, FullHouse, FourOfAKind, FiveOfAKind
            }

            val letterOrder = "AKQJT98765432"
        }
    }



    // ***************************************************
    // ***
    // ***      PART 2
    // ***

    private fun solveProblem2(lines: List<String>): Int {
        val cardsBids = lines.map { line ->
            val (cardStr, bid) = line.split(" ")
            Pair( Card2(cardStr), bid.toInt())
        }
        val cardsBidsSorted = cardsBids.sortedBy { it.first }
        cardsBidsSorted.forEach { log{ "cards=$it}"} }

        return cardsBidsSorted.mapIndexed { i, pair -> (i + 1) * pair.second}.sum()
    }

    class Card2(private val _cardStr: String): Comparable<Card2> {
        private val cardStr = _cardStr.toList().sortedBy { letterOrder.indexOf(it) }
        val numberJs = cardStr.count { it == 'J' }
        private var repetitions = calculateRepetitions(cardStr)

        private fun calculateRepetitions(cardStr: List<Char>) =
            cardStr.filter { it != 'J' }.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.toMutableList()
            //if( repetitions.size == 1) return repetitions


        private val type = classify(cardStr)

        private fun classify(cardStr: List<Char>): HandType {
//            val maxRepetitions = repetitions.values.max()
            log{ "repetitions=$repetitions"}

            if( numberJs == 5) return HandType.FiveOfAKind
            if( numberJs + repetitions[0].value == 5) return HandType.FiveOfAKind

            if( numberJs + repetitions[0].value == 4) return HandType.FourOfAKind
            if( numberJs == 4) return HandType.FourOfAKind

            if( repetitions.size == 2) return HandType.FullHouse
            if( repetitions.size == 1 && numberJs >= 2) return HandType.FullHouse

            if( numberJs + repetitions[0].value == 3) return HandType.ThreeOfAKind
            if( numberJs == 3) return HandType.ThreeOfAKind

            if( repetitions[0].value == 2 && repetitions[1].value == 2) return HandType.TwoPair
            if( repetitions[0].value == 2 && numberJs >= 1) return HandType.TwoPair

            if( repetitions[0].value == 2) return HandType.OnePair
            if( numberJs >= 1) return HandType.OnePair
            return HandType.HighCard
        }

        override fun compareTo(other: Card2): Int {
            val typeComp = type.ordinal - other.type.ordinal
            if( typeComp != 0) return typeComp
            _cardStr.forEachIndexed { i, c ->
                val higherCardComp = letterOrder.indexOf(other._cardStr[i]) - letterOrder.indexOf(c)
                if( higherCardComp != 0) return higherCardComp
            }
            return 0
        }

        override fun toString(): String {
            return "Card(_cardStr=$_cardStr, cardStr=$cardStr, type=$type)"
        }

        companion object {
            enum class HandType {
                HighCard, OnePair, TwoPair, ThreeOfAKind, FullHouse, FourOfAKind, FiveOfAKind
            }

            val letterOrder = "AKQT98765432J"
        }
    }


    fun log(s: () -> String) {
        if(debug) println( s() )
    }
}
