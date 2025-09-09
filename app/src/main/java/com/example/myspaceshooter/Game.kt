package com.example.myspaceshooter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.SystemClock.uptimeMillis
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.round
import kotlin.random.Random

const val STAGE_WIDTH = 1280
const val STAGE_HEIGHT = 672
var RNG = Random(uptimeMillis())
const val STAR_COUNT = 50
const val ENEMY_COUNT = 8

class Game(context: Context) : SurfaceView(context), Runnable, SurfaceHolder.Callback {
    private val tag = "GAME"
    private lateinit var gameThread: Thread
    @Volatile var isRunning : Boolean = false
    private val stars = ArrayList<Star>()
    private val enemies = ArrayList<Enemy>()
    private val player = Player(this)
    @Volatile var fingerDown = false
    private var isBoosting = false
    private var isGameOver = false
    private var jukebox = Jukebox(context.assets)

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
        Log.d(tag, "run()")
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
        renderHud(canvas, paint)
        holder.unlockCanvasAndPost(canvas)
    }

    private fun renderHud(canvas: Canvas, paint: Paint) {
        val textSize = 48f
        val textPosition = 10f
        paint.color = Color.WHITE
        paint.textSize = textSize
        paint.textAlign = Paint.Align.LEFT
        if(!isGameOver){
            canvas.drawText("Health: ${player.health}", textPosition, textSize, paint)
            canvas.drawText("Distance: ${round(player.distanceTraveled)}", textPosition, textSize*2, paint)

        }else{
            val centerX = STAGE_WIDTH / 2.0f
            val centerY = STAGE_HEIGHT / 2.0f
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("GAME OVER", centerX, centerY, paint)
            canvas.drawText("Press to  restart", centerX, centerY + textSize, paint)

        }
    }


    private fun update() {
        if(isGameOver){
            return
        }

        isBoosting = fingerDown
        //update all objects (entities)
        player.update(isBoosting)
        for (enemy in enemies){
            enemy.update(player.velX)
        }
        for (star in stars){
            star.update(player.velX)
        }

        checkCollisions()
        checkGameOver()
    }

    private fun checkGameOver() {
        if(player.health < 0){
            isGameOver = true
        }

    }

    private fun checkCollisions() {
        for (enemy in enemies) {
            if (isColliding(enemy, player)) {
                enemy.onCollision(player)
                player.onCollision(enemy)
                jukebox.play(SFX.crash)
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN ->  fingerDown = true
            MotionEvent.ACTION_UP -> {
                fingerDown = false
                if(isGameOver){
                    restart()
                }
            }
        }
        return true
    }

    private fun restart() {
        for (enemy in enemies){
            enemy.respawn()
        }
        player.respawn()
        isGameOver = false
    }

    fun onPause() {
        Log.d(tag, "onPause()")
    }

    fun onResume() {
        Log.d(tag, "onResume()")
    }

    fun onDestroy() {
        Log.d(tag, "onDestroy()")
        jukebox.destroy()
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        Log.d(tag, "surfaceCreated()")
        isRunning = true
        gameThread = Thread(this)
        gameThread.start()
    }

    override fun surfaceChanged(p0: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.d(tag, "surfaceChanged(width:$width, height:$height)")
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        Log.d(tag, "surfaceDestroyed()")
        isRunning = false
        gameThread.join()
    }
}