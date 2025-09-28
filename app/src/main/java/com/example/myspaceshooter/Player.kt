package com.example.myspaceshooter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.SystemClock.uptimeMillis
import androidx.core.math.MathUtils.clamp
import kotlin.math.absoluteValue
import androidx.core.graphics.scale

const val PLAYER_HEIGHT = 120
const val GRAVITY = 0.5f
const val DRAG = 0.97f
const val ACCELERATION = 0.8f //on the x axis
const val BOOST_FORCE = -0.8f //on the y axis
const val MAX_VEL= 20f
const val VELOCITY_EPSILON = -0.01f //small threshold for snapping to 0
const val PLAYER_STARTING_HEALTH = 3
const val PLAYER_MARGIN_X = 20f

/**
 * This class represents the player space ship.
 */
class Player(game: Game) : Entity() {

    private val bitmap = createScaledBitmap(game, R.drawable.player)
    var health = PLAYER_STARTING_HEALTH
    var distanceTraveled = 0f

    private val invulnMs = 1000L
    private var invulnerableUntil = 0L
    private val flickerIntervalMs = 80L

    init {
        width = bitmap.width.toFloat()
        height = bitmap.height.toFloat()
        respawn()
    }

    /**
     *  Resets the player to its initial state.
     */
    fun respawn(){
        distanceTraveled = 0f
        x = PLAYER_MARGIN_X
        health = PLAYER_STARTING_HEALTH
        centerY = STAGE_HEIGHT / 2.0f
        velX = 0f
        velY = 0f
        invulnerableUntil = 0L
    }

    /**
     *  Checks if the player is invulnerable.
     */
    fun isInvulnerable(now: Long = uptimeMillis()): Boolean = now < invulnerableUntil

    /**
     *  Applies damage to the player if not invulnerable.
     */
    fun applyDamage(amount: Int = 1, now: Long = uptimeMillis()): Boolean {
        if (isInvulnerable(now)){
            return false
        }
        health -= amount
        invulnerableUntil = now + invulnMs
        return true
    }


    override fun onCollision(that: Entity) {
        super.onCollision(that)
        health--
    }

    /**
     *  Creates a scaled bitmap for the player.
     */
    private fun createScaledBitmap(game:Game, redId: Int) : Bitmap{
        val original = BitmapFactory.decodeResource(game.resources, redId)
        val ratio = PLAYER_HEIGHT.toFloat() / original.height
        val newH = (original.height * ratio).toInt()
        val newW = (original.width * ratio).toInt()
        return original.scale(newW, newH)
    }

    /**
     *  Updates the player's position and velocity.
     */
    fun update(isBoosting: Boolean) {
        super.update()
        applyDrag()
        applyGravity()

        if (isBoosting) {
            applyBoost()
        }
        y += velY
        distanceTraveled += velX

        if (y < 0){
            y = 0f
            velY = 0f
        }

        if(bottom > STAGE_HEIGHT) {
            y = STAGE_HEIGHT - height
            velY = 0f
        }
    }

    /**
     * Applies boost to the player.
     */
    private fun applyBoost() {
        velX += ACCELERATION
        velY += BOOST_FORCE
        velX = clamp(velX, 0f, MAX_VEL)
    }

    /**
     *  Applies gravity to the player and clamp the velocity.
     **/
    private fun applyGravity() {
        velY += GRAVITY
        velY = clamp(velY, -MAX_VEL, MAX_VEL)
    }

    /**
     *  Applies drag to the player.
     */
    private fun applyDrag() {
        velX *= DRAG
        velY *= DRAG
        if (velX.absoluteValue < VELOCITY_EPSILON) velX = 0f
        if (velY.absoluteValue < VELOCITY_EPSILON) velY = 0f

        if (velX < 1f) velX = 1f
    }

    /**
     *  Draws the player space ship on the canvas.
     */
    override fun render(canvas: Canvas, paint: Paint) {
        super.render(canvas, paint)

        val now = uptimeMillis()
        val invulnActive = isInvulnerable(now)
        val oldAlpha = paint.alpha
        val oldStyle = paint.style
        val oldStroke = paint.strokeWidth
        val oldColor = paint.color
        val oldAA = paint.isAntiAlias

        paint.isAntiAlias = true

        // Draw the player bitmap
        if (invulnActive) {
            val blinkOn = ((now / flickerIntervalMs) % 2L == 0L)
            paint.alpha = if (blinkOn) 255 else 130
        } else {
            paint.alpha = 255
        }
        canvas.drawBitmap(bitmap, x, y, paint)

        // Draw a shield ring on top while invulnerable
        if (invulnActive) {
            val cx = centerX
            val cy = centerY
            val radius = (kotlin.math.max(width, height) * 0.60f)

            val pulse = if (((now / 120L) % 2L) == 0L) 1.00f else 1.12f

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 6f
            paint.color = android.graphics.Color.CYAN
            paint.alpha = 230
            canvas.drawCircle(cx, cy, radius * pulse, paint)

            paint.strokeWidth = 3f
            paint.alpha = 160
            canvas.drawCircle(cx, cy, radius * 0.82f, paint)
        }

        // Restore paint state
        paint.alpha = oldAlpha
        paint.style = oldStyle
        paint.strokeWidth = oldStroke
        paint.color = oldColor
        paint.isAntiAlias = oldAA
    }
}