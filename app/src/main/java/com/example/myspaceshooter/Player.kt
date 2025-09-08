package com.example.myspaceshooter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.math.MathUtils.clamp
import kotlin.math.absoluteValue

const val PLAYER_HEIGHT = 75
const val GRAVITY = 0.5f
const val DRAG = 0.97f
const val ACCELERATION = 0.8f //on the x axis
const val BOOST_FORCE = -0.8f //on the y axis
const val MAX_VEL= 20f
const val VELOCITY_EPSILON = -0.01f //small threshold for snapping to 0

class Player(game: Game) : Entity() {

    private val bitmap = createScaledBitmap(game, R.drawable.player)

    init {
        width = bitmap.width.toFloat()
        height = bitmap.height.toFloat()
        x = 30f
    }

    private fun createScaledBitmap(game:Game, redId: Int) : Bitmap{
        val original = BitmapFactory.decodeResource(game.resources, redId)
        val ratio = PLAYER_HEIGHT.toFloat() / original.height
        val newH = (original.height * ratio).toInt()
        val newW = (original.width * ratio).toInt()
        return Bitmap.createScaledBitmap(original, newW, newH, true)
    }

    fun update(isBoosting: Boolean) {
        super.update()
        applyDrag()
        applyGravity()

        if (isBoosting) {
            applyBoost()
        }
        y += velY

        if(bottom > STAGE_HEIGHT) {
            bottom = STAGE_HEIGHT.toFloat()
            velY = 0f
        }
    }

    private fun applyBoost() {
        velX += ACCELERATION
        velY += BOOST_FORCE
        velX = clamp(velX, 0f, MAX_VEL)
    }

    private fun applyGravity() {
        velY += GRAVITY
        velY = clamp(velY, -MAX_VEL, MAX_VEL)
    }

    private fun applyDrag() {
        velX *= DRAG
        velY *= DRAG
        if (velX.absoluteValue < VELOCITY_EPSILON) velX = 0f
        if (velY.absoluteValue < VELOCITY_EPSILON) velY = 0f
    }

    override fun render(canvas: Canvas, paint: Paint) {
        super.render(canvas, paint)
        canvas.drawBitmap(bitmap, x, y, paint)
    }
}