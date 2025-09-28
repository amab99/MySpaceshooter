package com.example.myspaceshooter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

/**
 *  Class that represents planet in the games.
 */
class Planet(private val bitmap: Bitmap, layer: Int) : Entity() {

    private val parallax = when (layer) { 1 -> 0.2f; 2 -> 0.45f; else -> 0.7f }
    private val baseSpeed = -when (layer) { 1 -> 1f; 2 -> 1.5f; else -> 2.2f }

    init {
        x = RNG.nextInt(STAGE_WIDTH).toFloat()
        y = RNG.nextInt((STAGE_HEIGHT * 0.75f).toInt()).toFloat() + STAGE_HEIGHT * 0.1f // avoid top edge
        velX = baseSpeed
    }

    /**
     *  Updates the planet's position and speed, it scrolls left relative to player velocity.
     */
    fun update(playerVelocity: Float) {
        super.update()
        x -= playerVelocity * parallax

        // wrap to the right when off-screen left
        if (right < -bitmap.width) {
            left = STAGE_WIDTH + bitmap.width.toFloat()
            centerY = RNG.nextInt(STAGE_HEIGHT).toFloat()
        }
    }

    /**
     *  Draws the planet on the canvas.
     */
    override fun render(canvas: Canvas, paint: Paint) {
        val halfW = bitmap.width / 2f
        val halfH = bitmap.height / 2f
        canvas.drawBitmap(bitmap, x - halfW, y - halfH, paint)
    }

    override fun onCollision(that: Entity) { /* background decoration only */ }
}