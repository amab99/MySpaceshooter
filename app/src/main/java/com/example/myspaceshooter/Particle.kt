package com.example.myspaceshooter

import android.graphics.Canvas
import android.graphics.Paint

/**
 * Class that represents a particle in the explosion effect.
 */
class Particle( var x: Float, var y: Float, private var velX: Float, private var velY: Float,
                private var life: Int) {
    var alive = true

    /**
     * Updates the particle's position and decrease its life.
     */
    fun update() {
        if (!alive) return
        x += velX
        y += velY
        life--
        if (life <= 0) alive = false
    }

    /**
     * Draws the particle as a fading circle on the canvas.
     */
    fun draw(canvas: Canvas, paint: Paint) {
        if (!alive) return
        paint.alpha = (life * 9).coerceIn(0, 255)
        canvas.drawCircle(x, y, 5f, paint)
    }
}