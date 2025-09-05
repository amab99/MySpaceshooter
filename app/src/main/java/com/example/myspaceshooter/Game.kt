package com.example.myspaceshooter

import android.content.Context
import android.view.SurfaceView

class Game(context: Context?) : SurfaceView(context), Runnable {
    override fun run() {
        //while isRunning
            //update game state
            //render game state
    }

    fun pause() {
        //join the thread (stop the game from processing)
    }

    fun resume() {
        //create a new thread,
        //set isRunning = true
        //start the thread
    }

}