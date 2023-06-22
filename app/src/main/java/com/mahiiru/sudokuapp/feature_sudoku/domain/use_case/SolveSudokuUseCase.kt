package com.mahiiru.sudokuapp.feature_sudoku.domain.use_case

import com.mahiiru.sudokuapp.feature_sudoku.domain.model.SudokuPuzzle

class SolveSudokuUseCase {

    operator fun invoke(puzzle: SudokuPuzzle) : Boolean {
        (1..puzzle.boundary).forEach { xIndex ->
            (1..puzzle.boundary).forEach { yIndex ->
                val node = puzzle.getNode(xIndex,yIndex)
                if(node.color == 0){
                    (1..puzzle.boundary).forEach { value ->
                        node.color = value
                        if (IsValueValidUseCase().invoke(puzzle,node)){
                            if (SolveSudokuUseCase().invoke(puzzle)) {
                                return true
                            }
                        }
                        node.color = 0
                    }
                    return false
                }
            }
        }
        return true
    }
}