package org.firstinspires.ftc.teamcode.lib.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable

/**
 * Defines a position and rotation in field coordinates.
 *
 * @property position Vector2 describing position based on field coordinates, where (0,0) equals middle of field with Y-axis positive direction moving away from audience (shown in Game Manual) and X-axis positive direction moving towards Red Alliance station, with the measurement unit is Inches.
 * @property rotation Degree value describing rotation of [Entity] based on field coordinates (if transformed on top of field origin, difference from 0 degrees i.e. direction of Y-axis with positive equaling clockwise)
 */
@Serializable
data class TransformComponent(
        val position: @ContextualSerialization Vector2 = Vector2.Zero,
        var rotation: Double = 0.0
) : Component

/**
 * Defines a velocity as well as acceleration in terms of 2D vectors and field coordinates.
 *
 * @property velocity Vector2 describing linear velocity based on field coordinates (measurement unit is Inches)
 * @property acceleration Vector2 describing linear acceleration based on field coordinates (measurement unit is Inches)
 */
@Serializable
data class MovementComponent(
        val velocity: @ContextualSerialization Vector2 = Vector2.Zero,
        val acceleration: @ContextualSerialization Vector2 = Vector2.Zero
) : Component

/**
 * Similar to [MovementComponent], but for Rotations described using [Double]
 *
 * @property velocity rotational velocity in terms of Degrees per Second
 * @property acceleration rotational acceleration in terms of Degrees per Second per Second (i.e. change in acceleration with respect to time)
 */
@Serializable
data class RotationalMovementComponent(
        var velocity: Double = 0.0,
        var acceleration: Double = 0.0
) : Component

/**
 * Combination of [MovementComponent] and [RotationalMovementComponent] applied to robot frame.
 *
 * @property velocity Vector2 describing linear velocity based on field coordinates (measurement unit is Inches)
 * @property acceleration Vector2 describing linear acceleration based on field coordinates (measurement unit is Inches)
 * @property rotationalVelocity rotational velocity in terms of Degrees per Second
 * @property rotationalAcceleration rotational acceleration in terms of Degrees per Second per Second (i.e. change in acceleration with respect to time)
 */
@Serializable
data class RobotMovementComponent(
        val velocity: @ContextualSerialization Vector2 = Vector2.Zero,
        val acceleration: @ContextualSerialization Vector2 = Vector2.Zero,
        var rotationalVelocity: Double = 0.0,
        var rotationalAcceleration: Double = 0.0
) : Component