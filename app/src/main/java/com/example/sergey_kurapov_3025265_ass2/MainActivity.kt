package com.example.sergey_kurapov_3025265_ass2

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    var isResetMineExplorer: Boolean = false
    var isReEnable: Boolean = false
    var isMarkingMode:Boolean = false

    var totalMines:Int = 0
    var markedMines:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // if rotation changed or state amended and previous state is saved
        // repopulate fields and data from saved state
        if(savedInstanceState != null){
            isResetMineExplorer = savedInstanceState.getBoolean("isResetMineExplorer" )
            isReEnable = savedInstanceState.getBoolean("isReEnable")
            isMarkingMode = savedInstanceState.getBoolean("isMarkingMode")
            totalMines = savedInstanceState.getInt("totalMines")
            markedMines = savedInstanceState.getInt("markedMines")

            // restore cells state in Custom View without point locations on the screen, the location changed
            val savedCells = savedInstanceState.getSerializable("cells") as ArrayList<Cell>
            val customViewM = findViewById<CustomView>(R.id.customViewMines)
            val currentCells = customViewM.cells
            for(i in 0 until currentCells.size){
                currentCells[i].copyState(savedCells[i])
            }

            // restore uncovered cells state in Custom View
            val savedUncoveredCells = savedInstanceState.getSerializable("uncoveredCells") as ArrayList<Cell>
            val currentUncoveredCells = customViewM.uncoveredCells

            for(cell in savedUncoveredCells){
                // add same cell from current cells
                currentUncoveredCells.add(currentCells[cell.id])
            }

            // restore other fields in Custom View
            customViewM.isMineExploded = savedInstanceState.getBoolean("isMineExploded")
            customViewM.markedMinesCount = savedInstanceState.getInt("markedMinesCount")
            customViewM.totalMinesCount = savedInstanceState.getInt("totalMinesCount")

            // restore Uncover Mode button text
            val btnUncover = findViewById<Button>(R.id.btnUncover)
            btnUncover.text = savedInstanceState.getCharSequence("btnUncoverTxt")
        }

        // get reference to Reset button
        val btnReEnable = findViewById<Button>(R.id.btnReEnable)
        // on the button click re enable touch
        btnReEnable.setOnClickListener {
            isReEnable = true
        }

        // get reference to Reset button
        val btnReset = findViewById<Button>(R.id.btnReset)
        // on the button click set reset to true
        btnReset.setOnClickListener {
            isResetMineExplorer = true

            val customViewM = findViewById<View>(R.id.customViewMines)
            // force to redraw screen
            customViewM.invalidate()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // save main activity fields
        outState.putBoolean("isResetMineExplorer", isResetMineExplorer)
        outState.putBoolean("isReEnable",isReEnable)
        outState.putBoolean("isMarkingMode",isMarkingMode)
        outState.putInt("totalMines",totalMines )
        outState.putInt("markedMines",markedMines)

        // save  button text
        val btnUncover = findViewById<Button>(R.id.btnUncover)
        outState.putCharSequence("btnUncoverTxt",btnUncover.text)

        // save view data
        val customViewM = findViewById<CustomView>(R.id.customViewMines)
        outState.putSerializable("cells", customViewM.cells)
        outState.putSerializable("uncoveredCells", customViewM.uncoveredCells)

        outState.putBoolean("isMineExploded", customViewM.isMineExploded)
        outState.putInt("markedMinesCount",customViewM.markedMinesCount )
        outState.putInt("totalMinesCount",customViewM.totalMinesCount )
    }


}