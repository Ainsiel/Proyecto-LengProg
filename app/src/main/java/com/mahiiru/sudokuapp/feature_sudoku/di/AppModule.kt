package com.mahiiru.sudokuapp.feature_sudoku.di

import android.content.Context
import com.mahiiru.sudokuapp.feature_sudoku.data.persistence.SudokuRepositoryImpl
import com.mahiiru.sudokuapp.feature_sudoku.domain.repository.ISudokuRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideISudokuRepository(@ApplicationContext context: Context): ISudokuRepository {
        return SudokuRepositoryImpl(context)
    }
}