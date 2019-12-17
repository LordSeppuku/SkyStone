package org.firstinspires.ftc.teamcode.legacy.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.legacy.lib.Button
import org.firstinspires.ftc.teamcode.legacy.lib.ButtonState

val Boolean.Int
    get() = if (this) 1 else 0

class Arm {

    private lateinit var hardwareMap: HardwareMap

    private val servo: Servo by lazy {
        hardwareMap.servo["srvArm"]
    }
    private val lift: DcMotor by lazy {
        hardwareMap.dcMotor["dcLift"]
    }

    private val arm: DcMotor by lazy {
        hardwareMap.dcMotor["dcArm"]
    }

    private val clasp = Button()
    private val unclasp = Button()

    fun init(hwMap: HardwareMap) {
        this.hardwareMap = hwMap

        lift.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        arm.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        servo.direction = Servo.Direction.REVERSE
        servo.position = 0.15
    }

    fun update(gamepad: Gamepad) {
        with(gamepad) {
            lift.power = (dpad_up.Int - dpad_down.Int).toDouble()
            arm.power = (left_trigger - right_trigger) * 0.5

            if (clasp.update(right_bumper) != ButtonState.NOT_PRESSED) {
                servo.position = 0.15
            } else if (unclasp.update(left_bumper) != ButtonState.NOT_PRESSED) {
                servo.position = 0.0
            }
        }
    }

    fun graspAndDeploy() {

    }

}