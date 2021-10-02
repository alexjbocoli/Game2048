package game

import board.Cell
import board.Direction
import board.GameBoard
import board.createGameBoard

/*
 * Your task is to implement the game 2048 https://en.wikipedia.org/wiki/2048_(video_game).
 * Implement the utility methods below.
 *
 * After implementing it you can try to play the game running 'PlayGame2048'.
 */
fun newGame2048(initializer: Game2048Initializer<Int> = RandomGame2048Initializer): Game =
    Game2048(initializer)

class Game2048(private val initializer: Game2048Initializer<Int>) : Game {
    private val board = createGameBoard<Int?>(4)

    override fun initialize() {
        repeat(2) {
            board.addNewValue(initializer)
        }
    }

    override fun canMove() = board.any { it == null }

    override fun hasWon() = board.any { it == 2048 }

    override fun processMove(direction: Direction) {
        if (board.moveValues(direction)) {
            board.addNewValue(initializer)
        }
    }

    override fun get(i: Int, j: Int): Int? = board.run { get(getCell(i, j)) }
}

/*
 * Add a new value produced by 'initializer' to a specified cell in a board.
 */
fun GameBoard<Int?>.addNewValue(initializer: Game2048Initializer<Int>) {
    val cellValuePair = initializer.nextValue(this)
    if (cellValuePair != null) {
        this[cellValuePair.first] = cellValuePair.second
    }
}

/*
 * Update the values stored in a board,
 * so that the values were "moved" in a specified rowOrColumn only.
 * Use the helper function 'moveAndMergeEqual' (in Game2048Helper.kt).
 * The values should be moved to the beginning of the row (or column),
 * in the same manner as in the function 'moveAndMergeEqual'.
 * Return 'true' if the values were moved and 'false' otherwise.
 */
fun GameBoard<Int?>.moveValuesInRowOrColumn(rowOrColumn: List<Cell>): Boolean {
    val currentValues = ArrayList<Int?>()
    rowOrColumn.forEach { currentValues.add(this[it]) }

    var hasMoved = false
    val updatedValues = currentValues.moveAndMergeEqual { it * 2 }
    if (updatedValues.isNotEmpty()) {
        for (i in updatedValues.indices) {
            if (currentValues[i] != updatedValues[i]) {
                this[rowOrColumn[i]] = updatedValues[i]
                hasMoved = true
            }
        }

        for (i in updatedValues.size until width) this[rowOrColumn[i]] = null
    }
    return hasMoved
}

/*
 * Update the values stored in a board,
 * so that the values were "moved" to the specified direction
 * following the rules of the 2048 game .
 * Use the 'moveValuesInRowOrColumn' function above.
 * Return 'true' if the values were moved and 'false' otherwise.
 */
fun GameBoard<Int?>.moveValues(direction: Direction): Boolean {
    val listOfMovements = ArrayList<List<Cell>>()
    when (direction) {
        Direction.UP -> {
            for (j in 1..width) listOfMovements.add(this.getColumn(1..width, j))
        }
        Direction.DOWN -> {
            for (j in 1..width) listOfMovements.add(this.getColumn(width downTo 1, j))
        }
        Direction.RIGHT -> {
            for (i in 1..width) listOfMovements.add(this.getRow(i, width downTo 1))
        }
        Direction.LEFT -> {
            for (i in 1..width) listOfMovements.add(this.getRow(i, 1..width))
        }
    }

    var hasMoved = false
    listOfMovements.forEach { if (moveValuesInRowOrColumn(it)) hasMoved = true }
    return hasMoved
}