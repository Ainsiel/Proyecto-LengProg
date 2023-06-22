package com.mahiiru.sudokuapp.feature_sudoku.domain.model

import com.mahiiru.sudokuapp.feature_sudoku.domain.use_case.BuildSudokuNodesUseCase
import java.util.LinkedList

data class SudokuPuzzle(
    val boundary : Int,
    val graph : LinkedHashMap<Int, LinkedList<SudokuNode>>
    = BuildSudokuNodesUseCase().invoke(boundary)
) {
    fun getNode(x : Int, y : Int) : SudokuNode {
        return graph[getHash(x,y)]!!.find { it.x == x && it.y == y }!!
    }
}
