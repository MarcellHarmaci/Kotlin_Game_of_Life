package main.kotlin.com.homework

import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class Cell(val x: Int, val y: Int) {
    var isAlive = false
        set(value) {
            when (value) {
                true -> {
                    field = value
                    rect.fill = Color.LIGHTGREEN
                }
                false -> {
                    field = value
                    rect.fill = Color.GREY
                }
            }
        }
    var nextState = false
    var neighbours = mutableListOf<Cell>()
    val rect = Rectangle(Game.CELLWIDTH, Game.CELLHEIGHT)

    init {
        rect.x = x * Game.CELLWIDTH
        rect.y = y * Game.CELLHEIGHT
        rect.stroke = Color.BLACK
        rect.fill = Color.GRAY
    }

    // count live neighbours
    private fun checkNeighbours(): Int {
        var cnt = 0
        for (neighbour in neighbours) {
            if (neighbour.isAlive)
                cnt++
        }
        return cnt
    }

    // get next state at every tick
    fun tick () {
        nextState = when (isAlive) {
            true ->
                when (checkNeighbours()) {
                    2, 3 -> true
                    else -> false
                }
            false ->
                when (checkNeighbours()) {
                    3 -> true
                    else -> false
                }
        }
    }

    fun switchState() {
        isAlive = nextState
    }
}

enum class Direction {
    Up, Right, Down, Left, Middle,
    UpLeft, UpRight, DownRight, DownLeft
}

// get a cells neighbour in a given direction
fun getNeighbour(cell: Cell, cells: List<List<Cell>>, dir: Direction): Cell {
    return when (dir) {
        Direction.Up        -> cells[cell.x    ][cell.y - 1]
        Direction.Right     -> cells[cell.x + 1][cell.y    ]
        Direction.Down      -> cells[cell.x    ][cell.y + 1]
        Direction.Left      -> cells[cell.x - 1][cell.y    ]
        Direction.UpLeft    -> cells[cell.x - 1][cell.y - 1]
        Direction.UpRight   -> cells[cell.x + 1][cell.y - 1]
        Direction.DownRight -> cells[cell.x + 1][cell.y + 1]
        Direction.DownLeft  -> cells[cell.x - 1][cell.y + 1]
        else -> {
            println("Invalid neighbour requested in getNeighbour")
            return cell
        }
    }
}

fun Cell.addNeighbour(cell: Cell, cells: List<List<Cell>>, dir: Direction) {
    cell.neighbours.add(getNeighbour(cell, cells, dir))
}

// set all neighbours of a cell by it's position
fun initNeighbours(cell: Cell, cells: List<List<Cell>>, cellPlacement: Direction) {
    when (cellPlacement) {
        // Corners
        Direction.UpLeft -> {
            cell.addNeighbour(cell, cells, Direction.Right)
            cell.addNeighbour(cell, cells, Direction.DownRight)
            cell.addNeighbour(cell, cells, Direction.Down)
        }
        Direction.UpRight -> {
            cell.addNeighbour(cell, cells, Direction.Left)
            cell.addNeighbour(cell, cells, Direction.DownLeft)
            cell.addNeighbour(cell, cells, Direction.Down)
        }
        Direction.DownLeft -> {
            cell.addNeighbour(cell, cells, Direction.Up)
            cell.addNeighbour(cell, cells, Direction.UpRight)
            cell.addNeighbour(cell, cells, Direction.Right)
        }
        Direction.DownRight -> {
            cell.addNeighbour(cell, cells, Direction.UpLeft)
            cell.addNeighbour(cell, cells, Direction.Up)
            cell.addNeighbour(cell, cells, Direction.Left)
        }

        // Sides
        Direction.Up -> {
            cell.addNeighbour(cell, cells, Direction.Left)
            cell.addNeighbour(cell, cells, Direction.Right)
            cell.addNeighbour(cell, cells, Direction.DownLeft)
            cell.addNeighbour(cell, cells, Direction.Down)
            cell.addNeighbour(cell, cells, Direction.DownRight)
        }
        Direction.Right -> {
            cell.addNeighbour(cell, cells, Direction.UpLeft)
            cell.addNeighbour(cell, cells, Direction.Up)
            cell.addNeighbour(cell, cells, Direction.Left)
            cell.addNeighbour(cell, cells, Direction.DownLeft)
            cell.addNeighbour(cell, cells, Direction.Down)
        }
        Direction.Down -> {
            cell.addNeighbour(cell, cells, Direction.UpLeft)
            cell.addNeighbour(cell, cells, Direction.Up)
            cell.addNeighbour(cell, cells, Direction.UpRight)
            cell.addNeighbour(cell, cells, Direction.Left)
            cell.addNeighbour(cell, cells, Direction.Right)
        }
        Direction.Left -> {
            cell.addNeighbour(cell, cells, Direction.Up)
            cell.addNeighbour(cell, cells, Direction.UpRight)
            cell.addNeighbour(cell, cells, Direction.Right)
            cell.addNeighbour(cell, cells, Direction.Down)
            cell.addNeighbour(cell, cells, Direction.DownRight)
        }

        // Middle / anywhere else
        Direction.Middle -> {
            cell.addNeighbour(cell, cells, Direction.UpLeft)
            cell.addNeighbour(cell, cells, Direction.Up)
            cell.addNeighbour(cell, cells, Direction.UpRight)
            cell.addNeighbour(cell, cells, Direction.Left)
            cell.addNeighbour(cell, cells, Direction.Right)
            cell.addNeighbour(cell, cells, Direction.DownLeft)
            cell.addNeighbour(cell, cells, Direction.Down)
            cell.addNeighbour(cell, cells, Direction.DownRight)
        }
    }
}
