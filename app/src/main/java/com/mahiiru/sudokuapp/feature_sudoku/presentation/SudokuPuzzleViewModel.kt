package com.mahiiru.sudokuapp.feature_sudoku.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahiiru.sudokuapp.core.util.Resource
import com.mahiiru.sudokuapp.feature_sudoku.domain.model.SudokuPuzzle
import com.mahiiru.sudokuapp.feature_sudoku.domain.repository.ISudokuRepository
import com.mahiiru.sudokuapp.feature_sudoku.domain.use_case.BuildPuzzleUseCase
import com.mahiiru.sudokuapp.feature_sudoku.domain.use_case.IsPuzzleCompletedUseCase
import com.mahiiru.sudokuapp.feature_sudoku.domain.use_case.SolveSudokuUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SudokuPuzzleViewModel @Inject constructor(
    private val repository: ISudokuRepository
): ViewModel() {

    var state by mutableStateOf(SudokuPuzzleState(), neverEqualPolicy())

    init {
        getSudokuPuzzle()
    }

    fun onEvent(event: SudokuPuzzleEvent){
        when (event){
            is SudokuPuzzleEvent.OnInput -> TODO()
            is SudokuPuzzleEvent.OnNewSudokuPuzzle -> rebuildNewPuzzle()
            is SudokuPuzzleEvent.OnSolveSudoku -> solvePuzzle()
            is SudokuPuzzleEvent.OnSudokuCompleted -> validatePuzzle()
            is SudokuPuzzleEvent.OnTileFocused -> TODO()
        }
    }

    private fun validatePuzzle() {
        viewModelScope.launch {
            repository.getCurrentGame().collect{ result ->
                when (result) {
                    is Resource.Error -> buildNewPuzzle()
                    is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    is Resource.Success -> {
                        state = state
                            .copy(isPuzzleCompleted = IsPuzzleCompletedUseCase().invoke(result.data!!))
                    }
                }
            }
        }
    }
    private fun solvePuzzle() {
        viewModelScope.launch {
            repository.getCurrentGame().collect{ result ->
                when (result) {
                    is Resource.Error -> buildNewPuzzle()
                    is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    is Resource.Success -> solveSudokuPuzzle(result.data!!)
                }
            }
        }
    }

    private suspend fun solveSudokuPuzzle(data: SudokuPuzzle) {
        SolveSudokuUseCase().invoke(data)
        repository.updateGame(data).collect{ result ->
            when (result) {
                is Resource.Error -> buildNewPuzzle()
                is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                is Resource.Success -> state = state.copy(puzzle = getPuzzleFromData(result.data!!))
            }
        }
    }

    private fun rebuildNewPuzzle() {
        viewModelScope.launch {
            repository.getCurrentGame().collect{ result ->
                when (result) {
                    is Resource.Error -> buildNewPuzzle()
                    is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    is Resource.Success -> buildNewPuzzle()
                }
            }
        }
    }

    private fun getSudokuPuzzle() {
        viewModelScope.launch {
            repository.getCurrentGame().collect { result ->
                when (result) {
                    is Resource.Error -> buildNewPuzzle()
                    is Resource.Loading -> state = state.copy(isLoading = result.isLoading)

                    is Resource.Success -> state = state.copy(puzzle = getPuzzleFromData(result.data!!))

                }
            }
        }
    }

    private suspend fun buildNewPuzzle() {
        val newPuzzle = SudokuPuzzle(state.boundary)
        BuildPuzzleUseCase().invoke(newPuzzle)
        repository.updateGame(newPuzzle).collect{ result ->
            when (result) {
                is Resource.Error -> Unit
                is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                is Resource.Success -> state = state.copy(puzzle = getPuzzleFromData(result.data!!))

            }
        }
    }

    private fun getPuzzleFromData(data: SudokuPuzzle): HashMap<Int, SudokuTile> {
        val newHash: HashMap<Int, SudokuTile> = HashMap()
        data.graph.forEach {
            val node = it.value[0]
            newHash[it.key] = SudokuTile(
                node.x,
                node.y,
                node.color,
                false,
                node.readOnly
            )
        }
        return newHash
    }


}