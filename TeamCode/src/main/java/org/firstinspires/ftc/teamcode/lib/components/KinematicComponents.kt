package org.firstinspires.ftc.teamcode.lib.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import kotlinx.serialization.Serializable
import kotlin.math.PI

/*
    Components aren't supposed to have functions (behavior), but these functions
    only transform data from one form into another. Basically these are supposed
    to be pure math functions meaning it's alrighty!
 */
/**
 * Interface for describing basic functions for driving to certain velocity based on number of wheel rotations.
 * Note: Though it does say velocity, you could legitimately just plug in a distance to figure out the total encoder ticks to go.
 * Robot Velocity is defined with the positive X-axis facing the right side of the robot, positive Y-axis facing the front, and positive rotation being clockwise from the positive Y-axis.
 */
interface DrivetrainKinematics {
    /**
     * Translate wheel rotations per second into robot velocity
     *
     * @param wheelRadius radius of wheel
     * @param rotations pass wheel rotations through this bad boy (wheel enumeration determined by each kinematic implementation, but generally speaking the first element should be the Front-Left wheel (in a four-wheeled drivetrain) with all successive elements assigned clockwise from that wheel.
     */
    fun wheelRotationsToRobotVelocity(wheelRadius: Double, vararg rotations: Double): Vector2

    /**
     * Abstraction of [wheelRotationsToRobotVelocity] to consume a [WheelComponent]. Why? I don't know, you tell me.
     *
     * @param wheelComponent relevant [WheelComponent]
     * @param rotations pass wheel rotations through this bad boy (wheel enumeration determined by each kinematic implementation, but generally speaking the first element should be the Front-Left wheel (in a four-wheeled drivetrain) with all successive elements assigned clockwise from that wheel.
     */
    fun wheelRotationsToRobotVelocity(wheelComponent: WheelComponent, vararg rotations: Double) = wheelRotationsToRobotVelocity(wheelComponent.radius, *rotations)

    /**
     * Translate robot velocity to wheel rotations.
     *
     * @param wheelRadius radius of wheel
     * @param linearVelocity linear velocity on X-axis and Y-axis
     */
    fun robotVelocityToWheelRotations(wheelRadius: Double, linearVelocity: Vector2): List<Double>

    /**
     * Translate wheel rotations per second into robot rotational velocity
     *
     * @param wheelRadius radius of wheel
     * @param rotations pass wheel rotations through this bad boy (wheel enumeration determined by each kinematic implementation, but generally speaking the first element should be the Front-Left wheel (in a four-wheeled drivetrain) with all successive elements assigned clockwise from that wheel.
     */
    fun wheelRotationsToRotationalVelocity(wheelRadius: Double, vararg rotations: Double): Double

    /**
     * Abstraction of [wheelRotationsToRotationalVelocity] to consume a [WheelComponent]. Why? I don't know, you tell me.
     *
     * @param wheelComponent relevant [WheelComponent]
     * @param rotations pass wheel rotations through this bad boy (wheel enumeration determined by each kinematic implementation, but generally speaking the first element should be the Front-Left wheel (in a four-wheeled drivetrain) with all successive elements assigned clockwise from that wheel.
     */
    fun wheelRotationsToRotationalVelocity(wheelComponent: WheelComponent, vararg rotations: Double) = wheelRotationsToRotationalVelocity(wheelComponent.radius, *rotations)

    /**
     * Translate rotational velocity to wheel rotations.
     *
     * @param wheelRadius radius of wheel
     * @param linearVelocity linear velocity on X-axis and Y-axis
     */
    fun rotationalVelocityToWheelRotations(wheelRadius: Double, rotationalVelocity: Double): List<Double>

    /**
     * Abstraction of [rotationalVelocityToWheelRotations] to consume a [WheelComponent].
     *
     * @param wheelComponent relevant [WheelComponent]
     * @param rotationalVelocity rotational velocity in degrees
     */
    fun rotationalVelocityToWheelRotations(wheelComponent: WheelComponent, rotationalVelocity: Double) = rotationalVelocityToWheelRotations(wheelComponent.radius, rotationalVelocity)

