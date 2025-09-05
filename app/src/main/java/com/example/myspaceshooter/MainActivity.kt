package com.example.myspaceshooter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.startGameButton)?.setOnClickListener {
            Log.d(TAG, "Start game button pressed");
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)

        }
    }
}