package com.mahiiru.sudokuapp.feature_sudoku.domain.use_case


import com.mahiiru.sudokuapp.feature_sudoku.domain.model.SudokuNode
import com.mahiiru.sudokuapp.feature_sudoku.domain.model.SudokuPuzzle
import com.mahiiru.sudokuapp.feature_sudoku.domain.model.getHash
import java.util.LinkedList
import kotlin.math.sqrt
import kotlin.random.Random

class SolveSudokuUseCase {

    operator fun invoke(puzzle: SudokuPuzzle) {
        puzzle.solve()
    }

    private fun SudokuPuzzle.solve(): SudokuPuzzle {
        val assignments = LinkedList<SudokuNode>()
        var assignmentAttempts = 0
        var partialBacktrack = false
        var fullbacktrackCounter = 0
        var niceValue: Int = (boundary / 2)
        var niceCounter = 0
        var newGraph = LinkedHashMap(this.graph)
        val uncoloredNodes = LinkedList<SudokuNode>()
        newGraph.values.filter { it.first.color == 0 }.forEach { uncoloredNodes.add(it.first) }

        while (uncoloredNodes.size > 0) {
            if (assignmentAttempts > boundary * boundary && partialBacktrack) {
                assignments.forEach { node ->
                    node.color = 0
                    uncoloredNodes.add(node)
                }

                assignments.clear()

                assignmentAttempts = 0
                partialBacktrack = false
                fullbacktrackCounter++
            } else if (assignmentAttempts > boundary * boundary * boundary) {
                partialBacktrack = true
                assignments.takeLast(assignments.size / 2)
                    .forEach { node ->
                        node.color = 0
                        uncoloredNodes.add(node)
                        assignments.remove(node)
                    }

                assignmentAttempts = 0
            }
            if (fullbacktrackCounter == boundary * boundary) {

                newGraph = this.seedColors().graph
                uncoloredNodes.clear()
                newGraph.values.filter { it.first.color == 0 }.forEach { uncoloredNodes.add(it.first) }
                assignments.clear()
                fullbacktrackCounter = 0
                niceValue = (boundary / 2)
            }

            val node = uncoloredNodes[Random.nextInt(0, uncoloredNodes.size)]

            val options = getPossibleValues(newGraph[getHash(node.x, node.y)]!!, boundary)

            if (options.size == 0) assignmentAttempts++
            else if (options.size > niceValue) {
                niceCounter++
                if (niceCounter > boundary * boundary) {
                    niceValue++
                    niceCounter = 0
                }
            } else {
                val color = options[Random.nextInt(0, options.size)]
                node.color = color
                uncoloredNodes.remove(node)
                assignments.add(node)
                if (niceValue > 1) niceValue--
            }
        }

        this.graph.clear()
        this.graph.putAll(newGraph)
        return this
    }

    private fun SudokuPuzzle.seedColors(): SudokuPuzzle {
        val boundary = sqrt(this.boundary.toDouble()).toInt()

        val allocatedNumbers = mutableListOf<Int>()
        var allocations = 0
        var byRow = true
        var ttb = true

        var loopCounter = 0
        while (loopCounter < boundary * 1000) {
            val rowOrColumnProgression = mutableListOf<Int>()
            if (ttb) (1..boundary).forEach { rowOrColumnProgression.add(it) }
            else (boundary downTo 1).forEach { rowOrColumnProgression.add(it) }

            rowOrColumnProgression.forEach { rowOrColumnIndex ->
                var newInt = Random.nextInt(1, boundary + 1)
                var notNew = true
                while (notNew) {
                    if (!allocatedNumbers.contains(newInt)) notNew = false
                    else if (allocatedNumbers.size == boundary) notNew = false
                    else newInt = Random.nextInt(1, boundary + 1)
                }

                allocatedNumbers.add(newInt)
                ttb = Random.nextBoolean()
                (1..boundary).forEach { subgridOffset ->
                    val fixedCoordinate = boundary * rowOrColumnIndex - boundary
                    val variantLowerBound = boundary * subgridOffset - boundary + 1
                    val variantUpperBound = variantLowerBound + boundary
                    val hashList = mutableListOf<Int>()

                    if (byRow) {
                        (variantLowerBound until variantUpperBound).forEach { variantCoordinate ->
                            hashList.add(getHash(variantCoordinate, fixedCoordinate + subgridOffset))
                        }
                    } else {
                        (variantLowerBound until variantUpperBound).forEach { variantCoordinate ->
                            hashList.add(getHash(fixedCoordinate + subgridOffset, variantCoordinate))
                        }
                    }

                    hashList.firstOrNull { this.graph[it]?.first?.color == 0 }.let {
                        if (it != null) {
                            this.graph[it]!!.first.color = newInt
                            allocations++
                        }
                    }

                    if (boundary == 4 || allocatedNumbers.size == boundary - 1) return this
                    else if (allocatedNumbers.size == boundary) {
                        return this
                    }
                }
            }
            byRow = !byRow
            loopCounter++
        }
        return this
    }

    private fun getPossibleValues(adjList: LinkedList<SudokuNode>, boundary: Int): List<Int> {
        val options = mutableListOf<Int>()
        (1..boundary).forEach {
            adjList.first.color = it

            val occurrences = adjList.count { node ->
                node.color == it
            }

            if (occurrences == 1) options.add(it)
        }
        adjList.first.color = 0
        return options
    }

}