package com.example.myspaceshooter

import android.content.Context
import android.util.Log
import android.view.SurfaceView

class Game(context: Context?) : SurfaceView(context), Runnable {
    private val TAG = "GAME"
    lateinit var gameThread: Thread
    @Volatile var isRunning : Boolean = false

    override fun run() {
        while (isRunning){
            update();
            render();
        }
    }

    private fun render() {
        //lock and acquire a surface to draw to
        //draw all entities
        //unlock the surface, and post it to the system for drawing
    }

    private fun update() {
        //update all objects (entities)
        Log.d(TAG, "update()")
    }

    fun onPause() {
        isRunning = false
        gameThread.join()
        Log.d(TAG, "onPause()")
    }

    fun onResume() {
        Log.d(TAG, "onResume()")
        isRunning = true;
        gameThread = Thread(this)
        gameThread.start()
    }

}