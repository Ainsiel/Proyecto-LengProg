package com.mahiiru.sudokuapp.feature_sudoku.presentation

data class SudokuTile(
    val x: Int,
    val y: Int,
    var value: Int,
    var hasFocus: Boolean,
    val readOnly: Boolean
)
