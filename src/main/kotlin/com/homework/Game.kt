package main.kotlin.com.homework

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.stage.Stage

class Game : Application() {

    companion object {
        private const val WIDTH  = 500.0
        private const val HEIGHT = 500.0
        private const val BOTTOMHEIGHT = 50.0
        const val CELLWIDTH  = 25.0
        const val CELLHEIGHT = 25.0
        private const val CELLSPERROW    = (WIDTH  / CELLWIDTH).toInt()
        private const val CELLSPERCOLUMN = (HEIGHT / CELLHEIGHT).toInt()

        // Time in milliseconds between each update
        private const val deltaT = 300_000_000L
    }

    private var isPaused = false
    private var clickReceived = false
    private var clkPosX = 0.0
    private var clkPosY = 0.0

    private lateinit var mainScene: Scene
    private lateinit var graphicsContext: GraphicsContext
    private var button = Button("Pause")

    private var lastFrameTime: Long = System.nanoTime()

    // use a set so duplicates are not possible
    private val currentlyActiveKeys = mutableSetOf<KeyCode>()

    // create cells using window and cell sizes
    private val cells = List(CELLSPERROW) { X: Int ->
        List(CELLSPERCOLUMN) { Y: Int ->
            Cell(X, Y)
        }
    }

    init {
        for (row in cells) for (cell in row) {
            // init each cell's neighbours by their position
            when {
                // corner cells
                cell.x == 0               && cell.y == 0                  -> initNeighbours(cell, cells, Direction.UpLeft)
                cell.x == CELLSPERROW - 1 && cell.y == 0                  -> initNeighbours(cell, cells, Direction.UpRight)
                cell.x == 0               && cell.y == CELLSPERCOLUMN - 1 -> initNeighbours(cell, cells, Direction.DownLeft)
                cell.x == CELLSPERROW - 1 && cell.y == CELLSPERCOLUMN - 1 -> initNeighbours(cell, cells, Direction.DownRight)

                // side cells
                cell.y == 0 -> initNeighbours(cell, cells, Direction.Up)
                cell.x == 0 -> initNeighbours(cell, cells, Direction.Left)
                cell.y == CELLSPERCOLUMN - 1 -> initNeighbours(cell, cells, Direction.Down)
                cell.x == CELLSPERROW    - 1 -> initNeighbours(cell, cells, Direction.Right)

                // middle cells
                else -> initNeighbours(cell, cells, Direction.Middle)
            }
        }

        // oscillator pattern with 15 step long period
        starterPeriod()
    }

    override fun start(mainStage: Stage) {
        mainStage.title = "Game of Life"

        val root = Group()
        mainScene = Scene(root)
        mainStage.scene = mainScene

        for (row in cells) for (cell in row) {
            root.children.add(cell.rect)
        }

        val canvas = Canvas(WIDTH, HEIGHT + BOTTOMHEIGHT)
        root.children.add(canvas)
        root.children.add(button)

        prepareActionHandlers()

        graphicsContext = canvas.graphicsContext2D

        // Main loop
        object : AnimationTimer() {
            override fun handle(currentNanoTime: Long) {
                tickAndRender(currentNanoTime)
            }
        }.start()

        mainStage.show()
    }

    private fun prepareActionHandlers() {
        mainScene.onKeyPressed = EventHandler { event ->
            currentlyActiveKeys.add(event.code)
        }
        mainScene.onKeyReleased = EventHandler { event ->
            currentlyActiveKeys.remove(event.code)
        }
        mainScene.onMouseClicked = EventHandler { event ->
            clkPosX = event.sceneX
            clkPosY = event.sceneY
            clickReceived = true
        }

        button.setOnAction {
            isPaused = !isPaused

            if (isPaused)
                button.text = "Start"
            else
                button.text = "Pause"
        }
    }

    private fun calcButtonPos() {
        button.layoutX = (WIDTH - button.width) / 2.0
        button.layoutY = HEIGHT + (BOTTOMHEIGHT - button.height) / 2.0
    }

    private fun tickAndRender(currentNanoTime: Long) {
        calcButtonPos()

        if (clickReceived) {
            editCells()
        }

        // the time elapsed since the last frame, in nanoseconds
        val elapsedNanos = currentNanoTime - lastFrameTime

        // check if it's too early to update
        if (elapsedNanos < deltaT) return
        lastFrameTime = currentNanoTime

        // clear canvas
        graphicsContext.clearRect(0.0, 0.0, WIDTH, HEIGHT + BOTTOMHEIGHT)

        // perform world updates
        if (isPaused.not())
            updateCells()
    }

    private fun editCells() {
        if (isPaused && clkPosX < WIDTH && clkPosY < HEIGHT) {
            val cellX: Int = ((clkPosX - clkPosX % CELLWIDTH) / CELLWIDTH).toInt()
            val cellY: Int = ((clkPosY - clkPosY % CELLHEIGHT) / CELLHEIGHT).toInt()

            // flip state of clicked cell
            cells[cellX][cellY].isAlive = cells[cellX][cellY].isAlive.not()
        }
        clickReceived = false
    }

    private fun updateCells() {
        // calculate the next state
        for (row in cells) for (cell in row) {
            cell.tick()
        }
        // actually switch to next state
        for (row in cells) for (cell in row) {
            cell.switchState()
        }
    }

    // Sorry this is ugly
    private fun starterPeriod() {
        cells[10][3].isAlive = true
        cells[9][4].isAlive = true
        cells[10][4].isAlive = true
        cells[11][4].isAlive = true
        cells[8][5].isAlive = true
        cells[9][5].isAlive = true
        cells[10][5].isAlive = true
        cells[11][5].isAlive = true
        cells[12][5].isAlive = true

        cells[10][14].isAlive = true
        cells[9][13].isAlive = true
        cells[10][13].isAlive = true
        cells[11][13].isAlive = true
        cells[8][12].isAlive = true
        cells[9][12].isAlive = true
        cells[10][12].isAlive = true
        cells[11][12].isAlive = true
        cells[12][12].isAlive = true
    }

}
