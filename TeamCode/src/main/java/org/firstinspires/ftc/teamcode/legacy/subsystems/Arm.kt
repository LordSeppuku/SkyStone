package org.firstinspires.ftc.teamcode.legacy.subsystems

import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.teamcode.legacy.lib.Button
import org.firstinspires.ftc.teamcode.legacy.lib.ButtonState

val Boolean.toInt
    get() = if (this) 1 else 0

class Arm(private val hwMap: HardwareMap) {

    private val servo: Servo by lazy {
        hwMap.servo["srvArm"]
    }
    private val lift: DcMotor by lazy {
        hwMap.dcMotor["dcLift"]
    }

    private val arm: DcMotor by lazy {
        hwMap.dcMotor["dcArm"]
    }

    private val clasp = Button()
    private val unclasp = Button()

    fun init() {
        lift.mode = DcMotor.RunMode.RUN_USING_ENCODER
        lift.direction = DcMotorSimple.Direction.REVERSE
        lift.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        arm.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        servo.direction = Servo.Direction.REVERSE
        servo.position = 0.1
    }

    fun update(gamepad: Gamepad) {
        with(gamepad) {
            lift.power = (dpad_up.toInt - dpad_down.toInt).toDouble()
            arm.power = (-left_trigger + right_trigger) * 0.5

            if (clasp.update(right_bumper) != ButtonState.NOT_PRESSED) {
                servo.position = 0.1
            } else if (unclasp.update(left_bumper) != ButtonState.NOT_PRESSED) {
                servo.position = 0.0
            }
        }
    }

    fun graspAndDeploy() {

    }

}