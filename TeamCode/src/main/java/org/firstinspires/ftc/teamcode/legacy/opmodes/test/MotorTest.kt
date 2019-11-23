package org.firstinspires.ftc.teamcode.legacy.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.legacy.lib.Button
import org.firstinspires.ftc.teamcode.legacy.lib.ButtonState

@TeleOp(name = "Motor Test", group = "Test")
class MotorTest: LinearOpMode() {

    override fun runOpMode() {
        waitForStart()
        val motorIterator = hardwareMap.dcMotor.iterator()
        var activeMotor = motorIterator.next()
        val aButton = Button()

        while (opModeIsActive()) {
            hardwareMap.dcMotor.get("fl").power = gamepad1.left_stick_y.toDouble()
        }

    }

}