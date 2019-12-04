package org.firstinspires.ftc.teamcode.legacy.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.legacy.lib.Button
import org.firstinspires.ftc.teamcode.legacy.lib.ButtonState

class Arm {

    private lateinit var hardwareMap: HardwareMap

    private lateinit var servo: Servo

    private lateinit var motor: DcMotor

    private val clasp = Button()
    private val unclasp = Button()

    fun init(hwMap: HardwareMap) {
        this.hardwareMap = hwMap

        servo = hardwareMap.servo["srvArm"]
        motor = hardwareMap.dcMotor["dcArm"]

        motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        servo.direction = Servo.Direction.REVERSE
        servo.position = 0.15
    }

    fun update(gamepad: Gamepad, telemetry: Telemetry) {
        with(gamepad) {
            motor.power = (left_trigger - right_trigger) * 0.1875

            if (clasp.update(right_bumper) != ButtonState.NOT_PRESSED) {
                servo.position = 0.25
            } else if (unclasp.update(left_bumper) != ButtonState.NOT_PRESSED) {
                servo.position = 0.15
            } else {

            }
        }
    }

}