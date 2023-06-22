package com.mahiiru.sudokuapp.feature_sudoku.domain.use_case

import com.mahiiru.sudokuapp.feature_sudoku.domain.model.SudokuPuzzle

class IsPuzzleCompletedUseCase {

    operator fun invoke(puzzle: SudokuPuzzle) : Boolean {
        (1..puzzle.boundary).forEach { xIndex ->
            (1..puzzle.boundary).forEach { yIndex ->
                val node = puzzle.getNode(xIndex,yIndex)
                if(node.color == 0 || !IsValueValidUseCase().invoke(puzzle,node)) return false
            }
        }
        return true
    }
}