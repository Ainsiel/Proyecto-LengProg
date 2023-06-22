package com.mahiiru.sudokuapp.feature_sudoku.domain.use_case

import com.mahiiru.sudokuapp.feature_sudoku.domain.model.SudokuNode
import com.mahiiru.sudokuapp.feature_sudoku.domain.model.SudokuPuzzle
import kotlin.math.sqrt

class IsValueValidUseCase {

    operator fun invoke(puzzle: SudokuPuzzle, node: SudokuNode) : Boolean {
        return !isValueInRow(puzzle,node) &&
                !isValueInColumn(puzzle,node) &&
                !isValueInBox(puzzle,node)
    }

    private fun isValueInRow(puzzle: SudokuPuzzle, node: SudokuNode): Boolean {
        val value = node.color

        (1..puzzle.boundary).forEach {xIndex ->
            val otherNode = puzzle.getNode(xIndex,node.y)
            if(otherNode.color == value && otherNode != node) return true
        }
        return false
    }

    private fun isValueInColumn(puzzle: SudokuPuzzle, node: SudokuNode): Boolean {
        val value = node.color

        (1..puzzle.boundary).forEach {yIndex ->
            val otherNode = puzzle.getNode(node.x,yIndex)
            if(otherNode.color == value && otherNode != node) return true
        }
        return false
    }

    private fun isValueInBox(puzzle: SudokuPuzzle, node: SudokuNode): Boolean {
        val value = node.color
        val gridSize = sqrt(puzzle.boundary.toDouble()).toInt()
        val startX = (((node.x - 1) / gridSize) * gridSize) + 1
        val startY = (((node.y - 1) / gridSize) * gridSize) + 1

        (startX until startX + gridSize).forEach { xIndex ->
            (startY until startY + gridSize).forEach { yIndex ->
                val otherNode = puzzle.getNode(xIndex,yIndex)
                if(otherNode.color == value && otherNode != node) return true
            }
        }
        return false
    }
}