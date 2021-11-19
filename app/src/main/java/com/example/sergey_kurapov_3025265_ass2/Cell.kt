package com.example.sergey_kurapov_3025265_ass2

import android.R.bool




class Cell {
    var topLeft = Point(0.0f, 0.0f)
    var topRight = Point(0.0f, 0.0f)
    var bottomLeft = Point(0.0f, 0.0f)
    var bottomRight = Point(0.0f, 0.0f)

    var isMineInCell = false
    var numOfMinesAround = 0

    // check if point belongs to cell
    fun isPointInCell(x: Float, y: Float): Boolean {

        // return true if point on shape or inside cell, otherwise false
        return isPointOnShape(x, y) || isPointInShape(x, y)
    }

    // check if point on shape of cell
    private fun isPointOnShape(x: Float, y: Float): Boolean {

        if (x.toFloat() == bottomLeft.x || x.toFloat() == topRight.x)
            return (y > bottomLeft.y && y < topRight.y)

        if (y.toFloat() == bottomLeft.y || y.toFloat() == topRight.y)
            return (x > bottomLeft.x && x < topRight.x)

        return false
    }

    // check if point inside shape
    private fun isPointInShape(x: Float, y: Float): Boolean {
        var inX: Boolean = false
        var inY: Boolean = false

        if (x > bottomLeft.x && x < topRight.x)
            inX = true

        if (y < bottomLeft.y && y > topRight.y)
            inY = true

        return inX && inY
    }

}