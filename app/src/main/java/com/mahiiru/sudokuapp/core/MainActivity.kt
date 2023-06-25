package com.mahiiru.sudokuapp.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.mahiiru.sudokuapp.core.ui.theme.SudokuAppTheme
import com.mahiiru.sudokuapp.feature_sudoku.presentation.SudokuPuzzleScreen
import com.mahiiru.sudokuapp.feature_sudoku.presentation.SudokuPuzzleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: SudokuPuzzleViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SudokuAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SudokuPuzzleScreen(viewModel = viewModel)
                }
            }
        }
    }
}