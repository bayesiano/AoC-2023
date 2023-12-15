object Day14d {
    private const val debug = false
    private const val day = "day14"

    @JvmStatic
    fun main(args: Array<String>) {
        runProblemRaw("$day/example_1.txt", problem = "$day c.Example 1", solution = 136, ::solveProblem1)
        runProblemRaw("$day/problem_1.txt", problem = "$day c.Problem 1", solution = 109661, ::solveProblem1)
//
        runProblemRaw("$day/example_1.txt", problem = "$day c.Example 2", solution = 64, ::solveProblem2)
        runProblemRaw("$day/problem_1.txt", problem = "$day c.Problem 2", solution = 90176, ::solveProblem2)
    }


    private fun solveProblem1(input: String): Int {
        val map: MirrorMap = MirrorMap.parse(input.split('\n')).apply { tiltNorth() }
        return map.weight()
    }


    private fun solveProblem2(input: String, numCycles: Int = 1_000_000_000): Int {
        val map = MirrorMap.parse( input.split('\n') )

        val lastMaps = mutableListOf<Pair<Int,Int>>()
        var i = 0
        var loop = MirrorMap.NoLoop
        while (i < numCycles && loop == MirrorMap.NoLoop) {
            loop = map.findLoop(lastMaps)
            if (loop != MirrorMap.NoLoop) {
                return lastMaps[loop.second + (numCycles - i) % loop.first].second // weight
            } else {
                lastMaps += Pair(map.hashCode(), map.weight())


//                if( i > 0) map.rotate90r()
                map.tiltNorth()

//                map.print()
//                println()
                map.rotate90r()


                map.print()
                println()
                map.tiltNorthAndRotate()
                map.print()
                println()

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

        fun findLoop(lastMaps: List<Pair<Int,Int>>): Pair<Int, Int> {
            val occurrences = lastMaps.mapIndexedNotNull { i, map1 -> if (map1.first == this.hashCode()) i else null }
            if (occurrences.size < 2) return NoLoop
            val (o1, o2) = occurrences.drop(occurrences.size - 2)
            (0..<o2 - o1).forEach { i -> if (lastMaps[o1 + i] != lastMaps[o2 + i]) return NoLoop }
            return Pair(o2 - o1, o1)
        }

        override fun equals(other: Any?): Boolean {
            if (other !is MirrorMap) return false
            m.forEachIndexed { j, l -> l.forEachIndexed { i, c -> if (c != other.m[j][i]) return false } }
            return true
        }

        override fun hashCode() = m.contentDeepHashCode()

        fun tiltNorthAndRotate() {
            val width = m[0].size
            val m2 = Array<CharArray>( m.size) { CharArray( width) }
            var j2 = m.size - 1
            m[0].indices.forEach { i ->
                var lastFree = -1
                var i2 = 0
                m.indices.forEach { j ->
                    m2[j2][i2] = m[j][i]
                    if (lastFree < 0 && m[j][i] == '.') lastFree = j
                    if (m[j][i] == 'O' && lastFree >= 0 && lastFree != j) {
                        m2[j2][i2] = '.'
                        m2[j2][lastFree] = 'O'
                        lastFree++
                    }
                    if (lastFree >= 0 && m[j][i] != '.') lastFree = j + 1
                    i2++
                }
                j2--
            }
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
        fun rotate90l() {
            val height = m.size
            var i2 = 0
            val m2 = m.map{it.clone() }
            m.indices.forEach { j ->
                var j2 = height - 1
                m[0].indices.forEach { i ->
                    m[j2][i2] = m2[j][i]
                    j2--
                }
                i2++
            }
        }
        fun tiltSouth() {
            val height = m.size
            m[0].indices.forEach { i ->
                var lastFree = height
                (height - 1 downTo 0).forEach { j ->
                    if (lastFree >= height && m[j][i] == '.') lastFree = j
                    if (m[j][i] == 'O' && lastFree < height && lastFree != j) {
                        m[j][i] = '.'
                        m[lastFree][i] = 'O'
                        lastFree--
                    }
                    if (lastFree < height && m[j][i] != '.') lastFree = j - 1
                }
            }
        }

        fun tiltWest() {
            m.indices.forEach { j ->
                var lastFree = -1
                m[0].indices.forEach { i ->
                    if (lastFree < 0 && m[j][i] == '.') lastFree = i
                    if (m[j][i] == 'O' && lastFree >= 0 && lastFree != i) {
                        m[j][i] = '.'
                        m[j][lastFree] = 'O'
                        lastFree++
                    }
                    if (lastFree >= 0 && m[j][i] != '.') lastFree = i + 1
                }
            }
        }

        fun tiltEast() {
            val width = m[0].size
            m.indices.forEach { j ->
                var lastFree = width - 1
                (width - 1 downTo 0).forEach { i ->
                    //if( lastFree >= width && map[j][i] == '.') lastFree = i
                    if (m[j][i] == 'O' && lastFree < width && lastFree != i) {
                        m[j][i] = '.'
                        m[j][lastFree] = 'O'
                        lastFree--
                    }
                    if (lastFree < width && m[j][i] != '.') lastFree = i - 1
                }
            }
        }

        fun print() {
            m.forEach { println(it)}
        }
    }

    private fun log(s: () -> String) {
        if (debug) println(s())
    }

}
