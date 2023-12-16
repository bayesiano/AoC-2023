import Day16.MirrorMap.Dir.*
import kotlin.math.max

object Day16 {

    private const val debug = true
    private const val day = "day16"

    @JvmStatic
    fun main(args: Array<String>) {
        runProblemRaw("$day/example_1.txt", problem = "$day.Example 1", solution = 46, ::solveProblem1)
        runProblemRaw("$day/problem_1.txt", problem = "$day.Problem 1", solution = 6902, ::solveProblem1)
//
        runProblemRaw("$day/example_1.txt", problem = "$day.Example 2", solution = 51, ::solveProblem2)
        runProblemRaw("$day/problem_1.txt", problem = "$day.Problem 2", solution = 7697, ::solveProblem2)
    }

    private fun solveProblem1(input: String): Int {
        val map0 = MirrorMap(input.split('\n').map { it.toCharArray() }.toTypedArray())
        return map0.calculateRayTracingEnergy(0, 0, EAST)
    }

    private fun solveProblem2(input: String): Int {
        val map0 = MirrorMap(input.split('\n').map { it.toCharArray() }.toTypedArray())

        return max(
            map0.baseMap.indices.fold(0) { acc, y ->
                max(
                    max(acc, map0.calculateRayTracingEnergy(0, y, EAST)),
                    map0.calculateRayTracingEnergy(map0.baseMap[0].size - 1, y, WEST)
                )
            },
            map0.baseMap[0].indices.fold(0) { acc, x ->
                max(
                    max(acc, map0.calculateRayTracingEnergy(x, 0, SOUTH)),
                    map0.calculateRayTracingEnergy(x, map0.baseMap.size - 1, NORTH)
                )
            }
        )
    }

    class MirrorMap(val baseMap: Array<CharArray>) {
        enum class Dir(val incX: Int, val incY: Int) {
            NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0)
        }

        private fun clone(map: Array<CharArray>) = map.map { it.clone() }.toTypedArray()

        fun calculateRayTracingEnergy(x: Int, y: Int, dir: Dir): Int {
            val map = arrayOf(clone(baseMap), clone(baseMap), clone(baseMap), clone(baseMap))
            rayTracing(map, x, y, dir)
            return countEnergizedTiles(map)
        }

        private fun countEnergizedTiles(map: Array<Array<CharArray>>): Int =
            map[0].indices.sumOf { y ->
                map[0][y].indices.count { x ->
                    Dir.entries.indices.any { dir ->
                        map[dir][y][x] == '#'
                    }
                }
            }

        private fun rayTracing(map: Array<Array<CharArray>>, x: Int, y: Int, dir: Dir) {
            if (x !in baseMap[0].indices || y !in baseMap.indices || map[dir.ordinal][y][x] == '#') return
//        if( debug) print(map0, map)
            map[dir.ordinal][y][x] = '#'
            when (baseMap[y][x]) {
                '.' -> rayTracing(map, x + dir.incX, y + dir.incY, dir)
                '|' -> if (dir == EAST || dir == WEST) {
                    rayTracing(map, x + NORTH.incX, y + NORTH.incY, NORTH)
                    rayTracing(map, x + SOUTH.incX, y + SOUTH.incY, SOUTH)
                } else rayTracing(map, x + dir.incX, y + dir.incY, dir)

                '-' -> if (dir == NORTH || dir == SOUTH) {
                    rayTracing(map, x + EAST.incX, y + EAST.incY, EAST)
                    rayTracing(map, x + WEST.incX, y + WEST.incY, WEST)
                } else rayTracing(map, x + dir.incX, y + dir.incY, dir)

                '/' -> if (dir == EAST) {
                    rayTracing(map, x + NORTH.incX, y + NORTH.incY, NORTH)
                } else if (dir == SOUTH) {
                    rayTracing(map, x + WEST.incX, y + WEST.incY, WEST)
                } else if (dir == WEST) {
                    rayTracing(map, x + SOUTH.incX, y + SOUTH.incY, SOUTH)
                } else {
                    rayTracing(map, x + EAST.incX, y + EAST.incY, EAST)
                }

                '\\' -> if (dir == WEST) {
                    rayTracing(map, x + NORTH.incX, y + NORTH.incY, NORTH)
                } else if (dir == NORTH) {
                    rayTracing(map, x + WEST.incX, y + WEST.incY, WEST)
                } else if (dir == EAST) {
                    rayTracing(map, x + SOUTH.incX, y + SOUTH.incY, SOUTH)
                } else {
                    rayTracing(map, x + EAST.incX, y + EAST.incY, EAST)
                }

                else -> System.err.println("ERROR ($x,$y) $dir = ${baseMap[y][x]}")
            }
        }

        private fun printMap(map0: Array<CharArray>, map: Array<Array<CharArray>>) {
            map0.forEachIndexed { y, row ->
                row.forEachIndexed { x, c ->
                    if (c != '.') print(c)
                    else if (map[0][y][x] == '#') print('#')
                    else if (map[1][y][x] == '#') print('#')
                    else if (map[2][y][x] == '#') print('#')
                    else if (map[3][y][x] == '#') print('#')
                    else print('.')
                }
                println()
            }
            println()
        }
    }
}
    