    companion object {

        fun ticksToWheelRotations(wheelComponent: WheelComponent, encoderComponent: EncoderComponent) = ticksToWheelRotations(encoderComponent.currentPosition, wheelComponent.gearRatio, encoderComponent.TICKS_PER_REV)

        /**
         * Convert encoder ticks to wheel rotations.
         *
         * @param ticks current ticks from encoder
         * @param gearRatio gear ratio based on wheel gear over drive gear
         */
        fun ticksToWheelRotations(ticks: Int, gearRatio: Double = 1.0, ticksPerRevolution: Double) = (ticks / ticksPerRevolution) * gearRatio

        fun wheelRotationsToTicks(wheelRotations: Double, wheelComponent: WheelComponent, encoderComponent: EncoderComponent) = wheelRotationsToTicks(wheelRotations, wheelComponent.gearRatio, encoderComponent.TICKS_PER_REV)
        fun wheelRotationsToTicks(wheelRotations: Double, gearRatio: Double, ticksPerRevolution: Double) = (wheelRotations / gearRatio) * ticksPerRevolution

        fun robotVelocityToLinearVelocity(movementComponent: MovementComponent, transformComponent: TransformComponent) = movementComponent.velocity.rotate(transformComponent.rotation.toFloat())
        fun robotVelocityToLinearVelocity(robotVelocity: Vector2, robotHeading: Double) = robotVelocity.rotate(robotHeading.toFloat())
        fun robotVelocityToLinearVelocity(robotX: Double, robotY: Double, robotHeading: Double = 0.0) = Vector2(robotX.toFloat(), robotY.toFloat()).rotate(robotHeading.toFloat())
    }
}

@Serializable
data class MecanumKinematics(
        val wheelBaseLength: Double,
        val wheelBaseWidth: Double
) : DrivetrainKinematics {
    /**
     * @param rotations assumes that first element is Front-Left Wheel and every further element is assigned clockwise.
     */
    override fun wheelRotationsToRobotVelocity(wheelRadius: Double, vararg rotations: Double) = Vector2(
            ((rotations[0] - rotations[4] - rotations[2] + rotations[3]) * (wheelRadius / 4) * (2 * PI)).toFloat(),
            ((rotations[0] + rotations[4] + rotations[2] + rotations[3]) * (wheelRadius / 4) * (2 * PI)).toFloat()
    )

    override fun robotVelocityToWheelRotations(wheelRadius: Double, linearVelocity: Vector2): List<Double> =
            listOf(
                    (1 / wheelRadius) * (linearVelocity.y + linearVelocity.x),
                    (1 / wheelRadius) * (linearVelocity.y - linearVelocity.x),
                    (1 / wheelRadius) * (linearVelocity.y - linearVelocity.x),
                    (1 / wheelRadius) * (linearVelocity.y + linearVelocity.x)
            )

    /**
     * @param rotations assumes that first element is Front-Left Wheel and every further element is assigned clockwise.
     */
    override fun wheelRotationsToRotationalVelocity(wheelRadius: Double, vararg rotations: Double) =
            ((rotations[0] - rotations[4] + rotations[2] - rotations[3]) * (2 * PI) * (wheelRadius / (4.0 * (wheelBaseLength + wheelBaseWidth))))

    override fun rotationalVelocityToWheelRotations(wheelRadius: Double, rotationalVelocity: Double): List<Double> =
            listOf(
                    (1 / wheelRadius) * ((wheelBaseLength + wheelBaseWidth) * rotationalVelocity) * (2 * PI),
                    (1 / wheelRadius) * (-(wheelBaseLength + wheelBaseWidth) * rotationalVelocity) * (2 * PI),
                    (1 / wheelRadius) * ((wheelBaseLength + wheelBaseWidth) * rotationalVelocity) * (2 * PI),
                    (1 / wheelRadius) * (-(wheelBaseLength + wheelBaseWidth) * rotationalVelocity) * (2 * PI)
            )
}

/**
 * Simple container for a [DrivetrainKinematics].
 */
data class KinematicComponent(
        val kinematics: DrivetrainKinematics
) : Component