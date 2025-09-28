package com.example.myspaceshooter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * This class represents the main activity of the game.
 */
class GameActivity : AppCompatActivity() {
    lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        game = Game(this)
        setContentView(game)
    }

    override fun onPause() {
        super.onPause()
        game.onPause()
    }

    override fun onResume() {
        game.onResume()
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
        game.onDestroy()
    }
    override fun onStop() {
        super.onStop()
        game.onPause()
    }
}
