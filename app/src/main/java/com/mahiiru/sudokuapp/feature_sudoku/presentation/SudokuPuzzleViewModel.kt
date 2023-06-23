package com.mahiiru.sudokuapp.feature_sudoku.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mahiiru.sudokuapp.feature_sudoku.domain.repository.ISudokuRepository


//TODO Dagger Hilt
class SudokuPuzzleViewModel(
    private val repository: ISudokuRepository
) {

    var state by mutableStateOf(SudokuPuzzleState())

    init {
        getSudokuPuzzle()
    }

    private fun getSudokuPuzzle() {
        TODO("Not yet implemented")
    }
}