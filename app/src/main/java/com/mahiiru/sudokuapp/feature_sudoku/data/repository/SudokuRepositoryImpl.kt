package com.mahiiru.sudokuapp.feature_sudoku.data.repository

import com.mahiiru.sudokuapp.core.util.Resource
import com.mahiiru.sudokuapp.feature_sudoku.domain.model.SudokuPuzzle
import com.mahiiru.sudokuapp.feature_sudoku.domain.repository.ISudokuRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

private const val FILE_NAME = "game_state.txt"

class SudokuRepositoryImpl(
    fileStorageDirectory: String,
    private val pathToStorageFile: File = File(fileStorageDirectory, FILE_NAME)
) : ISudokuRepository {
    override suspend fun updateGame(game: SudokuPuzzle): Flow<Resource<SudokuPuzzle>> = flow {
        try {
            updateGameData(game)
            emit(Resource.Success(game))
        } catch (e: Exception) {
            emit(Resource.Error("Couldn't update the game."))
        }
    }

    private fun updateGameData(game: SudokuPuzzle) {
        try {
            val fileOutputStream = FileOutputStream(pathToStorageFile)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(game)
            objectOutputStream.close()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateNode(x: Int, y: Int, color: Int): Flow<Resource<SudokuPuzzle>> = flow {
        try {
            val game = getGame()
            game.getNode(x,y).color = color
            updateGame(game)
            emit(Resource.Success(game))
        } catch (e: Exception) {
            emit(Resource.Error("Couldn't update the node."))
        }
    }

    override suspend fun getCurrentGame(): Flow<Resource<SudokuPuzzle>> = flow {
        emit(Resource.Loading(true))
        try {
            val game = getGame()
            emit(Resource.Success(game))
        } catch (e: Exception) {
            emit(Resource.Error("Couldn't get the current game."))
        }
    }

    private fun getGame(): SudokuPuzzle {
        try {
            var game: SudokuPuzzle

            val fileInputStream = FileInputStream(pathToStorageFile)
            val objectInputStream = ObjectInputStream(fileInputStream)
            game = objectInputStream.readObject() as SudokuPuzzle
            objectInputStream.close()

            return (game)
        } catch (e: FileNotFoundException) {
            throw e
        }
    }

}