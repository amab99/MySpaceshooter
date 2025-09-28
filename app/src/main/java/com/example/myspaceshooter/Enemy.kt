package com.example.myspaceshooter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import androidx.core.graphics.scale

/**
 * This class represents the enemy in the game, the enemy scrolls from right to left.
 */
const val ENEMY_HEIGHT = 50
class Enemy(game: Game) : Entity() {

    private val bitmap : Bitmap

    init {
        var id = R.drawable.tm_2
        when(RNG.nextInt(6)){
            0 -> id = R.drawable.tm_2
            1 -> id = R.drawable.tm_3
            2 -> id = R.drawable.tm_4
            3 -> id = R.drawable.tm_5
            4 -> id = R.drawable.tm_6
            5 -> id = R.drawable.tm_7
        }
        bitmap = createScaledBitmap(game, id)
        width = bitmap.width.toFloat()
        height = bitmap.height.toFloat()
        respawn()
    }

    /**
     * Function used when collision with the player, the enemy respawns.
     */
    override fun onCollision(that: Entity) {
        super.onCollision(that)
        respawn()
    }

    /**
     * Function used to create a scaled bitmap for the enemy.
     */
    private fun createScaledBitmap(game:Game, redId: Int) : Bitmap{
        val original = BitmapFactory.decodeResource(game.resources, redId)
        val ratio = ENEMY_HEIGHT.toFloat() / original.height
        val newH = (original.height * ratio).toInt()
        val newW = (original.width * ratio).toInt()
        val scaled = original.scale(newW, newH)
        return flipVertically(scaled)
    }

    /**
     * Function to flip the bitmap around its center.
     */
    fun flip(src: Bitmap, horizontally : Boolean) : Bitmap{
        val matrix = Matrix()
        val cx = src.width / 2f
        val cy = src.height / 2f
        if (horizontally){
            matrix.postScale(1f, -1f, cx, cy)
        }else{
            matrix.postScale(-1f, 1f, cx, cy)
        }
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

    /**
     * Helper to flip the bitmap vertically.
     */
    fun flipVertically(src:Bitmap) = flip(src, horizontally = false)

    /**
     * Helper to flip the bitmap horizontally.
     */
    fun flipHorizontally(src:Bitmap) = flip(src, horizontally = true)

    /**
     * Function to update the enemy's position.
     * Moving the enemy left relative to player velocity and respawning when off screen.
     */
    fun update(playerVelocity: Float) {
        super.update()
        x -= playerVelocity
        if(right < 0){
            respawn()
        }
    }

    /**
     * Function to respawn the enemy.
     */
    fun respawn(){
        left = STAGE_WIDTH.toFloat() + RNG.nextInt(STAGE_WIDTH)
        centerY = RNG.nextInt(STAGE_HEIGHT).toFloat()
    }

    /**
     * function draws the enemy on the canvas.
     */
    override fun render(canvas: Canvas, paint: Paint) {
        super.render(canvas, paint)
        canvas.drawBitmap(bitmap, x, y, paint)
    }
}