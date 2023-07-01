package com.mahiiru.sudokuapp.feature_sudoku.domain.use_case

import com.mahiiru.sudokuapp.feature_sudoku.domain.model.SudokuNode
import com.mahiiru.sudokuapp.feature_sudoku.domain.model.SudokuPuzzle
import com.mahiiru.sudokuapp.feature_sudoku.domain.model.getHash
import java.util.LinkedList
import kotlin.math.sqrt
import kotlin.random.Random


class BuildPuzzleUseCase {

    operator fun invoke(puzzle: SudokuPuzzle) {
        puzzle.buildEdges()
        puzzle.seedColors()
        puzzle.solve()
        puzzle.unsolve()
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


    private fun SudokuPuzzle.buildEdges(): SudokuPuzzle {
        this.graph.forEach {
            val x = it.value.first.x
            val y = it.value.first.y

            it.value.mergeWithoutRepeats(
                getNodesByColumn(this.graph, x)
            )

            it.value.mergeWithoutRepeats(
                getNodesByRow(this.graph, y)
            )

            it.value.mergeWithoutRepeats(
                getNodesBySubgrid(this.graph, x, y, boundary)
            )

        }
        return this
    }
     fun SudokuPuzzle.solve(): SudokuPuzzle {
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

    internal fun SudokuPuzzle.unsolve(): SudokuPuzzle {
        val difficulty = 0.50
        var remove = ((boundary * boundary) - (boundary * boundary * difficulty)).toInt()
        val allocations = mutableListOf<SudokuNode>()


        var counter = 0

        while (counter <= remove) {
            var colored = true
            while (colored) {
                val randX = Random.nextInt(1, boundary + 1)
                val randY = Random.nextInt(1, boundary + 1)

                val node = this.graph[getHash(randX, randY)]!!.first



                if (node.color != 0) {
                    allocations.add(
                        SudokuNode(
                            node.x,
                            node.y,
                            node.color,
                            node.readOnly
                        )
                    )

                    node.color = 0
                    colored = false
                    counter++
                }
            }
        }
        return this.apply {
                allocations.forEach { node ->
                    this.graph[node.hashCode()]!!.first.color = 0
                    this.graph[node.hashCode()]!!.first.readOnly = false
                }
            }
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

    private fun LinkedList<SudokuNode>.mergeWithoutRepeats(new: List<SudokuNode>) {
        val hashes: MutableList<Int> = this.map { it.hashCode() }.toMutableList()
        new.forEach {
            if (!hashes.contains(it.hashCode())) {
                this.add(it)
                hashes.add(it.hashCode())
            }
        }
    }

     private fun getNodesByColumn(graph: LinkedHashMap<Int,
             LinkedList<SudokuNode>>, x: Int): List<SudokuNode> {
        val edgeList = mutableListOf<SudokuNode>()
        graph.values.filter {
            it.first.x == x
        }.forEach {
            edgeList.add(it.first)
        }
        return edgeList
    }

     private fun getNodesByRow(graph: LinkedHashMap<Int,
            LinkedList<SudokuNode>>, y: Int): List<SudokuNode> {
        val edgeList = mutableListOf<SudokuNode>()
        graph.values.filter { it.first.y == y }.forEach { edgeList.add(it.first) }
        return edgeList
    }

     private fun getNodesBySubgrid(graph: LinkedHashMap<Int,
            LinkedList<SudokuNode>>, x: Int, y: Int, boundary: Int): List<SudokuNode> {
        val edgeList = mutableListOf<SudokuNode>()
        val iMaxX = getIntervalMax(boundary, x)
        val iMaxY = getIntervalMax(boundary, y)

        ((iMaxX - sqrt(boundary.toDouble()).toInt()) + 1..iMaxX).forEach { xIndex ->
            ((iMaxY - sqrt(boundary.toDouble()).toInt()) + 1..iMaxY).forEach { yIndex ->
                edgeList.add(
                    graph[getHash(xIndex, yIndex)]!!.first
                )
            }
        }
        return edgeList
    }

     private fun getIntervalMax(boundary: Int, target: Int): Int {
        var intervalMax = 0
        val interval = sqrt(boundary.toDouble()).toInt()

        (1..interval).forEach { index ->
            if (interval * index >= target && target > (interval * index - interval)) {
                intervalMax = index * interval
                return@forEach
            }
        }
        return intervalMax
    }
}