package com.mahiiru.sudokuapp.feature_sudoku.presentation


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun SudokuPuzzleScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    viewModel: SudokuPuzzleViewModel
) {
    Column(modifier = modifier){
        Spacer(modifier = Modifier.height(100.dp))
        Column(
            modifier = modifier.weight(0.5f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
            SudokuBoard(onEventHandler = viewModel::onEvent, viewModel = viewModel)
        }
    }
}

@Composable
fun SudokuBoard(
    onEventHandler: (SudokuPuzzleEvent) -> Unit,
    viewModel: SudokuPuzzleViewModel
) {
    val boundary = viewModel.state.boundary
    val boardSate = viewModel.state.puzzle

    Column(Modifier.fillMaxSize()) {
        (1..boundary).forEach {yIndex ->
            Row(Modifier.weight(1f)) {
                (1..boundary).forEach { xIndex ->
                    val x = xIndex*100
                    val tileKey = "$x$yIndex".toInt()
                    SudokuTextField(
                        modifier = Modifier.weight(1f),
                        onEventHandler = onEventHandler,
                        tile = boardSate[tileKey]!!)
                }
            }
        }
    }
}

@Composable
fun SudokuTextField(
    modifier: Modifier,
    onEventHandler: (SudokuPuzzleEvent) -> Unit,
    tile: SudokuTile
) {
    Text(text = tile.value.toString(), color = Color.Red)
}
