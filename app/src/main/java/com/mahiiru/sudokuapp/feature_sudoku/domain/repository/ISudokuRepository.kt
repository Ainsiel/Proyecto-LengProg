package com.mahiiru.sudokuapp.feature_sudoku.domain.repository

import com.mahiiru.sudokuapp.core.util.Resource
import com.mahiiru.sudokuapp.feature_sudoku.domain.model.SudokuNode
import com.mahiiru.sudokuapp.feature_sudoku.domain.model.SudokuPuzzle
import kotlinx.coroutines.flow.Flow

interface ISudokuRepository {

    suspend fun updateGame(game: SudokuPuzzle) : Flow<Resource<SudokuPuzzle>>
    suspend fun updateNode(x: Int, y: Int, color: Int) : Flow<Resource<SudokuNode>>
    suspend fun getCurrentGame() : Flow<Resource<SudokuPuzzle>>
}