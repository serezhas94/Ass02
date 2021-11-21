package com.example.sergey_kurapov_3025265_ass2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {
    var isResetMineExplorer: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // get reference to button
        val btnReset = findViewById<Button>(R.id.btnReset)
        btnReset.setOnClickListener {
            isResetMineExplorer = true
        }


    }
}