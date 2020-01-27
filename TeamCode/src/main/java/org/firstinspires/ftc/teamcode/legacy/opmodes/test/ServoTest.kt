package org.firstinspires.ftc.teamcode.legacy.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.legacy.lib.Button
import org.firstinspires.ftc.teamcode.legacy.lib.ButtonState

@TeleOp(name = "Servo Test", group = "Test")
class ServoTest : LinearOpMode() {
    override fun runOpMode() {

        val servo = hardwareMap.servo.get("srvArm")
        var testPos = 0.0
        var tgtPos = 0.0

        val up = Button()
        val down = Button()
        val A = Button()
        val B = Button()
        val X = Button()
        val Y = Button()

        waitForStart()

        while (opModeIsActive()) {
            telemetry.clear()

            telemetry.addLine("Press A to reset to zero, X for neg position, B for pos position, Y to reverse")
            telemetry.addLine("Dpad UP increases target position, Dpad DOWN opposite")

            with(gamepad1) {

                if (up.update(dpad_up) == ButtonState.PRESSED) testPos += 0.05
                if (down.update(dpad_down) == ButtonState.PRESSED) testPos -= 0.05

                if (A.update(a) == ButtonState.PRESSED) tgtPos = 0.0
                if (B.update(b) == ButtonState.PRESSED) tgtPos = testPos
                if (X.update(x) == ButtonState.PRESSED) tgtPos = -testPos
                if (Y.update(y) == ButtonState.PRESSED) {
                    if (servo.direction == Servo.Direction.FORWARD) {
                        servo.direction = Servo.Direction.REVERSE
                    } else servo.direction = Servo.Direction.FORWARD
                }
            }

            telemetry.addData("Test Position: ", testPos)

            servo.position = tgtPos

            telemetry.addData("Servo position: ", servo.position)
            telemetry.addData("Servo direction: ", servo.direction)

            telemetry.update()
        }
    }
}