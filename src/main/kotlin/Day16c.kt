import Day16c.MirrorMap.Dir.*
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.max

// Improved using bitwise trace map thanks to the idea of Jakub Gwóźdź (jakubgwozdz)

object Day16c {

    private const val debug = true
    private const val day = "day16"

    @JvmStatic
    fun main(args: Array<String>) {
        runProblemRaw("$day/example_1.txt", problem = "$day C.Example 1", solution = 46, ::solveProblem1)
        runProblemRaw("$day/problem_1.txt", problem = "$day C.Problem 1", solution = 6902, ::solveProblem1)

        runProblemRaw("$day/example_1.txt", problem = "$day C.Example 2", solution = 51, ::solveProblem2)
        runProblemRawTest("$day/problem_1.txt", problem = "$day C.Problem 2", solution = 7697, ::solveProblem2)
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
        enum class Dir(val incX: Int, val incY: Int, val mask: Byte) {
            NORTH(0, -1, 1),
            EAST(1, 0, 2),
            SOUTH(0, 1, 4),
            WEST(-1, 0, 8)
        }

        private val mirror270 = mapOf( NORTH to WEST, EAST to SOUTH, SOUTH to EAST, WEST to NORTH)
        private val mirror90 = mapOf( NORTH to EAST, EAST to NORTH, SOUTH to WEST, WEST to SOUTH)

        private fun newRayTracingMap(width: Int, height: Int) =
            Array(height) {ByteArray(width)}

        fun calculateRayTracingEnergy(x: Int, y: Int, dir: Dir): Int {
            val map = newRayTracingMap(baseMap[0].size, baseMap.size)
            rayTracing(map, x, y, dir)
            return countEnergizedTiles(map)
        }

        private fun countEnergizedTiles(map: Array<ByteArray>): Int =
            map.indices.sumOf { y ->
                map[y].indices.count { x -> map[y][x] > 0 }
            }

        private fun rayTracing(map: Array<ByteArray>, x0: Int, y0: Int, dir0: Dir) {
            var x = x0
            var y = y0
            var dir = dir0
            while (true) {
                if (x !in baseMap[0].indices || y !in baseMap.indices || map[y][x] and dir.mask > 0) return
//        if( debug) print(map0, map)
                map[y][x] = map[y][x] or dir.mask
                when (baseMap[y][x]) {
                    '|' -> if (dir == EAST || dir == WEST) {
                        rayTracing(map, x + NORTH.incX, y + NORTH.incY, NORTH)
                        dir = SOUTH
                    }

                    '-' -> if (dir == NORTH || dir == SOUTH) {
                        rayTracing(map, x + EAST.incX, y + EAST.incY, EAST)
                        dir = WEST
                    }

                    '/' -> dir = mirror90[dir]!!
                    '\\' -> dir = mirror270[dir]!!
                }
                x += dir.incX
                y += dir.incY
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
    