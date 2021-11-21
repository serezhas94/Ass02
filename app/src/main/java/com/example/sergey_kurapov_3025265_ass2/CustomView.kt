package com.example.sergey_kurapov_3025265_ass2

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CustomView(context: Context?) : View(context) {
    // private fields of the class
    private var _context: Context? = context
    private var _attribs: AttributeSet? = null

    private val _cellRows = 10
    private val _cellColumns = 10

    private val _linesPaint = Paint()
    private val _uncCellPaint = Paint()

    private val _mineCellPaint = Paint()
    private val _letterPaint = Paint()

    private var _pointers: Int = 0
    private var _touchPoint: Point = Point(0.0f, 0.0f)

    private var _cells:ArrayList<Cell> = ArrayList<Cell>(100)
    private var _uncoveredCells:ArrayList<Cell> = ArrayList<Cell>(100)

    private var _isMineExploded: Boolean = false


    // secondary constructor that will take in a context and attribute set
    constructor(context: Context?, attribs: AttributeSet?) : this(context) {
        _attribs = attribs
        _context = context

    }

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

        // initialize cells
        for (i in 0 until _cellRows * _cellColumns) {
            _cells.add(Cell())
        }

        // create 20 mines in random
        val randomIndexes = mutableSetOf<Int>()
        while (randomIndexes.count() < 20) {
            var index = (0..100).random()
            randomIndexes.add(index)
        }

        // set 20 mines in cells
        for(i in randomIndexes) {
            _cells[i].isMineInCell = true
        }
    }

    // overridden draw function that will draw the canvas depending on the mode selected
    override fun onDraw(canvas: Canvas?) {
        // call the superclass drawing function before we start
        super.onDraw(canvas)

        // get the width and height of the canvas which is the available drawing area we have
        var width: Int = canvas!!.width
        var height: Int = canvas!!.height

        var cellWidth = width / _cellColumns
        var cellHeight = height / _cellRows

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

                _cells[count].topLeft = Point((i * cellWidth).toFloat(),(j * cellHeight).toFloat())
                _cells[count].topRight = Point((i * cellWidth + cellWidth).toFloat(),(j * cellHeight).toFloat())

                _cells[count].bottomLeft = Point((i * cellWidth).toFloat(),(j * cellHeight + cellHeight).toFloat())
                _cells[count].bottomRight = Point((i * cellWidth + cellWidth).toFloat(),(j * cellHeight + cellHeight).toFloat())

                count ++
            }
        }

        if(_pointers > 0){

            // find cell which touched
            for (i in 0 until _cells.count()){
                if(_cells[i].isPointInCell(_touchPoint!!.x, _touchPoint!!.y)){
                    //if mine in cell
                    if(_cells[i] .isMineInCell) {
                        // set variable mine exploded to true
                        _isMineExploded = true
                    }
                    _uncoveredCells.add(_cells[i])
                    break
                }
            }
            _pointers = 0
        }

        // draw uncovered cells
        for (cell in _uncoveredCells){
            if(cell.isMineInCell){

                //paint cell in red
                canvas.drawRect(cell.topLeft.x,cell.topLeft.y, cell.bottomRight.x, cell.bottomRight.y , _mineCellPaint)
                // draw M letter
                _letterPaint.textSize = (cell.bottomRight.y - cell.topRight.y) / 2
                var x = (cell.bottomLeft.x + (cell.bottomLeft.x + cell.bottomRight.x) /2) / 2
                var y = ((cell.topRight.y + cell.bottomRight.y)/2 + cell.bottomRight.y) /2
                canvas.drawText("M", x, y, _letterPaint )
            }
            else{
                canvas.drawRect(cell.topLeft.x,cell.topLeft.y, cell.bottomRight.x, cell.bottomRight.y , _uncCellPaint)
            }
        }

    }

    // overridden function that will allow us to react to touch events on our view
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(_isMineExploded){
            var mainActivity = context as MainActivity

            // reset button clicked in main activity
            if(mainActivity.isResetMineExplorer){

               _isMineExploded = false
                mainActivity.isResetMineExplorer = false
            }
        }

        // if mine not exploded  touch event, otherwise ignore it
        if (!_isMineExploded) {

            // see what event we have and take appropriate action
            if (event!!.actionMasked == MotionEvent.ACTION_DOWN
                || event!!.actionMasked == MotionEvent.ACTION_POINTER_DOWN
            ) {
                // either we have a single touch (ACTION_DOWN) or an additional touch (ACTION_POINTER_DOWN) either way
                // add a new pointer and keep track of the id of this pointer
                val index = event.actionIndex
                val id = event.getPointerId(index)

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
        if(width > height){
            setMeasuredDimension(height, height)
        }
        else{
            setMeasuredDimension(width, width)
        }
    }
}