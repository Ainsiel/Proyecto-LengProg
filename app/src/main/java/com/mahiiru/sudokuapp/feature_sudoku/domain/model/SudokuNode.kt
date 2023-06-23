package com.mahiiru.sudokuapp.feature_sudoku.domain.model

import java.io.Serializable

data class SudokuNode(
    val x: Int,
    val y: Int,
    var color: Int,
    var readOnly: Boolean = true
) : Serializable {
    override fun hashCode(): Int {
        return getHash(x,y)
    }
}

internal fun getHash(x: Int, y: Int): Int {
    val newX = x*100
    return "$newX$y".toInt()
}
