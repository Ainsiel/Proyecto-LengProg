package com.mahiiru.sudokuapp.feature_sudoku.presentation

data class SudokuPuzzleState(
    val puzzle: (HashMap<Int, SudokuTile>) = HashMap(),
    val isLoading: Boolean = false,
    val isPuzzleCompleted: Boolean = false,
    val boundary: Int = 9,
)
