package com.example.myspaceshooter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.random.Random

/**
 * This class represents a star in the games background.
 */
class Star : Entity() {

    private val radius = Random.nextInt(3, 7).toFloat()
    private val layer = Random.nextInt(1, 4)
    private val baseSpeed = -when (layer) {
        1 -> Random.nextInt(2, 4)
        2 -> Random.nextInt(3, 6)
        else -> Random.nextInt(5, 8)
    }.toFloat()
    private val parallax = when (layer) { 1 -> 0.3f; 2 -> 0.6f; else -> 1.0f }
    private val hasDiagonals = Random.nextFloat() < 0.50f   // 35% of stars get diagonals
    private var twinkleCounter = Random.nextInt(0, 60)

    init {
        x = RNG.nextInt(STAGE_WIDTH).toFloat()
        y = RNG.nextInt(STAGE_HEIGHT).toFloat()
        velX = baseSpeed
    }

    /**
     * Updates the star's position and velocity.
     */
    fun update(playerVel: Float) {
        super.update()
        x -= playerVel * parallax

        if (right < 0f) {
            left = STAGE_WIDTH.toFloat()
            centerY = RNG.nextInt(STAGE_HEIGHT).toFloat()
        }
        twinkleCounter = (twinkleCounter + 1) % 60
    }

    /**
     * Draws the star on the canvas.
     */
    override fun render(canvas: Canvas, paint: Paint) {
        val oldColor = paint.color
        val oldAlpha = paint.alpha
        val oldStyle = paint.style
        val oldStroke = paint.strokeWidth

        paint.color = Color.YELLOW
        paint.alpha = if (twinkleCounter < 30) 255 else 200 // blink
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = (radius / 3f).coerceAtLeast(1f)
        val h = radius

        canvas.drawLine(x, y - h, x, y + h, paint)
        canvas.drawLine(x - h, y, x + h, y, paint)

        if (hasDiagonals) {
            canvas.drawLine(x - h, y - h, x + h, y + h, paint)
            canvas.drawLine(x - h, y + h, x + h, y - h, paint)
        }

        paint.color = oldColor
        paint.alpha = oldAlpha
        paint.style = oldStyle
        paint.strokeWidth = oldStroke
    }
}