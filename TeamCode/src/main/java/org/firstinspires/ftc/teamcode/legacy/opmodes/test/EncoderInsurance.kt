package org.firstinspires.ftc.teamcode.RogueOpModes.Autonomous

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor.RunMode
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.teamcode.lib.abstractions.ButtonState

@TeleOp(name = "Encoder Insurance", group = "Test")
class EncoderInsurance : LinearOpMode() {
    override fun runOpMode() {
        val motors = listOf("fl", "fr", "bl", "br")
        var aState = ButtonState.UNPRESSED
        var bState = ButtonState.UNPRESSED
        var currentIndex = 0
        waitForStart()
        while (opModeIsActive()) {
            aState = aState.update(gamepad1.a)
            bState = bState.update(gamepad1.b)
            if (aState == ButtonState.PRESSED) {
                currentIndex += 1
                if (currentIndex > 3) currentIndex = 0
            }
            telemetry.addLine().addData("Current Motor: ", motors[currentIndex])
            (hardwareMap.dcMotor.get(motors[currentIndex]) as DcMotorEx).apply {
                mode = RunMode.RUN_USING_ENCODER
                power = -gamepad1.left_stick_y.toDouble()
                telemetry.addLine().addData("Power: ", power)
                        .addData("Current Pos: ", currentPosition)
                        .addData("Current Vel in DPS: ", getVelocity(AngleUnit.DEGREES))
                if (bState == ButtonState.PRESSED) mode = RunMode.STOP_AND_RESET_ENCODER; sleep(50)
            }
            telemetry.update()
        }

    }
}