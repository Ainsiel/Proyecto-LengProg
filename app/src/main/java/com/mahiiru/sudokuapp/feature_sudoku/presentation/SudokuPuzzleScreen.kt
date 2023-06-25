package com.mahiiru.sudokuapp.feature_sudoku.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mahiiru.sudokuapp.core.ui.AppToolbar


@Composable
fun SudokuPuzzleScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    viewModel: SudokuPuzzleViewModel
) {
    Column(modifier = modifier){
        AppToolbar(modifier = Modifier.wrapContentHeight(), title = "Sudoku") {
            NewEventIcon(
                onEventHandler = viewModel::onEvent,
                SudokuPuzzleEvent.OnNewSudokuPuzzle,
                Icons.Filled.Refresh
            )
            NewEventIcon(
                onEventHandler = viewModel::onEvent,
                SudokuPuzzleEvent.OnSudokuCompleted,
                Icons.Filled.Done
            )
            NewEventIcon(
                onEventHandler = viewModel::onEvent,
                SudokuPuzzleEvent.OnSolveSudoku,
                Icons.Filled.CheckCircle
            )
        }
        Spacer(modifier = Modifier.height(50.dp))
        Column(
            modifier = Modifier
                .height(450.dp)
                .fillMaxWidth()
                .padding(horizontal = 25.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            SudokuBoard(onEventHandler = viewModel::onEvent, viewModel = viewModel)
        }
    }
}

@Composable
fun NewEventIcon(
    onEventHandler: (SudokuPuzzleEvent) -> Unit,
    event: SudokuPuzzleEvent,
    imageVector: ImageVector
) {
    Icon(

        imageVector = imageVector,
        tint = MaterialTheme.colorScheme.secondary,
        contentDescription = null,
        modifier = Modifier
            .clickable(onClick = {
                onEventHandler.invoke(
                    event
                )
            }
            )
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .height(36.dp),
    )
}

@Composable
fun SudokuBoard(
    onEventHandler: (SudokuPuzzleEvent) -> Unit,
    viewModel: SudokuPuzzleViewModel
) {
    val boundary = viewModel.state.boundary
    val boardSate = viewModel.state.puzzle

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(5.dp)
    ) {
        (1..boundary).forEach {yIndex ->
            Row(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
            ) {
                (1..boundary).forEach { xIndex ->
                    val x = xIndex*100
                    val tileKey = "$x$yIndex".toInt()
                    SudokuTextField(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxSize(),
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
    Column(
        modifier = modifier
            .border(width = 1.dp, color = MaterialTheme.colorScheme.inversePrimary)
            .background(color = MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Text(text = tile.value.toString(), color = MaterialTheme.colorScheme.secondary)
    }
}
