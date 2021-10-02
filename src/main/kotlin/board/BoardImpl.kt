package board

import board.Direction.*
import java.lang.IllegalArgumentException

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImpl(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl(width)

open class SquareBoardImpl(size: Int): SquareBoard {

    private val board =
        Array(size) { i -> Array(size) { j -> Cell(i + 1, j + 1) } }

    override val width: Int
        get() = board.size

    override fun getCellOrNull(i: Int, j: Int): Cell? {
        if (i > width || j > width || i <= 0 || j <= 0) {
            return null
        }
        return board[i - 1][j - 1]
    }

    override fun getCell(i: Int, j: Int): Cell {
        if (i > width || j > width || i <= 0 || j <= 0) {
            throw IllegalArgumentException("Incorrect value(s) for row and/or column!")
        }
        return board[i - 1][j - 1]
    }

    override fun getAllCells(): Collection<Cell> {
        val cellList = ArrayList<Cell>()
        for (i in 0 until width) {
            for (j in 0 until width) {
                cellList.add(board[i][j])
            }
        }
        return cellList
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        val cellList = ArrayList<Cell>()
        for (j in jRange) {
            if (j <= width) cellList.add(board[i - 1][j - 1])
            else break
        }
        return cellList
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        val cellList = ArrayList<Cell>()
        for (i in iRange) {
            if (i <= width) cellList.add(board[i - 1][j - 1])
            else break
        }
        return cellList
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        val x = this.i
        val y = this.j
        return when (direction) {
            UP -> getCellOrNull(x - 1, y)
            DOWN -> getCellOrNull(x + 1, y)
            RIGHT -> getCellOrNull(x, y + 1)
            LEFT -> getCellOrNull(x, y - 1)
        }
    }
}

class GameBoardImpl<T>(size: Int) : SquareBoardImpl(size), GameBoard<T> {

    private val storedValues = mutableMapOf<Cell, T?>()

    init {
        getAllCells().forEach { storedValues[it] = null }
    }

    override fun get(cell: Cell): T? {
        return storedValues[cell]
    }

    override fun set(cell: Cell, value: T?) {
        storedValues[cell] = value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> {
        return storedValues.filterValues(predicate).keys
    }

    override fun find(predicate: (T?) -> Boolean): Cell? {
        return storedValues.filterValues(predicate).keys.firstOrNull()
    }

    override fun any(predicate: (T?) -> Boolean): Boolean {
        return storedValues.map { find(predicate) != null }.first()
    }

    override fun all(predicate: (T?) -> Boolean): Boolean {
        return storedValues.filterValues(predicate).size == storedValues.size
    }
}