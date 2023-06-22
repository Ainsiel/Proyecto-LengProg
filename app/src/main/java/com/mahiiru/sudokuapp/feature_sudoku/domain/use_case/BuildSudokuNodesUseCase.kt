package com.mahiiru.sudokuapp.feature_sudoku.domain.use_case

import com.mahiiru.sudokuapp.feature_sudoku.domain.model.SudokuNode
import java.util.LinkedList

class BuildSudokuNodesUseCase {

    operator fun invoke(n : Int) : LinkedHashMap<Int, LinkedList<SudokuNode>> {
        val newMap = LinkedHashMap<Int, LinkedList<SudokuNode>>()

        (1..n).forEach { xIndex ->
            (1..n).forEach { yIndex ->
                val newNode = SudokuNode(
                    xIndex,
                    yIndex,
                    0
                )

                val newList = LinkedList<SudokuNode>()
                newList.add(newNode)
                newMap.put(
                    newNode.hashCode(),
                    newList
                )
            }
        }

        return newMap
    }
}