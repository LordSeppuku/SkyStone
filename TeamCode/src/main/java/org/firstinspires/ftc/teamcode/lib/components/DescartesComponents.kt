package org.firstinspires.ftc.teamcode.lib.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.CatmullRomSpline
import com.badlogic.gdx.math.Path
import com.badlogic.gdx.math.Vector2

/**
 * Defines a position and rotation in field coordinates.
 *
 * @property position Vector2 describing position based on field coordinates, where (0,0) equals middle of field with Y-axis positive direction moving away from audience (shown in Game Manual) and X-axis positive direction moving towards Blue Alliance station, with the measurement unit is Inches.
 * @property rotation Degree value describing rotation of [Entity] where the rotation is determined by the angle between Xe and Xf, with positive being a counter-clockwise direction.
 */

data class TransformComponent(
        val position: Vector2 = Vector2(0f, 0f),
        var rotation: Double = 0.0
) : Component

/**
 * Defines a velocity as well as acceleration in terms of 2D vectors and field coordinates.
 *
 * @property velocity Vector2 describing linear velocity based on field coordinates (measurement unit is Inches)
 * @property acceleration Vector2 describing linear acceleration based on field coordinates (measurement unit is Inches)
 */

data class MovementComponent(
        val velocity: Vector2 = Vector2.Zero,
        val acceleration: Vector2 = Vector2.Zero
) : Component

/**
 * Similar to [MovementComponent], but for Rotations described using [Double]
 *
 * @property velocity rotational velocity in terms of Degrees per Second
 * @property acceleration rotational acceleration in terms of Degrees per Second per Second (i.e. change in acceleration with respect to time)
 */

data class RotationalMovementComponent(
        var velocity: Double = 0.0,
        var acceleration: Double = 0.0
) : Component

/**
 * [MovementComponent] as applied to a frame other than field.
 *
 * @property velocity Vector2 describing linear velocity based on robot coordinates (measurement unit is Inches)
 * @property acceleration Vector2 describing linear acceleration based on robot coordinates (measurement unit is Inches)*/

data class LocalizedMovementComponent(
        val velocity: Vector2 = Vector2.Zero,
        val acceleration: Vector2 = Vector2.Zero
) : Component

/**
 * Simple container for a [Path] in field coordinates.
 */
class PathComponent(
        var path: Path<Vector2> = CatmullRomSpline<Vector2>()
) : Component

data class PurePursuitComponent(
        val speedScalar: Double = 1.0,
        val lookAheadRadius: Double = 12.0,
        val increaseFailSafe: Boolean = true
) : Component