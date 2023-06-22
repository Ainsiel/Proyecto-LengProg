package com.mahiiru.sudokuapp.feature_sudoku.domain.use_case

import com.mahiiru.sudokuapp.feature_sudoku.domain.model.SudokuPuzzle


class BuildPuzzleUseCase {

    operator fun invoke(puzzle: SudokuPuzzle) {
        val size = puzzle.boundary
        val numClues = size * size / 3
        var count = 0

        while (count < numClues){
            val x = (1..size).random()
            val y = (1..size).random()
            val node = puzzle.getNode(x,y)

            if(node.color == 0 ) {
                val value = (1..size).random()
                node.color = value

                if (IsValueValidUseCase().invoke(puzzle,node)){
                    count++
                } else {
                    node.color = 0
                }
            }
        }
    }
}