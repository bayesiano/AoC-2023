object Day10 {
    private const val debug = false
    private const val day = "day10"

    @JvmStatic
    fun main(args: Array<String>) {
        runProblem("$day/example_1.txt", problem = "$day.Example 1", solution = 4, ::solveProblem1)
        runProblem("$day/example_1b.txt", problem = "$day.Example 1", solution = 8, ::solveProblem1)
        runProblem("$day/problem_1.txt", problem = "$day.Problem 1", solution = 6815, ::solveProblem1)

        runProblem("$day/example_2.txt", problem = "$day.Example 2", solution = 4, ::solveProblem2)
        runProblem("$day/example_2b.txt", problem = "$day.Example 2b", solution = 4, ::solveProblem2)
        runProblem("$day/example_2c.txt", problem = "$day.Example 2c", solution = 8, ::solveProblem2)
        runProblem("$day/problem_1.txt", problem = "$day.Problem 2c", solution = 269, ::solveProblem2)

    }

    private fun List<Pipes.CoordXY>.penultimate() =
        if (size < 2) last()
        else get(size - 2)


    private fun solveProblem1(lines: List<String>): Int {
        val pipes = Pipes(lines.map { it.map { c -> c } })
        //log{  "start(${routes})"}

        val longestLoop = pipes.longestLoop()

        return longestLoop.size / 2
    }

    private fun permutations(routes: List<List<Pipes.CoordXY>>): List<List<Pipes.CoordXY>> =
        listOf(routes[0] + routes[1].subList(0, routes[1].size - 1).reversed())


    class Pipes(val map: List<List<Char>>) {
        //        val start = findStart()
        private fun findStart(): CoordXY {
            val startY = map.indexOfFirst { it.contains('S') }
            val startX = map[startY].indexOf('S')
            return CoordXY(startX, startY)
        }

        private val toNorthMovs = listOf('|', 'L', 'J', 'S')
        private val toEastMovs = listOf('-', 'L', 'F', 'S')
        private val toSouthMovs = listOf('|', '7', 'F', 'S')
        private val toWestMovs = listOf('-', '7', 'J', 'S')

        private fun getNexts(route: List<CoordXY>): List<CoordXY> {
            val pos = route.last()
            val from = route.penultimate()
            val options = mutableListOf<CoordXY>()
            with(pos.north) {
                if (from != this && getContent(pos) in toNorthMovs && getContent(this) in toSouthMovs)
                    options.add(this)
            }
            with(pos.east) {
                if (from != this && getContent(pos) in toEastMovs && getContent(this) in toWestMovs)
                    options.add(this)
            }
            with(pos.south) {
                if (from != this && getContent(pos) in toSouthMovs && getContent(this) in toNorthMovs)
                    options.add(this)
            }
            with(pos.west) {
                if (from != this && getContent(pos) in toWestMovs && getContent(this) in toEastMovs)
                    options.add(this)
            }
            return options
        }

        fun getContent(pos: CoordXY) =
            if (pos.y !in map.indices || pos.x !in map[pos.y].indices) '.'
            else map[pos.y][pos.x]

        fun getContent( x: Int, y: Int) =
            if (y !in map.indices || x !in map[y].indices) '.'
            else map[y][x]

        fun longestLoop(): List<CoordXY> {
            var routes = listOf(listOf(findStart()))
            var sol = listOf<CoordXY>()

            while (routes.isNotEmpty()) {
                routes = routes.flatMap { route ->
                    val nexts = getNexts(route)
                    nexts.map { n -> route + n }
                }
                //log{ "routes=$routes"}
                val touching = routes.filter { r0 -> routes.any { r1 -> r1 != r0 && r1.last() == r0.last() } }
                if (touching.isNotEmpty()) {
                    val newSol = permutations(touching).maxBy { it.size }
                    if (newSol.size > sol.size) sol = newSol
                    //log{ "sol=$sol  touching routes $touching, lengths=${touching.map { it.size }}"}
                    routes = routes.filter { r -> r !in touching }
                }
            }
            return sol
        }

        data class CoordXY(val x: Int, val y: Int) {
            val north get() = CoordXY(x, y - 1)
            val south get() = CoordXY(x, y + 1)
            val east get() = CoordXY(x + 1, y)
            val west get() = CoordXY(x - 1, y)
        }
    }

    // ***************************************************
    // ***
    // ***      PART 2
    // ***

    private fun solveProblem2(lines: List<String>): Int {
        val pipes = Pipes(lines.map { it.map { c -> c } })
        val longestLoop = pipes.longestLoop()

        val maxY = longestLoop.map { it.y }.max()
        val exs = (0..maxY)
            .map { y ->
                longestLoop.filter { pos ->
                    pos.y == y && pipes.getContent(Pipes.CoordXY(pos.x, y)) != '-'
                }
                    .map { it.x }
                    .toSortedSet().toList()
            }

        val tilesInside = exs.mapIndexed { y, row ->
            val rowContent = row.map { x -> pipes.getContent(x, y) }.joinToString("")
//            val rowContentAroundStart = "${}rowContent.last()
//            if( rowContentLast == 'F' && rowContent[1] == 'J' ) rowContentLast
            sumRowTilesInside(row, rowContent)
        }.sum()

        return tilesInside
    }

    private fun sumRowTilesInside(
        row: List<Int>, rowContent: String, inside: Boolean = false,
        lastXinside: Int = 0, res: Int = 0
    ): Int =
        if (row.isEmpty()) res
        else if (rowContent[0] == '|') {
            if (!inside)  sumRowTilesInside( row.drop(1), rowContent.drop(1), true, lastXinside = row[0], res )
            else sumRowTilesInside( row.drop(1), rowContent.drop(1), false, lastXinside = 0,
                res + row[0] - lastXinside - 1)
        }
        else if (!inside) {
                if (rowContent.startsWith("J|") || rowContent.startsWith("7|")) {
                    sumRowTilesInside(
                        row.drop(2), rowContent.drop(2), inside = false,
                        lastXinside = 0, res + row[0] - row[1] - 1
                    )
                } else if (rowContent.startsWith("L7") || rowContent.startsWith("FJ")) {
                    sumRowTilesInside(row.drop(2), rowContent.drop(2), true, lastXinside = row[1], res)
                } else if (rowContent.startsWith("LJ") || rowContent.startsWith("F7")) {
                    sumRowTilesInside(row.drop(2), rowContent.drop(2), false, lastXinside = 0, res)
                } else if (rowContent.startsWith("S7")) {
                    sumRowTilesInside(row.drop(2), rowContent.drop(2), false, lastXinside = 0, res)
                } else if (rowContent.startsWith("FS")) {
                    sumRowTilesInside(row.drop(2), rowContent.drop(2), false, lastXinside = 0, res)
                } else {
                    throw Exception("Error Outside  $row - $rowContent")
                }
        } else {
                if (rowContent.startsWith("L7") || rowContent.startsWith("FJ")) {
                    sumRowTilesInside(
                        row.drop(2), rowContent.drop(2), false,
                        lastXinside = 0, res + row[0] - lastXinside - 1
                    )
                } else if (rowContent.startsWith("F7") || rowContent.startsWith("LJ")) {
                    sumRowTilesInside( row.drop(2), rowContent.drop(2), true, lastXinside = row[1],
                        res + row[0] - lastXinside - 1
                    )
                } else if (rowContent.startsWith("S7")) {
                    sumRowTilesInside( row.drop(2), rowContent.drop(2), false, lastXinside = 0,
                        res + row[0] - lastXinside - 1
                    )
                } else {
                    throw Exception("Error Inside  $row - $rowContent")
                }
            }



    fun log(s: () -> String) {
        if (debug) println(s())
    }

}