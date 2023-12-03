import java.io.File
import kotlin.system.measureTimeMillis

fun <T> runProblem(filename: String, function: (List<String>) -> T): T {
    val lines = File(Day03.javaClass.classLoader.getResource(filename)!!.file).readLines()
    var res: T
    val ms = measureTimeMillis {
        res = function( lines)
    }
    println( "Response = $res,  time=$ms ms")
    return res
}
