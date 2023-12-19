
object Day19 {
    private const val debug = false
    private val day = this::class.simpleName!!.lowercase()

    @JvmStatic
    fun main(args: Array<String>) {
        runProblemRaw("$day/example_1.txt", "$day.Example 1", solution = 19114, ::solveProblem1)
        runProblemRaw("$day/problem_1.txt", "$day.Problem 1", solution = 397643, ::solveProblem1)

        runProblemRaw("$day/example_1.txt", "$day.Example 2", solution = 167409079868000L, ::solveProblem2)
        runProblemRaw("$day/problem_1.txt", "$day.Problem 2", solution = 132392981697081L, ::solveProblem2)
    }

    private fun solveProblem1(input: String): Int =
        parse(input).let { (workflows, parts) ->
            parts.sumOf { part ->
                workflows.applyWorkflow(part)?.sum() ?: 0
            }
        }

    data class Workflow(val name: String, val rules: List<Rule>, val default: String) {
        fun processPart(p: Part): String {
            rules.forEach { r ->
                if (r.op == '<' && p.getQty(r.type) < r.qty || r.op == '>' && p.getQty(r.type) > r.qty) return r.next
            }
            return default
        }
    }
    data class Workflows( val table: Map<String,Workflow>): Map<String,Workflow> by table {
        constructor( input: String) : this( input.split('\n').associate { wf0 ->
            wf0.split( Regex("[{}, ]+") ).filter { it.isNotEmpty() }.let { l ->
                val name = l[0]
                val rules = l.drop(1).dropLast(1).map { Rule.parse(it) }
                val default = l.last()
                name to Workflow(name, rules, default)
            }
        })
        fun applyWorkflow( part: Part, wf: String = "in"): Part? =
            when (wf) {
                "A" -> part
                "R" -> null
                else -> applyWorkflow(part, this[wf]!!.processPart(part))
            }
        fun solve2( wf: String, part: Map<String, List<IntRange>>): List<Map<String, List<IntRange>>> {
            val res = mutableListOf<Map<String, List<IntRange>>>()

            var part2 = part
            this[wf]!!.rules.forEach { r ->
                when (r.next) {
                    "R" -> {}
                    "A" -> res += applyRule(r, part2)
                    else -> res += solve2(r.next, applyRule(r, part2))
                }
                part2 = applyRule(r, part2, false)
            }
            if (this[wf]!!.default == "A") res += part2
            else if (this[wf]!!.default != "R") res += solve2(this[wf]!!.default, part2)

            res.forEach { lr -> log { "  - $lr" } }
            return res
        }
    }

    data class Rule(val type: String, val op: Char, val qty: Int, val next: String) {
        companion object {
            private val rxRule = Regex("([a-z]+)([<>])([0-9]+):([a-zA-Z]+)")
            fun parse(input: String): Rule {
                val (type, op, qty, next) = rxRule.matchEntire(input)!!.destructured
                return Rule(type, op[0], qty.toInt(), next)
            }
        }
    }

    data class Part(val elements: Map<String, Int>): Map<String, Int> by elements {
        constructor( input: String) : this(
            Part(input.split( Regex("[{}, ]+") ).filter { it.isNotEmpty() }.associate { m ->
                val (type, qty) = Regex("([a-z]+)=([0-9]+)").matchEntire(m)!!.destructured
                type to qty.toInt()
            }))

        fun sum() = values.sum()
        fun getQty(type: String): Int = get(type)!!
    }

    private fun solveProblem2(input: String): Long {
        val (workflows, _) = parse(input)
        val part = mutableMapOf(
            "a" to mutableListOf(1..4000),
            "m" to mutableListOf(1..4000),
            "s" to mutableListOf(1..4000),
            "x" to mutableListOf(1..4000),
        )
        val res = workflows.solve2("in", part).sumOf { c ->
            c.values.map { typeList -> typeList.sumOf { r -> (r.last - r.first + 1).toLong() } }
                .reduce { a, b -> a * b }
        }
        return res
    }

    private fun parse(input: String): Pair<Workflows, List<Part>> {
        val (workflows0, parts0) = input.split("\n\n")
        val workflows = Workflows( workflows0)
        workflows.forEach { w -> log { "$w" } }

        val parts = parts0.split('\n').map { Part(it) }
        parts.forEach { log { "part $it" } }

        return Pair(workflows, parts)
    }

    private fun applyRule(r: Rule, part: Map<String, List<IntRange>>, positive: Boolean = true ): Map<String, List<IntRange>> {
        val part2 = part.entries.associate { e -> e.key to e.value.map { it }.toMutableList() }.toMutableMap()
        val l = part2[r.type]!!
        val i = l.indexOfFirst { r.qty in it }
        if (i >= 0) {
            l[i] = if (r.op == '>') if (positive) (r.qty + 1..l[i].last) else (l[i].first..r.qty)
                   else if (positive) (l[i].first..<r.qty) else (r.qty..l[i].last)
        }
        return part2
    }

    private fun log(s: () -> String) {
        if (debug) println(s())
    }
}



