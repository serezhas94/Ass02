package com.example.sergey_kurapov_3025265_ass2

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.GridView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class CustomView(context: Context?) : View(context) {
    // private fields of the class
    private var _context: Context? = context
    private var _attribs: AttributeSet? = null

    private val _cellRows = 10
    private val _cellColumns = 10
    private val _lines = Paint()


    private var _radius: Float = 0.0f
    private var _pointers: Int = 0
    private var _touch_x: HashMap<Int, Float> = HashMap<Int, Float>()
    private var _touch_y: HashMap<Int, Float> = HashMap<Int, Float>()
    private var _colour: HashMap<Int, Paint> = HashMap<Int, Paint>()


    // secondary constructor that will take in a context and attribute set
    constructor(context: Context?, attribs: AttributeSet?) : this(context) {
        _attribs = attribs
    }


    // init block that will do the rest of the initialisation
    init {

        _lines.style = Paint.Style.FILL_AND_STROKE
        _lines.color = Color.WHITE
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
                0.0f, (i * cellHeight).toFloat(), getWidth().toFloat(), (i * cellHeight).toFloat(),
                _lines
            )
        }

        // draw lines for columns
        for (i in 0 until _cellColumns) {
            canvas.drawLine(
                (i * cellWidth).toFloat(), 0.0f, (i * cellWidth).toFloat(), getHeight().toFloat(),
                _lines
            )
        }

        // set the radius of the circles to be 15% of the screen width
        _radius = width * 0.15f

        // we will call a different draw function depending on what touch situations we are in
        when {
            _pointers == 0 -> drawNoTouch(canvas)
            _pointers == 1 -> drawSingleTouch(canvas)
            _pointers > 1 -> drawMultiTouch(canvas)
        }
    }

    // function that will draw some basic shapes if there is no touch on screen
    private fun drawNoTouch(canvas: Canvas?) {


    }

    // function that will draw on single touch
    private fun drawSingleTouch(canvas: Canvas?) {

    }

    // function that will draw if multi touch is enabled
    private fun drawMultiTouch(canvas: Canvas?) {

    }

    // overridden function that will allow us to react to touch events on our view
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        // see what event we have and take appropriate action
        if (event!!.actionMasked == MotionEvent.ACTION_DOWN
            || event!!.actionMasked == MotionEvent.ACTION_POINTER_DOWN
        ) {
            // either we have a single touch (ACTION_DOWN) or an additional touch (ACTION_POINTER_DOWN) either way
            // add a new pointer and keep track of the id of this pointer
            // get the pointer index that has been added and figure out the id of that pointer.
            // once we know this then add that info into the hashmap and increment the amount of
            // pointers we have
            val index = event.actionIndex
            val id = event.getPointerId(index)
            _touch_x.put(id, event.x)
            _touch_y.put(id, event.y)


            // increment the number of pointers also invalidate the display and indicate this event has been handled
            _pointers++
            invalidate()
            return true

        } else if (event!!.actionMasked == MotionEvent.ACTION_UP
            || event!!.actionMasked == MotionEvent.ACTION_POINTER_UP
        ) {
            // either we have the last touch removed (ACTION_UP) or one of the additional touches have been
            // removed (ACTION_POINTER_UP) either way remove the pointer that caused either event remove the id of our pointer from this

            val index = event.actionIndex
            val id = event.getPointerId(index)
            _touch_x.remove(id)
            _touch_y.remove(id)


            // decrement the number of pointers also invalidate the display and indicate this event has been handled
            _pointers--
            invalidate()
            return true

        } else if (event.actionMasked == MotionEvent.ACTION_MOVE) {
            // go through all of the ids and update the appropriate pointer ids that have moved
            for (key in _touch_x.keys) {
                val index = event.findPointerIndex(key)
                _touch_x.put(key, event.getX(index))
                _touch_y.put(key, event.getY(index))
            }
            // invalidate the display so the screen is updated
            invalidate()
            return true
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