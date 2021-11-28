package com.example.sergey_kurapov_3025265_ass2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

// to interact with view, at a minimum a constructor
// that takes a Context and an AttributeSet object as parameters
class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){
    // private fields of the class

    private val _cellRows = 10
    private val _cellColumns = 10

    private val _linesPaint = Paint()
    private val _uncCellPaint = Paint()

    private val _mineCellPaint = Paint()
    private val _letterPaint = Paint()

    private val _markingCellPaint = Paint()

    private var _pointers: Int = 0
    private var _touchPoint: Point = Point(0.0f, 0.0f)

    var cells: ArrayList<Cell> = ArrayList(100)
    var uncoveredCells: ArrayList<Cell> = ArrayList(100)

    var isMineExploded: Boolean = false

    var markedMinesCount: Int = 0
    var totalMinesCount: Int = 0


    // init block that will do the rest of the initialisation
    init {

        _linesPaint.style = Paint.Style.FILL_AND_STROKE
        _linesPaint.color = Color.WHITE

        _uncCellPaint.style = Paint.Style.FILL
        _uncCellPaint.color = Color.GRAY

        _mineCellPaint.style = Paint.Style.FILL
        _mineCellPaint.color = Color.RED

        _letterPaint.style = Paint.Style.FILL
        _letterPaint.color = Color.BLACK

        _markingCellPaint.style = Paint.Style.FILL
        _markingCellPaint.color = Color.YELLOW

        initializeData()

        val mainActivity = context as MainActivity
        mainActivity.markedMines = markedMinesCount
        mainActivity.totalMines = totalMinesCount
    }

    private fun initializeData() {
        markedMinesCount = 0
        totalMinesCount = 20

        // initialize cells
        for (i in 0 until _cellRows * _cellColumns) {
            cells.add(Cell(i))
        }

        // create mines in random
        val randomIndexes = mutableSetOf<Int>()
        while (randomIndexes.count() < totalMinesCount) {
            val index = (0..99).random()
            randomIndexes.add(index)
        }

        // set 20 mines in cells
        for (i in randomIndexes) {
            cells[i].isMineInCell = true
        }

        setCellNumMinesAround()
    }


    // overridden draw function that will draw the canvas depending on the mode selected
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        // call the superclass drawing function before we start
        super.onDraw(canvas)

        val mainActivity = context as MainActivity
        if(mainActivity.isResetMineExplorer){
            mainActivity.isResetMineExplorer = false

            // clear previous data
            cells.clear()
            uncoveredCells.clear()

            //initialize data again
            initializeData()

            // reset mine exploded
            isMineExploded = false

            //reset mines count in main activity
            mainActivity.markedMines = markedMinesCount
            mainActivity.totalMines = totalMinesCount
        }

        // get the width and height of the canvas which is the available drawing area we have
        val width: Int = canvas!!.width
        val height: Int = canvas!!.height

        val cellWidth = width / _cellColumns
        val cellHeight = height / _cellRows

        // fill the entire canvas in black
        canvas.drawColor(Color.BLACK)

        // draw lines for rows
        for (i in 0 until _cellRows) {
            canvas.drawLine(
                0.0f, (i * cellHeight).toFloat(), width.toFloat(), (i * cellHeight).toFloat(),
                _linesPaint
            )
        }

        // draw lines for columns
        for (i in 0 until _cellColumns) {
            canvas.drawLine(
                (i * cellWidth).toFloat(), 0.0f, (i * cellWidth).toFloat(), height.toFloat(),
                _linesPaint
            )
        }

        // set cell corner points
        var count = 0
        for (i in 0 until _cellRows) {
            for (j in 0 until _cellColumns) {

                cells[count].topLeft = Point((i * cellWidth).toFloat(), (j * cellHeight).toFloat())
                cells[count].topRight =
                    Point((i * cellWidth + cellWidth).toFloat(), (j * cellHeight).toFloat())

                cells[count].bottomLeft =
                    Point((i * cellWidth).toFloat(), (j * cellHeight + cellHeight).toFloat())
                cells[count].bottomRight = Point(
                    (i * cellWidth + cellWidth).toFloat(),
                    (j * cellHeight + cellHeight).toFloat()
                )
                count++
            }
        }

