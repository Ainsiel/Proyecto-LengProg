package com.mahiiru.sudokuapp.feature_sudoku.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mahiiru.sudokuapp.feature_sudoku.domain.repository.ISudokuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SudokuPuzzleViewModel @Inject constructor(
    private val repository: ISudokuRepository
): ViewModel() {

    var state by mutableStateOf(SudokuPuzzleState())

    init {
        getSudokuPuzzle()
    }

    private fun getSudokuPuzzle() {
        TODO("Not yet implemented")
    }
}