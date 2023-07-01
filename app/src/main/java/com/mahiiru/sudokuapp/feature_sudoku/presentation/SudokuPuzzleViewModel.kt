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
            is SudokuPuzzleEvent.OnInput -> onInputTile(event.input)
            is SudokuPuzzleEvent.OnNewSudokuPuzzle -> rebuildNewPuzzle()
            is SudokuPuzzleEvent.OnSolveSudoku -> solvePuzzle()
            is SudokuPuzzleEvent.OnSudokuCompleted -> validatePuzzle()
            is SudokuPuzzleEvent.OnTileFocused -> updateFocusState(event.x,event.y)
        }
    }

    private fun onInputTile(input: Int) {
        var focusedTile : SudokuTile? = null
        state.puzzle.forEach {
            if (it.value.hasFocus) focusedTile = it.value.copy()
        }
        if (focusedTile != null) updateNodeData(focusedTile!!,input)
    }

    private fun updateNodeData(focusedTile: SudokuTile, input: Int) {
        viewModelScope.launch {
            repository.updateNode(
                focusedTile.x,
                focusedTile.y,
                input
            ).collect {
                result ->
                when (result) {
                    is Resource.Error -> Unit
                    is Resource.Loading -> state = state.copy(isLoading = result.isLoading)
                    is Resource.Success -> state = state.copy(puzzle = getPuzzleFromData(result.data!!))
                }
            }
        }
    }

    private fun updateFocusState(x: Int, y: Int) {
        val puzzle = state.copy().puzzle
        puzzle.forEach { (key,value) ->
            value.hasFocus = value.x == x && value.y == y
        }
        state = state.copy(puzzle = puzzle)
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