        if (_pointers > 0) {

            // find cell which touched
            for (i in 0 until cells.count()) {
                if (cells[i].isPointInCell(_touchPoint.x, _touchPoint.y)) {

                    // in marking mode
                    if (mainActivity.isMarkingMode) {

                        if (cells[i].isMarked) {
                            cells[i].isMarked = false

                            // decrease marked mines count
                            if (cells[i].isMineInCell) {
                                markedMinesCount--
                            }

                            // remove from uncovered cells
                            uncoveredCells.remove(cells[i])

                        } else if (!uncoveredCells.contains(cells[i])) {
                            cells[i].isMarked = true

                            // increase marked mines count
                            if (cells[i].isMineInCell) {
                                markedMinesCount++
                            }
                            // add to uncovered cells
                            uncoveredCells.add(cells[i])
                        }
                    }
                    // in uncover mode
                    else {
                        if (!cells[i].isMarked) {
                            //if mine in cell
                            if (cells[i].isMineInCell) {

                                // set variable mine exploded to true
                                isMineExploded = true
                            }
                            // add to uncovered cells
                            uncoveredCells.add(cells[i])
                        }
                    }
                    break
                }
            }
            _pointers = 0
            mainActivity.markedMines = markedMinesCount
        }
        // draw uncovered cells
        for (cell in uncoveredCells) {

            if (cell.isMarked) {
                canvas.drawRect(
                    cell.topLeft.x,
                    cell.topLeft.y,
                    cell.bottomRight.x,
                    cell.bottomRight.y,
                    _markingCellPaint
                )
            } else if (cell.isMineInCell) {

                //paint cell in red
                canvas.drawRect(
                    cell.topLeft.x,
                    cell.topLeft.y,
                    cell.bottomRight.x,
                    cell.bottomRight.y,
                    _mineCellPaint
                )
                // draw M letter
                _letterPaint.textSize = (cell.bottomRight.y - cell.topRight.y) / 2
                val x = (cell.bottomLeft.x + (cell.bottomLeft.x + cell.bottomRight.x) / 2) / 2
                val y = ((cell.topRight.y + cell.bottomRight.y) / 2 + cell.bottomRight.y) / 2
                canvas.drawText("M", x, y, _letterPaint)
            } else {
                canvas.drawRect(
                    cell.topLeft.x,
                    cell.topLeft.y,
                    cell.bottomRight.x,
                    cell.bottomRight.y,
                    _uncCellPaint
                )
                if(cell.numOfMinesAround > 0){
                    // draw number
                    _letterPaint.textSize = (cell.bottomRight.y - cell.topRight.y) / 2
                    val x = (cell.bottomLeft.x + (cell.bottomLeft.x + cell.bottomRight.x) / 2) / 2
                    val y = ((cell.topRight.y + cell.bottomRight.y) / 2 + cell.bottomRight.y) / 2
                    canvas.drawText(cell.numOfMinesAround.toString(), x, y, _letterPaint)
                }
            }
        }
    }

    // overridden function that will allow us to react to touch events on our view
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (isMineExploded) {
            val mainActivity = context as MainActivity

            // reset button clicked in main activity
            if (mainActivity.isReEnable) {

                isMineExploded = false
                mainActivity.isReEnable = false
            }
        }

        // if mine not exploded  touch event, otherwise ignore it
        if (!isMineExploded) {

            // see what event we have and take appropriate action
            if (event!!.actionMasked == MotionEvent.ACTION_DOWN
                || event.actionMasked == MotionEvent.ACTION_POINTER_DOWN
            ) {
                // either we have a single touch (ACTION_DOWN) or an additional touch (ACTION_POINTER_DOWN) either way
                // add a new pointer and keep track of the id of this pointer
                _touchPoint.x = event.x
                _touchPoint.y = event.y

                // increment the number of pointers also invalidate the display and indicate this event has been handled
                _pointers++
                invalidate()
                return true
            }
        }

        // if we haven't handled the event then we need to pass it on to see what else could handle it
        return super.onTouchEvent(event)
    }

    // set height same as width
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = measuredHeight
        if (width > height) {
            setMeasuredDimension(height, height)
        } else {
            setMeasuredDimension(width, width)
        }
    }

    // set in cell the number the mines around
    private fun setCellNumMinesAround() {
        for (i in 0 until cells.count()) {
            // the first left column
            if (i % 10 == 0) {
                // the first row
                if (i < 10) {
                    cells[i].numOfMinesAround =
                        boolToInt(cells[i + 1].isMineInCell) + boolToInt(cells[i + 10].isMineInCell) + boolToInt(
                            cells[i + 11].isMineInCell
                        )
                }
                // the last row
                else if (i > 89) {
                    cells[i].numOfMinesAround =
                        boolToInt(cells[i + 1].isMineInCell) + boolToInt(cells[i - 10].isMineInCell) + boolToInt(
                            cells[i - 9].isMineInCell
                        )
                }
                // rows between
                else {
                    cells[i].numOfMinesAround = boolToInt(cells[i + 1].isMineInCell) + boolToInt(cells[i + 10].isMineInCell) + boolToInt(cells[i + 11].isMineInCell)
                    cells[i].numOfMinesAround = cells[i].numOfMinesAround  + boolToInt(cells[i - 10].isMineInCell) + boolToInt(cells[i - 9].isMineInCell)
                }
            }
            // the last right column
            else if (i % 10 == 9) {
                // the first row
                if (i < 10) {
                    cells[i].numOfMinesAround =
                        boolToInt(cells[i - 1].isMineInCell) + boolToInt(cells[i + 10].isMineInCell) + boolToInt(
                            cells[i + 9].isMineInCell
                        )
                }
                // the last row
                else if (i > 89) {
                    cells[i].numOfMinesAround =
                        boolToInt(cells[i - 1].isMineInCell) + boolToInt(cells[i - 10].isMineInCell) + boolToInt(
                            cells[i - 11].isMineInCell
                        )
                }
                // rows between
                else {
                    cells[i].numOfMinesAround = boolToInt(cells[i - 1].isMineInCell) + boolToInt(cells[i + 10].isMineInCell) + boolToInt(cells[i + 9].isMineInCell)
                    cells[i].numOfMinesAround = cells[i].numOfMinesAround  + boolToInt(cells[i - 10].isMineInCell) + boolToInt(cells[i - 11].isMineInCell)
                }
            }
            // the rest of columns
            else {
                // the first row
                if (i < 10) {
                    cells[i].numOfMinesAround = boolToInt(cells[i + 1].isMineInCell) + boolToInt(cells[i + 10].isMineInCell) + boolToInt(cells[i + 11].isMineInCell)
                    cells[i].numOfMinesAround = cells[i].numOfMinesAround + boolToInt(cells[i - 1].isMineInCell)  + boolToInt(cells[i + 9].isMineInCell)
                }
                // the last row
                else if (i > 89) {
                    cells[i].numOfMinesAround = boolToInt(cells[i + 1].isMineInCell) + boolToInt(cells[i - 10].isMineInCell) + boolToInt(cells[i - 9].isMineInCell)
                    cells[i].numOfMinesAround = cells[i].numOfMinesAround + boolToInt(cells[i - 1].isMineInCell)  + boolToInt(cells[i - 11].isMineInCell)
                }
                // rows between
                else {
                    cells[i].numOfMinesAround =
                        boolToInt(cells[i + 1].isMineInCell) + boolToInt(cells[i + 10].isMineInCell) + boolToInt(cells[i + 11].isMineInCell)
                    cells[i].numOfMinesAround =  cells[i].numOfMinesAround + boolToInt(cells[i - 10].isMineInCell) + boolToInt(cells[i - 9].isMineInCell)
                    cells[i].numOfMinesAround =  cells[i].numOfMinesAround + boolToInt(cells[i - 1].isMineInCell) + boolToInt(cells[i + 9].isMineInCell)
                    cells[i].numOfMinesAround =  cells[i].numOfMinesAround   + boolToInt(cells[i - 11].isMineInCell)
                }
            }
        }
    }

    // additional fun help to convert boolean to integer
    private fun boolToInt(b: Boolean): Int {
        return if (b) 1 else 0
    }
}