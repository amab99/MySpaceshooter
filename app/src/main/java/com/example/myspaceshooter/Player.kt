package com.example.myspaceshooter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint

const val PLAYER_HEIGHT = 100
class Player(game: Game) : Entity() {

    private val bitmap = createScaledBitmap(game, R.drawable.player)

    init {
        width = bitmap.width.toFloat()
        height = bitmap.height.toFloat()
    }

    private fun createScaledBitmap(game:Game, redId: Int) : Bitmap{
        val original = BitmapFactory.decodeResource(game.resources, redId)
        val ratio = PLAYER_HEIGHT.toFloat() / original.height
        val newH = (original.height * ratio).toInt()
        val newW = (original.width * ratio).toInt()
        return Bitmap.createScaledBitmap(original, newW, newH, true)
    }

    override fun render(canvas: Canvas, paint: Paint) {
        super.render(canvas, paint)
        canvas.drawBitmap(bitmap, x, y, paint)
    }
}