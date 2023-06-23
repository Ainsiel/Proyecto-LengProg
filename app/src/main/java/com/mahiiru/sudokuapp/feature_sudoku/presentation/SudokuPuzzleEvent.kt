package com.mahiiru.sudokuapp.feature_sudoku.presentation

sealed class SudokuPuzzleEvent {
    data class OnInput(val input: Int) : SudokuPuzzleEvent()
    data class OnTileFocused(val x: Int, val y: Int) : SudokuPuzzleEvent()
    object OnNewSudokuPuzzle : SudokuPuzzleEvent()
    object OnSolveSudoku : SudokuPuzzleEvent()
    object OnSudokuCompleted : SudokuPuzzleEvent()
}
