package com.example.myspaceshooter

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.SystemClock.uptimeMillis
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.random.Random

const val STAGE_WIDTH = 1280
const val STAGE_HEIGHT = 672
var RNG = Random(uptimeMillis())
const val STAR_COUNT = 50
const val ENEMY_COUNT = 8

class Game(context: Context?) : SurfaceView(context), Runnable, SurfaceHolder.Callback {
    private val TAG = "GAME"
    lateinit var gameThread: Thread
    @Volatile var isRunning : Boolean = false
    val stars = ArrayList<Star>()
    val enemies = ArrayList<Enemy>()
    val player = Player(this)
    @Volatile var fingerDown = false
    var isBoosting = false

    init {
        holder?.addCallback(this)
        holder?.setFixedSize(STAGE_WIDTH, STAGE_HEIGHT)

        for (i in 0 until STAR_COUNT) {
            stars.add(Star())
        }
        for (i in 0 until ENEMY_COUNT) {
            enemies.add(Enemy(this))
        }
    }

    override fun run() {
        Log.d(TAG, "run()")
        while (isRunning){
            update()
            render()
        }
    }

    private fun render() {
        val canvas = holder?.lockCanvas() ?: return
        canvas.drawColor(Color.BLUE)
        val paint = Paint()

        for (star in stars){
            star.render(canvas, paint)
        }
        for (enemy in enemies){
            enemy.render(canvas, paint)
        }
        player.render(canvas, paint)
        holder.unlockCanvasAndPost(canvas)
    }

    private fun update() {
        isBoosting = fingerDown
        //update all objects (entities)
        player.update(isBoosting)
        for (enemy in enemies){
            enemy.update(player.velX)
        }
        for (star in stars){
            star.update(player.velX)
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN ->  fingerDown = true
            MotionEvent.ACTION_UP -> fingerDown = false
        }
        return true
    }

    fun onPause() {
        Log.d(TAG, "onPause()")
    }

    fun onResume() {
        Log.d(TAG, "onResume()")
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        Log.d(TAG, "surfaceCreated()")
        isRunning = true
        gameThread = Thread(this)
        gameThread.start()
    }

    override fun surfaceChanged(p0: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.d(TAG, "surfaceChanged(width:$width, height:$height)")
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        Log.d(TAG, "surfaceDestroyed()")
        isRunning = false
        gameThread.join()
    }
}