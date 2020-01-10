package org.firstinspires.ftc.teamcode.lib.components

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo

/**
 *  A component representing the utmost necessary data required from a DcMotor.
 *
 *  Encoder functionality has been abstracted to its own class in order to ensure modularity, because sometimes, you don't need encoders.
 *  RunMode access has been abstracted way and any entity that has both this component and an encoder will have the motor it is representing be set to [DcMotor.RunMode.RUN_USING_ENCODER]
 *
 *  @property power the current power of the motor
 *  @property direction the current direction of the motor
 *  @property zeroPowerBehavior the current ZeroPowerBehavior (i.e. what the motor does when given zero power: resist against movement -> BRAKE, do not resist -> FLOAT
 */
data class DcMotorComponent(
        override val deviceString: String,
        var power: Double = 0.0,
        var direction: DcMotorSimple.Direction = DcMotorSimple.Direction.FORWARD,
        var zeroPowerBehavior: DcMotor.ZeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
) : HardwareComponent

/**
 * Component representing all necessary sensor values from encoder.
 *
 * @property currentPosition current position of encoder
 * @property targetPosition target position of encoder
 * @property currentVelocity current velocity (in encoder ticks per second) of encoder
 * @property targetVelocity target velocity (in encoder ticks per second) of encoder
 * @property runToTargetPosition should this motor move towards target position.
 * @property direction direction of encoder (TODO: Figure out if encoder values are affected by directionality, pretty dang certain.)
 * @property TICKS_PER_REV how many ticks it takes for the encoder to go one full revolution (is Double because some motors have whack gear ratios)
 */
data class EncoderComponent(
        override val deviceString: String,
        var currentPosition: Int = 0,
        var targetPosition: Int = 0,
        var currentVelocity: Double = 0.0,
        var targetVelocity: Double = 0.0,
        var runToTargetPosition: Boolean = false,
        var direction: DcMotorSimple.Direction = DcMotorSimple.Direction.FORWARD,
        var TICKS_PER_REV: Double = 0.0
) : HardwareComponent

/**
 * Component representing a Servo hardware device.
 *
 * @property position current position of servo
 * @property direction current direction of servo
 */
data class ServoComponent(
        override val deviceString: String,
        var position: Double = 0.0,
        var direction: Servo.Direction = Servo.Direction.FORWARD
) : HardwareComponent

/**
 * Component representing a Continuous Rotation Servo hardware device.
 *
 * @property power current position of servo
 * @property direction current direction of servo
 */
data class CRServoComponent(
        override val deviceString: String,
        var power: Double = 0.0,
        var direction: DcMotorSimple.Direction = DcMotorSimple.Direction.FORWARD
) : HardwareComponent