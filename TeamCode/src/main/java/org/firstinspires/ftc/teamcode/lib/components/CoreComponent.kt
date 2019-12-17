package org.firstinspires.ftc.teamcode.lib.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

data class TransformComponent(
        val position: Vector2 = Vector2(),
        val rotation: Double = 0.0
) : Component

data class MovementComponent(
        val velocity: Vector2 = Vector2(),
        val acceleration: Vector2 = Vector2()
) : Component

data class RotationalMovementComponent(
        val velocity: Double = 0.0,
        val acceleration: Double = 0.0
) : Component