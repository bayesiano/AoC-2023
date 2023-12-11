object Day10 {
    private const val debug = false
    const val day = "day10"

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

    private fun permutations(routes: List<List<Day10.Pipes.CoordXY>>): List<List<Day10.Pipes.CoordXY>> =
        listOf(routes[0] + routes[1].subList(0, routes[1].size - 1).reversed())


    class Pipes(val map: List<List<Char>>) {
        //        val start = findStart()
        fun findStart(): CoordXY {
            val startY = map.indexOfFirst { it.contains('S') }
            val startX = map[startY].indexOf('S')
            return CoordXY(startX, startY)
        }

        val toNorthMovs = listOf('|', 'L', 'J', 'S')
        val toEastMovs = listOf('-', 'L', 'F', 'S')
        val toSouthMovs = listOf('|', '7', 'F', 'S')
        val toWestMovs = listOf('-', '7', 'J', 'S')

        fun getNexts(route: List<CoordXY>): List<CoordXY> {
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

        fun longestLoop(): List<Pipes.CoordXY> {
            var routes = listOf(listOf(findStart()))
            var sol = listOf<Pipes.CoordXY>()

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
        var totalInside = 0
        /*
                val isInside = Array<Boolean>(pipes.map.size) { false}
                (0..<pipes.map.maxOf { it.size }).forEach { x ->
                    isInside.indices.forEach { y ->
                        val pos = Pipes.CoordXY( x, y)

                        if( isInside[y] && pipes.getContent(pos) == '.')
                            totalInside++
                        else {
                            if (pos in longestLoop && !isInside[y]) {
                                when (pipes.getContent(pos)) {
                                    '|' ->
                                        isInside[y] = true
                                }
                            } else if (pos in longestLoop && isInside[y]) {
                                when (pipes.getContent(pos)) {
                                    '|', '7', 'J', 'F', 'L'  ->
                                        isInside[y] = false
                                }
                            }
                        }
                    }
                    log { "[$x] isInside=" + isInside.mapIndexed { y, v ->
                        if (v && pipes.getContent(Pipes.CoordXY(x, y)) == '.') 'I'
                        else if (v) 'i'
                        else ' '
                    } }
                }
        */

        /*
        val isInside = Array<Boolean>(pipes.map.size) { false}
        (0..<pipes.map.maxOf { it.size }).forEach { x ->
            isInside.indices.forEach { y ->
                val pos = Pipes.CoordXY( x, y)
                if( isInside[y] && pipes.getContent(pos) == '.') {
                    totalInside++
                    log{ "- $pos ss inside"}
                }

                val nextPos = Pipes.CoordXY( x+1, y)
                if( pos in longestLoop && nextPos !in longestLoop) {
                    isInside[y] = true
                }
                else if( pos !in longestLoop && nextPos in longestLoop) {
                    isInside[y] = false

                }
            }
        }
*/
        val maxY = longestLoop.map { it.y }.max()
        val exs = (0..maxY)
            .map { y ->
                longestLoop.filter { pos ->
                    pos.y == y && pipes.getContent(Pipes.CoordXY(pos.x, y)) != '-'
                }
                    .map { it.x }
                    .toSortedSet()
            }

//        val exs2 = exs.mapIndexed{ y, row ->
//            var inside = false
//            row.windowed(2, step = 2).mapIndexed { count, l ->
//
//                val content0 = pipes.getContent( Pipes.CoordXY( l[0], y))
//                val content1 = pipes.getContent( Pipes.CoordXY( l[1], y))
//
//                val res = if (!inside) {
//                    if (content0 == '|' && content1 == '|' ) {
//                        l[1] - l[0]- 1
//                    }
//                    else if (content0 == '|' && content1 == 'F' ) {
//                        l[1] - l[0]- 1
//                    }
//                    else if (content0 == '|' && content1 == 'L' ) {
//                        l[1] - l[0]- 1
//                    }
//                    else if (content0 == 'L' && content1 == '7' ) {
//                        inside = true
//                        0
//                    }
//                    else if (content0 == 'F' && content1 == 'J' ) {
//                        inside = true
//                        0
//                    }
//                    else 0
//                }
//                else {
//                    if (content0 == 'L' && content1 == '7' ) {
//                        inside = false
//                        0
//                    }
//                    else if (content0 == 'F' && content1 == 'J' ) {
//                        inside = false
//                        0
//                    }
//                    //else if (!inside/*count % 2 == 0*/ && (pipes.getContent( Pipes.CoordXY( l[0], y)) == '|' || pipes.getContent( Pipes.CoordXY( l[1], y)) == '|' )) {
//                    else 0
//                }
//                log { "y[$y], count=$count, $l, content=$content0 - $content1, sum=$res, inside= $inside" }
//                res
//            }
//        }
        /*        val exs2 = exs.mapIndexed{ y, row ->
                    var inside = false
                    row.windowed(2, step = 1).mapIndexed { count, l ->
                        // oFJiL7oL7iLJiLJi|o|iLJiIL-7oOO
                        val content0 = pipes.getContent( Pipes.CoordXY( l[0], y))
                        val content1 = pipes.getContent( Pipes.CoordXY( l[1], y))
                        val content = "$content0$content1"

                        val res = if (!inside) {
                            if (content == "||" ) {
                                inside = true
                                l[1] - l[0]- 1
                            }
                            else if (content == "|F" ) {
                                inside = true
                                l[1] - l[0]- 1
                            }
                            else if (content == "|L" ) {
                                inside = true
                                l[1] - l[0]- 1
                            }
                            else if (content == "J|" ) {
                                inside = true
                                l[1] - l[0]- 1
                            }
                            else if (content == "7|" ) {
                                inside = true
                                l[1] - l[0]- 1
                            }
                            else if (content == "7L" ) {
                                inside = true
                                0
                            }
                            else if (content == "L7" ) {
                                inside = false
                                0
                            }
                            else if (content == "FJ" ) {
                                inside = true //false
                                0
                            }
                            else if (content == "JL" ) {
                                inside = true //false
                                0
                            }
                            else if (content == "F7" ) {
                                inside = true
                                0
                            }
                            else 0
                        }
                        else {
                            if (content == "||" ) {
                                inside = false
                                0
                            }
                            else if (content == "7|" ) {
                                inside = false
                                0
                            }
                            else if (content == "J|" ) {
                                inside = false
                                0
                            }
                            else if (content == "7F" ) {
                                inside = false
                                0
                            }
                            else if (content == "J|" ) {
                                inside = false
                                l[1] - l[0]- 1
                            }
                            else if (content == "L7" ) {
                                inside = true //false //true
                                0
                            }
                            else if (content == "FJ" ) {
                                inside = true// false
                                0
                            }
                            else if (content == "JL" ) {
                                inside = true// false
                                l[1] - l[0]- 1
                            }
                            //else if (!inside/*count % 2 == 0*/ && (pipes.getContent( Pipes.CoordXY( l[0], y)) == '|' || pipes.getContent( Pipes.CoordXY( l[1], y)) == '|' )) {
                            else 0
                        }
                        log { "y[$y], count=$count, $l, content=$content0 - $content1, sum=$res, inside= $inside" }
                        res
                    }
                }*/
        val exs2 = exs.mapIndexed { y, row ->
            var inside = false
            var x = 0
            var lastX = 0
            val row2 = row.toList()
            val res = mutableListOf<Int>()
            //val endWithPipe = if( pipes.getContent(Pipes.CoordXY(row2.last(), y)) == '|') 1 else 0
            while (x < row.size) {
                //row.windowed(2, step = 2).mapIndexed { count, l ->
                // oFJi L7o L7i LJi LJi |o |i LJi IL-7o OO

                // oFiJi + L7o Li7i + LiJi + LiJi ||i Li Ji IL-7o OO
                // FJL7L 7LJLJ ||LJI L-7OO
                val content0 = pipes.getContent(Pipes.CoordXY(row2[x], y))
                if (content0 == '|') {
                    if (!inside) {
                        inside = true
                        lastX = row2[x]
                        x += 1
                        //res += l[1] - l[0] - 1
                    } else {
                        inside = false
                        res += row2[x] - lastX - 1
                        x += 1
                    }
                    log { "y[$y], x=$x, $row2[0], content=$content0 ,  sum=$res, inside= $inside, lastX=$lastX" }
                } else {
                    val l = listOf(row2[x], row2[x + 1])
                    val content1 = pipes.getContent(Pipes.CoordXY(l[1], y))
                    val content = "$content0$content1"

                    if (!inside) {
                        if (content == "J|") {
                            inside = false
                            x += 2
                            res += l[1] - l[0] - 1
                        } else if (content == "7|") {
                            inside = false
                            x += 2
                            res += l[1] - l[0] - 1
                        }
//                    else if (content == "7L" ) {
//                        inside = true
//                        0
//                    }
                        else if (content == "L7") {
                            inside = true
                            x += 2
//                        res += x - lastX -1
                            lastX = l[1]
                        } else if (content == "LJ") {
                            inside = false
                            x += 2
//                        res += x - lastX -1
                            //res += l[1] - l[0] - 1
                        } else if (content == "FJ") {
                            inside = true //false
                            x += 2
                            lastX = l[1]
//                        res += x - lastX -1
                        } else if (content == "F7") {
                            inside = false
                            x += 2
//                        res += x - lastX -1
                        } else if (content == "S7") {
                            inside = false
                            x += 2
                            0
                        } else if (content == "FS") {
                            inside = false
                            x += 2
                        } else {
                            throw Exception("Error $inside  ($x,$y) - $content")
                        }
                    } else {
                        if (content == "L7") {
                            inside = false // true false //true
                            res += l[0] - lastX - 1
                            x += 2
                        } else if (content == "FJ") {
                            inside = false // true// false
                            x += 2
                            res += l[0] - lastX - 1
                        } else if (content == "F7") {
                            inside = true
                            x += 2
//                        res += l[0] - lastX -1
                            res += l[0] - lastX - 1
                            lastX = l[1]
                        } else if (content == "LJ") {
                            inside = true
                            x += 2
                            res += l[0] - lastX - 1
                            lastX = l[1]
                        } else if (content == "S7") {
                            inside = false
                            x += 2
                            res += l[0] - lastX - 1
                            lastX = l[1]
                        }
//                    else if (content == "JL" ) {
//                        inside = true// false
//                        l[1] - l[0]- 1
//                    }
                        //else if (!inside/*count % 2 == 0*/ && (pipes.getContent( Pipes.CoordXY( l[0], y)) == '|' || pipes.getContent( Pipes.CoordXY( l[1], y)) == '|' )) {
                        else {
                            throw Exception("Error $inside  ($x,$y) - $content")
                            0
                        }
                    }
                    log { "y[$y], x=$x, $l, content=$content ,  sum=$res, inside= $inside, lastX=$lastX" }
                }
//                lastX = l[1]

            }
            res
        }

        exs2.forEachIndexed { y, values ->
            log { "exs[$y]=$values" }
        }
        return exs2.sumOf { it.sum() }
    }

    fun log(s: () -> String) {
        if (debug) println(s())
    }
}
