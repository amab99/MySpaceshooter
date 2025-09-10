package com.example.myspaceshooter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val tag = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.startGameButton)?.setOnClickListener {
            Log.d(tag, "Start game button pressed")
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)

        }
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        val longestDistance = prefs.getFloat(LONGEST_DIST, 0.0f)
        val highscore =findViewById<TextView>(R.id.highscore)
        highscore.text = getString(R.string.longest_distance, longestDistance.toInt())
    }
}