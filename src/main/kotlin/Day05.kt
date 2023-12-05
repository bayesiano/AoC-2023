object Day05 {
    private const val debug = false

    @JvmStatic
    fun main(args: Array<String>) {
       runProblem("day05/example_1.txt", problem="Day05.Example 1", solution=35L, ::solveProblem1)
        runProblem("day05/problem_1.txt", problem="Day05.Problem 1", solution=379811651L, ::solveProblem1) // 1916, 7094

        runProblem("day05/example_1.txt", problem = "Day05.Example 1", solution = 46L, ::solveProblem2)
        runProblem("day05/problem_1.txt", problem = "Day05.Problem 1", solution = 27992443, ::solveProblem2) // 17924, 19806
    }

    private fun solveProblem1(lines: List<String>): Long {
        val values = lines[0].substring(5).getLongs().first.toLongArray()
        translateSeeds(values, lines)
        return values.min()
    }

    private fun translateSeeds(seeds: LongArray, lines: List<String>): LongArray {
        log("seeds                 = ${seeds.joinToString { "$it" }}")

        var lines2 = lines.drop(3)
        lines2 = seeds.translate(lines2)
        log("seed->soil            = ${seeds.joinToString { "$it" }}")

        lines2 = seeds.translate(lines2)
        log("soil->fertilizer      = ${seeds.joinToString { "$it" }}")

        lines2 = seeds.translate(lines2)
        log("fertilizer->water     = ${seeds.joinToString { "$it" }}")

        lines2 = seeds.translate(lines2)
        log("water->light          = ${seeds.joinToString { "$it" }}")

        lines2 = seeds.translate(lines2)
        log("light->temperature    = ${seeds.joinToString { "$it" }}")

        lines2 = seeds.translate(lines2)
        log("temperature->humidity = ${seeds.joinToString { "$it" }}")

        seeds.translate(lines2)
        log("humidity->location    = ${seeds.joinToString { "$it" }}")

        return seeds
    }


    private fun LongArray.translate(lines: List<String>): List<String> {
        var lineNumber = 0
        val vars2 = this.clone()
        while (lineNumber < lines.size - 1 && lines[lineNumber].isNotEmpty()) {
            val (translation, _) = lines[lineNumber].getLongs()
            val startSourceRange = translation[1]
            val startTargetRange = translation[0]
            val lengthRange = translation[2]
            lineNumber++
            this.forEachIndexed { j, seed ->
                if (seed in startSourceRange..<startSourceRange + lengthRange) {
                    vars2[j] += startTargetRange - startSourceRange
                    return@forEachIndexed
                }
            }
        }
        vars2.forEachIndexed { i, value -> this[i] = value }
        return lines.drop(lineNumber + 2)
    }


    // ***************************************************
    // ***
    // ***      PART 2
    // ***

    private fun solveProblem2(lines: List<String>): Long {
        val seedsRanges = lines[0].substring(5).getLongs().first
        val seeds = seedsRanges.chunked(2).map { w ->
            (w[0]..<w[0] + w[1])
        }
        return NumRanges(lines).translateSeeds(seeds)
    }

    data class NumRanges(val lines: List<String>) {
        private var lineNumber = 3

        private val seed2soil = readTranslation()
        private val soil2fertilizer = readTranslation()
        private val fertilizer2water = readTranslation()
        private val water2light = readTranslation()
        private val ligth2temperature = readTranslation()
        private val temperature2humidity = readTranslation()
        private val humidity2location = readTranslation()



        fun translateSeeds(seeds: List<LongRange>): Long {
            log("seeds = ${seeds.joinToString { "$it" }}")

            val soil = translate(seeds.asSequence(), seed2soil)
            val fertilizer = translate(soil, soil2fertilizer)
            val water = translate(fertilizer, fertilizer2water)
            val light = translate(water, water2light)
            val temperature = translate(light, ligth2temperature)
            val humidity = translate(temperature, temperature2humidity)
            val location = translate(humidity, humidity2location)
            val min = location.reduce { acc, range ->
                if( acc.first <= range.first) acc else range
            }.first
            //log( "min=$min, count=$count")
            return min
        }

        private fun translate(ranges: Sequence<LongRange>, translator: Translator): Sequence<LongRange> = sequence {
                ranges.forEach { range ->
                    val res = translateRange( range, translator.translations)
                    yieldAll(res)
            }
        }

        private fun translateRange(range: LongRange, translations: List<Translation>): Sequence<LongRange> = sequence {
            if( translations.isNotEmpty()) {

                val translation = translations.first()

                val diff = translation.target.first - translation.source.first

                if (range.last < translation.source.first || range.first > translation.source.last) {
                    yieldAll(translateRange(range, translations.drop(1)))
                } else if (range.first < translation.source.first && range.last <= translation.source.last) {
                    var start = range.first
                    var end = translation.source.first - 1
                    yieldAll(translateRange(start..end, translations.drop(1)))

                    start = translation.target.first
                    end = range.last + diff
                    yield(start..end)
                } else if (range.first >= translation.source.first && range.last > translation.source.last) {
                    var start = range.first + diff
                    var end = translation.target.last
                    yield(start..end)

                    start = translation.source.last + 1
                    end = range.last
                    yieldAll(translateRange(start..end, translations.drop(1)))
                } else if (range.first >= translation.source.first && range.last <= translation.source.last) {
                    val start = range.first + diff
                    val end = range.last + diff
                    yield(start..end)
                } else if (range.first < translation.source.first && range.last > translation.source.last) {
                    var start = range.first
                    var end = translation.source.first - 1
                    yieldAll(translateRange(start..end, translations.drop(1)))

                    start = translation.target.first
                    end = translation.target.last
                    yield(start..end)

                    start = translation.source.last + 1
                    end = range.last
                    yieldAll(translateRange(start..end, translations.drop(1)))
                } else {
                    println("ERRRRRORRRRR !!!!!!")
                }
            }
            else yield( range)
        }

        data class Translation(val source: LongRange, val target: LongRange)
        data class Translator(val translations: List<Translation>)

        private fun readTranslation(): Translator {
            //lineNumber++
            val translations = mutableListOf<Translation>()
            while (lineNumber < lines.size && lines[lineNumber].isNotEmpty()) {
                val (translation, _) = lines[lineNumber].getLongs()
                val startTargetRange = translation[0]
                val startSourceRange = translation[1]
                val lengthRange = translation[2]
                lineNumber++
                translations += Translation(
                    startSourceRange..<startSourceRange + lengthRange,
                    startTargetRange..<startTargetRange + lengthRange
                )
            }
            lineNumber+=2
            return Translator(translations)
        }
    }


    fun log(s: String) {
        if(debug) println(s)
    }
}
