package com.example.myspaceshooter

import android.graphics.Canvas
import android.graphics.Paint
import kotlin.random.Random

/**
 * Class that represents an explosion effect that should be displayed
 * when player hits an enemy.
 */
class Explosion(private val x: Float, private val y: Float) {

    private var alive = true

    /**
     * A list of particles that make up the explosion effect.
     */
    private val particles = MutableList(22) { Particle(x = x, y = y,
        velX = Random.nextFloat() * 7f - 3.5f, velY = Random.nextFloat() * 7f - 3.5f,
        life = 28 + Random.nextInt(18)
        )
    }

    /**
     * Function to update the explosion effect by updating the particles.
     */
    fun update() {
        if (!alive) return
        particles.forEach { it.update() }
        particles.removeAll { !it.alive }
        if (particles.isEmpty()) alive = false
    }

    /**
     * Function that draws the explosion effect on the canvas.
     * This by drawing the particles as fading circles.
     */
    fun draw(canvas: Canvas, paint: Paint) {
        if (!alive) return
        val oldAlpha = paint.alpha
        particles.forEach { it.draw(canvas, paint) }
        paint.alpha = oldAlpha
    }

    fun isAlive(): Boolean = alive
}