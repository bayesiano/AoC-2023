import Day19.Workflow.Companion.rxWorkflow

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


    private fun solveProblem1(input: String): Int {
        val (workflows0, parts) = parse(input)

        val res = parts.sumOf { part ->
            part.applyWorkflows(workflows0)?.sum() ?: 0
        }
        return res
    }


    data class Workflow(val name: String, val rules: List<Rule>, val default: String) {
        fun processPart(p: Part): String {
            rules.forEach { r ->
                if (r.op == '<' && p.getQty(r.type) < r.qty || r.op == '>' && p.getQty(r.type) > r.qty) return r.next
            }
            return default
        }

        companion object {
            val rxWorkflow = Regex("[{}, ]+")
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

    data class Part(val elements: Map<String, Int>) {
        fun sum() = elements.values.sum()
        fun getQty(type: String): Int = elements[type]!!
        fun applyWorkflows(workflows0: Map<String, Workflow>, wf: String = "in"): Part? =
            when (wf) {
                "A" -> this
                "R" -> null
                else -> applyWorkflows(workflows0, workflows0[wf]!!.processPart(this))
            }

        companion object {
            private val rxParts = Regex("[{}, ]+")
            private val rxPart = Regex("([a-z]+)=([0-9]+)")
            fun parse(input: String) =
                Part(input.split(rxParts).filter { it.isNotEmpty() }.associate { m ->
                    val (type, qty) = rxPart.matchEntire(m)!!.destructured
                    type to qty.toInt()
                })
        }
    }

    private fun solveProblem2(input: String): Long {
        val (workflows, _) = parse(input)
        val part = mutableMapOf(
            "a" to mutableListOf(1..4000),
            "m" to mutableListOf(1..4000),
            "s" to mutableListOf(1..4000),
            "x" to mutableListOf(1..4000),
        )
        val res = solve2("in", workflows, part).sumOf { c ->
            c.values.map { typeList -> typeList.sumOf { r -> (r.last - r.first + 1).toLong() } }
                .reduce { a, b -> a * b }
        }
        return res
    }

    private fun parse(input: String): Pair<Map<String, Workflow>, List<Part>> {
        val (workflows0, parts0) = input.split("\n\n")
        val workflows = workflows0.split('\n').associate { wf0 ->
            wf0.split(rxWorkflow).filter { it.isNotEmpty() }.let { l ->
                val name = l[0]
                val rules = l.drop(1).dropLast(1).map { Rule.parse(it) }
                val default = l.last()
                name to Workflow(name, rules, default)
            }
        }
        workflows.forEach { w -> log { "$w" } }

        val parts = parts0.split('\n').map { Part.parse(it) }
        parts.forEach { log { "part $it" } }

        return Pair(workflows, parts)
    }

    private fun solve2(
        wf: String,
        workflows: Map<String, Workflow>,
        part: Map<String, List<IntRange>>
    ): List<Map<String, List<IntRange>>> {
        val res = mutableListOf<Map<String, List<IntRange>>>()

        var part2 = part
        workflows[wf]!!.rules.forEach { r ->
            when (r.next) {
                "R" -> {}
                "A" -> res += applyRule(r, part2)
                else -> res += solve2(r.next, workflows, applyRule(r, part2))
            }
            part2 = applyRule(r, part2, false)
        }
        if (workflows[wf]!!.default == "A") res += part2
        else if (workflows[wf]!!.default != "R") res += solve2(workflows[wf]!!.default, workflows, part2)

        res.forEach { lr -> log { "  - $lr" } }
        return res
    }

    private fun applyRule(
        r: Rule,
        part: Map<String, List<IntRange>>,
        positive: Boolean = true
    ): Map<String, List<IntRange>> {
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



