package com.example.myspaceshooter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.SystemClock.uptimeMillis
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.graphics.scale
import kotlin.collections.listOf
import kotlin.math.round
import kotlin.random.Random

const val STAGE_WIDTH = 1280
const val STAGE_HEIGHT = 672
var RNG = Random(uptimeMillis())
const val STAR_COUNT = 80
const val ENEMY_COUNT = 8
const val PREFS  = "com.example.myspaceshooter"
const val LONGEST_DIST = "longest_distance"

/**
 * This class represents the main engine of the game.
 */
class Game(context: Context) : SurfaceView(context), Runnable, SurfaceHolder.Callback {
    private val tag = "GAME"
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val editor = prefs.edit()
    private var maxDistanceTraveled = 0.0f
    private lateinit var gameThread: Thread
    @Volatile var isRunning : Boolean = false
    private val stars = ArrayList<Star>()
    private val enemies = ArrayList<Enemy>()
    private val player = Player(this)
    @Volatile var fingerDown = false
    private var isBoosting = false
    private var jukebox = Jukebox(context.assets)
    private val planets = ArrayList<Planet>()
    private val planetsBitmaps: List<Bitmap>
    private var boostStreamId: Int = 0
    private val explosions = ArrayList<Explosion>()

    private var currentGameState = GameState.INITIALIZING

    private var ignoreInputUntilNextRelease = false

