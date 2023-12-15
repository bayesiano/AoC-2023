object Day14c {
    private const val debug = false
    private const val day = "day14"

    @JvmStatic
    fun main(args: Array<String>) {
        runProblemRaw("$day/example_1.txt", problem = "$day C.Example 1", solution = 136, ::solveProblem1)
        runProblemRaw("$day/problem_1.txt", problem = "$day C.Problem 1", solution = 109661, ::solveProblem1)
//
        runProblemRaw("$day/example_1.txt", problem = "$day C.Example 2", solution = 64, ::solveProblem2)
        runProblemRaw("$day/problem_1.txt", problem = "$day C.Problem 2", solution = 90176, ::solveProblem2)
    }


    private fun solveProblem1(input: String): Int {
        val map: MirrorMap = MirrorMap.parse(input.split('\n')).apply { tiltNorth() }
        return map.weight()
    }


    private fun solveProblem2(input: String, numCycles: Int = 1_000_000_000): Int {
        var map = MirrorMap.parse( input.split('\n') )

        val lastMaps = mutableListOf<MirrorMap>()
        var i = 0
        var loop = MirrorMap.NoLoop
        while (i < numCycles && loop == MirrorMap.NoLoop) {
            loop = map.findLoop(lastMaps)
            if (loop != MirrorMap.NoLoop) {
                map = lastMaps[loop.second + (numCycles - i) % loop.first]
                break
            } else {
                lastMaps += map.clone()

                map.tiltNorth()
                map.rotate90r()
                map.tiltNorth()
                map.rotate90r()
                map.tiltNorth()
                map.rotate90r()
                map.tiltNorth()
                map.rotate90r()
            }
            i++
        }
        return map.weight()
    }


    class MirrorMap(private val m: Array<CharArray>) {
        fun weight(): Int = m.mapIndexed { j, l -> l.count { it == 'O' } * (m.size - j) }.sum()

        companion object {
            fun parse(str: List<String>) = MirrorMap(str.map { it.toCharArray() }.toTypedArray())

            val NoLoop = Pair(-1, -1)
        }

        fun findLoop(lastMaps: List<MirrorMap>): Pair<Int, Int> {
            val occurrences = lastMaps.mapIndexedNotNull { i, map1 -> if (map1 == this) i else null }
            if (occurrences.size < 3) return NoLoop
            val (o1, o2, o3) = occurrences.drop(occurrences.size - 3)
            if( o2-o1 != o3-o2) return NoLoop
            (0..<o2 - o1).forEach { i -> if (lastMaps[o1 + i] != lastMaps[o2 + i]) return NoLoop }
            return Pair(o2 - o1, o1)
        }

        override fun equals(other: Any?): Boolean {
            if (other !is MirrorMap) return false
            m.forEachIndexed { j, l -> l.forEachIndexed { i, c -> if (c != other.m[j][i]) return false } }
            return true
        }


        fun tiltNorth() {
            m[0].indices.forEach { i ->
                var lastFree = -1
                m.indices.forEach { j ->
                    if (lastFree < 0 && m[j][i] == '.') lastFree = j
                    if (m[j][i] == 'O' && lastFree >= 0 && lastFree != j) {
                        m[j][i] = '.'
                        m[lastFree][i] = 'O'
                        lastFree++
                    }
                    if (lastFree >= 0 && m[j][i] != '.') lastFree = j + 1
                }
            }
        }

        fun rotate90r() {
            var i2 = m[0].size - 1
            val m2 = m.map{it.clone() }
            m.indices.forEach { j ->
                var j2 = 0
                m[0].indices.forEach { i ->
                    m[j2][i2] = m2[j][i]
                    j2++
                }
                i2--
            }
        }
        fun clone(): MirrorMap = MirrorMap(m.map { it.clone() }.toTypedArray())


    }

    private fun log(s: () -> String) {
        if (debug) println(s())
    }

}
