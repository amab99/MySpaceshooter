package com.example.myspaceshooter

import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log

abstract class Entity {
    private val tag = "Entity"
    var x = 0f
    var y = 0f
    var width = 0f
    var height = 0f
    var velX = 0f
    var velY = 0f

    init {
        Log.d(tag, "Entity created")
    }

    open fun update() {}
    open fun render(canvas: Canvas, paint: Paint) {}
    open fun onCollision(that: Entity) {} // Notify the Entity about collisions

    var left: Float
        get() = x
        set(value) {
            x = value
        }
    var right: Float
        get() = x + width
        set(value) {
            x = value - width
        }
    var top: Float
        get() = y
        set(value) {
            y = value
        }
    var bottom: Float
        get() = y + height
        set(value) {
            y = value - height
        }
    var centerX: Float
        get() = x + (width * 0.5f)
        set(value) {
            x = value - (width * 0.5f)
        }
    var centerY: Float
        get() = y + (height * 0.5f)
        set(value) {
            y = value - (height * 0.5f)
        }
}

//a basic axis-aligned bounding box intersection test.
//see: https://gamedev.stackexchange.com/questions/586/what-is-the-fastest-way-to-work-out-2d-bounding-box-intersection
fun isColliding(a: Entity, b: Entity): Boolean {
    return !(a.right <= b.left || b.right <= a.left || a.bottom <= b.top || b.bottom <= a.top)
}