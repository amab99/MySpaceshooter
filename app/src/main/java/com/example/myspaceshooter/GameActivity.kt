package com.example.myspaceshooter

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    private val tag = "GameActivity"
    lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "Game Activity was launched")
        game = Game(this)
        setContentView(game)
    }

    override fun onPause() {
        game.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        game.onResume()
    }

    override fun onDestroy() {
        game.onDestroy()
        super.onDestroy()
    }
}