    private val bgBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bkgd_0)

    private val bg: Bitmap = bgBitmap.scale(STAGE_WIDTH, STAGE_HEIGHT)

    /**
     * Initializes the game by creating the stars, enemies, player, and planets.
     */
    init {
        holder?.addCallback(this)
        holder?.setFixedSize(STAGE_WIDTH, STAGE_HEIGHT)
        planetsBitmaps = listOf(
            BitmapFactory.decodeResource(resources, R.drawable.planet_1),
            BitmapFactory.decodeResource(resources, R.drawable.planet_2),
            BitmapFactory.decodeResource(resources, R.drawable.planet_3),
            BitmapFactory.decodeResource(resources, R.drawable.planet_4)).map { bitmap ->
            bitmap.scale(200, 200)
        }

        (0 until STAR_COUNT).forEach { i ->
            stars.add(Star())
        }

        planets.add(Planet(planetsBitmaps[0], 1))
        planets.add(Planet(planetsBitmaps[1], 2))
        planets.add(Planet(planetsBitmaps[2], 3))
        planets.add(Planet(planetsBitmaps[3], 4))

        (0 until ENEMY_COUNT).forEach { i ->
            enemies.add(Enemy(this))
        }
        maxDistanceTraveled = prefs.getFloat(LONGEST_DIST, 0.0f)
    }

    /**
     * The main game loop.
     * This method is called by the game thread to update the game state.
     */
    override fun run() {
        while (isRunning){
            if (currentGameState == GameState.PLAYING){
                update()
            } else{
                explosions.forEach { it.update() }
                explosions.removeAll { !it.isAlive() }
            }
            render()
        }
    }

    /**
     *  Renders the game objects (background, entities, explosions and hud) on the canvas.
     */
    private fun render() {
        val canvas = holder?.lockCanvas() ?: return
        try {
            canvas.drawBitmap(bg, 0f, 0f, null)
            val paint = Paint()

            for (star in stars){
                star.render(canvas, paint)
            }
            for (planet in planets){
                planet.render(canvas, paint)
            }
            for (enemy in enemies){
                enemy.render(canvas, paint)
            }
            player.render(canvas, paint)

            explosions.forEach { it.draw(canvas, paint) }
            renderHud(canvas, paint)
        } finally {
            holder.unlockCanvasAndPost(canvas)
        }
    }

    /**
     *  Draws the player health and distance traveled by the player
     */
    private fun renderHud(canvas: Canvas, paint: Paint) {
        val textSize = 48f
        val textPosition = 10f
        paint.color = Color.WHITE
        paint.textSize = textSize
        paint.textAlign = Paint.Align.LEFT
        paint.alpha = 255

        when (currentGameState){
            GameState.PLAYING -> {
                val healthText = resources.getString(R.string.health_text)
                val distanceText = resources.getString(R.string.distance_text)
                canvas.drawText( "$healthText ${player.health}", textPosition, textSize, paint)
                canvas.drawText("$distanceText ${round(player.distanceTraveled)}", textPosition, textSize*2, paint)
            }
            GameState.PAUSED -> {
                val healthText = resources.getString(R.string.health_text)
                val distanceText = resources.getString(R.string.distance_text)
                canvas.drawText( "$healthText ${player.health}", textPosition, textSize, paint)
                canvas.drawText("$distanceText ${round(player.distanceTraveled)}", textPosition, textSize*2, paint)

                val centerX = STAGE_WIDTH / 2.0f
                val centerY = STAGE_HEIGHT / 2.0f
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText(resources.getString(R.string.game_paused_text), centerX, centerY - textSize, paint)
                canvas.drawText(resources.getString(R.string.tap_to_continue), centerX, centerY + textSize, paint)
            }
            GameState.GAME_OVER -> {
                val centerX = STAGE_WIDTH / 2.0f
                val centerY = STAGE_HEIGHT / 2.0f
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText(resources.getString(R.string.game_over_text), centerX, centerY, paint)
                canvas.drawText(resources.getString(R.string.press_to_start_text), centerX, centerY + textSize, paint)
            }
            GameState.INITIALIZING -> {
                paint.textAlign = Paint.Align.CENTER
            }
        }
    }


    /**
     * Updates the game state by updating the player and entities like enemies, planets and stars.
     */
    private fun update() {

        isBoosting = fingerDown

        //update all objects (entities)
        player.update(isBoosting)

        player.distanceTraveled += player.velX

        // Move elements left relative to player velocity
        for (enemy in enemies){
            enemy.update(player.velX)
        }
        for (star in stars){
            star.update(player.velX)
        }
        for (planet in planets){
            planet.update(player.velX)
        }
        checkCollisions()
        checkGameOver()

        explosions.forEach { it.update() }
        explosions.removeAll { !it.isAlive() }
    }

    /**
     *  Checks if the player has reached the end of the game (is dead).
     */
    private fun checkGameOver() {
        if(player.health < 0 && currentGameState == GameState.PLAYING){
            // record longest distance traveled
            if(player.distanceTraveled > maxDistanceTraveled){
                editor.putFloat(LONGEST_DIST, player.distanceTraveled)
                editor.apply()
            }
            // stop boost sound if still looping
            if (boostStreamId != 0){
                jukebox.soundPool.stop(boostStreamId)
                boostStreamId = 0
            }

            explosions.add(Explosion(player.centerX, player.centerY))
            jukebox.play(SFX.death)
            currentGameState = GameState.GAME_OVER
            fingerDown = false
            ignoreInputUntilNextRelease = true
        }

    }

    /**
     *  Checks for collisions between the player and enemies.
     */
    private fun checkCollisions() {
        for (enemy in enemies) {
            if (isColliding(enemy, player)) {
                val tookDamage = player.applyDamage(1)
                if (tookDamage) {
                    jukebox.play(SFX.crash)
                    explosions.add(Explosion(enemy.centerX, enemy.centerY ))
                }
                enemy.onCollision(player)
            }
        }
    }


    /**
     *  Handles touch events, when the player touches the screen..
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event?.actionMasked

        when(action){
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN ->  {
                if (ignoreInputUntilNextRelease){
                    return true
                }
                if (currentGameState == GameState.PLAYING){
                    fingerDown = true
                    if(boostStreamId == 0){
                        boostStreamId = jukebox.soundPool.play(SFX.boost, 1f, 1f, 1, -1, 1f)
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                if (ignoreInputUntilNextRelease){
                    ignoreInputUntilNextRelease = false
                    fingerDown = false
                    if (boostStreamId != 0){
                        jukebox.soundPool.stop(boostStreamId)
                        boostStreamId = 0
                    }
                    return true
                }
                if (currentGameState == GameState.PLAYING){
                    fingerDown = false
                    if (boostStreamId != 0){
                        jukebox.soundPool.stop(boostStreamId)
                        boostStreamId = 0
                    }
                }else if (currentGameState == GameState.GAME_OVER){
                    restart()
                    ignoreInputUntilNextRelease = true
                    fingerDown = false
                } else if (currentGameState == GameState.PAUSED){
                    currentGameState = GameState.PLAYING
                    ignoreInputUntilNextRelease = true
                    fingerDown = false
                }
            }
        }
        return true
    }

    /**
     *  Resets the game session and restarts the game.
     */
    private fun restart() {
        explosions.clear()
        for (enemy in enemies){
            enemy.respawn()
        }
        player.respawn()
        maxDistanceTraveled = prefs.getFloat(LONGEST_DIST, 0.0f)
        jukebox.play(SFX.start)
        currentGameState = GameState.PLAYING
    }

    /**
     *  Pauses the game and stops the boost sound if still playing.
     */
    fun onPause() {
        //stop boost sound if still looping
        if (boostStreamId != 0){
            jukebox.soundPool.stop(boostStreamId)
            boostStreamId = 0
        }

        if (currentGameState == GameState.PLAYING){
            currentGameState = GameState.PAUSED
        }
    }

    fun onResume() {
        Log.d(tag, "onResume()")
    }

    fun onDestroy() {
        jukebox.destroy()
    }

    /**
     *  Called when the surface is created.
     */
    override fun surfaceCreated(p0: SurfaceHolder) {
        if (!isRunning){
            isRunning = true
            gameThread = Thread(this)
            gameThread.start()
        }

        if (currentGameState == GameState.INITIALIZING){
            restart()
        }
    }


    override fun surfaceChanged(p0: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.d(tag, "surfaceChanged(width:$width, height:$height)")
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        isRunning = false
        gameThread.join()
    }
}