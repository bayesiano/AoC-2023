
import kotlin.text.StringBuilder

object Day03 {
    @JvmInline
    value class CoordX(val x: Int)

    data class PartNumber(val number: Long, val range: IntRange) {
        constructor(number: StringBuilder, x0: Int) : this(
            number.toString().toLong(),
            IntRange(x0 - 1, x0 + number.length)
        )

        fun touches(x: Int) = x in range
    }

    data class Line(val numbers: List<PartNumber>, val symbolCoords: List<CoordX>)

    private const val EmptyChar = '.'

    @JvmStatic
    fun main(args: Array<String>) {
        val example1 = runProblem("day03/in_1.txt", ::solveProblem1)
        assert(example1 == 533784L)
        val prloblem1 = runProblem("day03/in_2.txt", ::solveProblem1)
        assert(prloblem1 == 533784L)

        val example2 = runProblem("day03/in_b_1.txt", ::solveProblem2)
        assert(example2 == 467835L)
        val prloblem2 = runProblem("day03/in_2.txt", ::solveProblem2)
        assert(prloblem2 == 78826761L)
    }


    private fun solveProblem1(lines: List<String>): Long {
        val candidates = generateCandidates(lines)
        val filtered = candidates.mapIndexed { y, line ->
            val lineFiltered = line.numbers.filter { n ->
                candidates[y].symbolCoords.any { n.touches(it.x) }
                        || (y > 0 && candidates[y - 1].symbolCoords.any { n.touches(it.x) })
                        || (y < candidates.size - 1 && candidates[y + 1].symbolCoords.any { n.touches(it.x) })
            }
            lineFiltered
        }

        val res1 = filtered.map { it.map { it.number }.sum() }
        val res = res1.sum()
        return res
    }

    private fun generateCandidates(lines: List<String>) =
        lines.map { line ->
            val numbers = mutableListOf<PartNumber>()
            val symbolCoords = mutableListOf<CoordX>()
            var partialNumber = StringBuilder("")
            var x0 = -1

            line.forEachIndexed { x, c ->
                if (c.isDigit()) {
                    if (partialNumber.isEmpty()) x0 = x
                    partialNumber.append(c)
                } else if (!c.isDigit()) {
                    if (partialNumber.isNotEmpty()) {
                        numbers += PartNumber(partialNumber, x0)
                        partialNumber = StringBuilder("")
//                        x0 = -1
                    }
                }
                if (c != EmptyChar && !c.isDigit()) symbolCoords += CoordX(x)
            }
            if (partialNumber.isNotEmpty())
                numbers += PartNumber(partialNumber, x0)

            Line(numbers, symbolCoords)
        }

    private fun solveProblem2(lines: List<String>): Long {
        val candidates = generateCandidates(lines)

        val filtered = candidates.mapIndexed { y, line ->
            val gearsParts = line.symbolCoords.map { c ->
                line.numbers.filter { it.touches(c.x) } +
                        candidates[y - 1].numbers.filter { it.touches(c.x) } +
                        candidates[y + 1].numbers.filter { it.touches(c.x) }
            }
            gearsParts
        }.map { it.filter { it.size > 1 } }

        val res = filtered.flatMap { l ->
            l.map { gear ->
                gear[0].number * gear[1].number
            }
        }.sum()
        return res
    }
}
