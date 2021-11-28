package com.example.sergey_kurapov_3025265_ass2

import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    var isResetMineExplorer: Boolean = false
    var isMarkingMode:Boolean = false

    var totalMines:Int = 0
    var markedMines:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // get reference to Reset button
        val btnReset = findViewById<Button>(R.id.btnReset)
        // on the button click set reset to true
        btnReset.setOnClickListener {
            isResetMineExplorer = true
        }

        // get reference to Uncover button
        val btnUncover = findViewById<Button>(R.id.btnUncover)
        // change the mode on the button click
        btnUncover.setOnClickListener{
            if(btnUncover.text == getString(R.string.uncover_mode_button)){
                isMarkingMode = true
                btnUncover.text = getString(R.string.marking_mode_button)
            }
            else{
                btnUncover.text = getString(R.string.uncover_mode_button)
                isMarkingMode = false
            }
        }

        // set text in text views
        val txtMarkedMines = findViewById<TextView>(R.id.txtMarkedMines)
        txtMarkedMines.text = getString(R.string.number_of_mines_marked) + " " + markedMines
        txtMarkedMines.setTypeface(null, Typeface.BOLD)

        val txtTotalMines = findViewById<TextView>(R.id.txtTotalMines)
        txtTotalMines.text = getString(R.string.total_number) + " " + totalMines
        txtTotalMines.setTypeface(null, Typeface.BOLD)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // get reference to text view
        val txtMarkedMines = findViewById<TextView>(R.id.txtMarkedMines)
        txtMarkedMines.text = getString(R.string.number_of_mines_marked) + " " + markedMines
        txtMarkedMines.setTypeface(null, Typeface.BOLD)

        return super.onTouchEvent(event)
    }
}