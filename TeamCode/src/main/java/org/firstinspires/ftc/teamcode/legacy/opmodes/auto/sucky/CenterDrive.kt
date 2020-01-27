package org.firstinspires.ftc.teamcode.legacy.opmodes.auto.sucky

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.legacy.lib.*
import org.firstinspires.ftc.teamcode.legacy.subsystems.MecanumDrivetrain

@Disabled
@Autonomous(name = "Center Drive", group = "bad")
class CenterDrive: LinearOpMode() {

    val drivetrain: MecanumDrivetrain = MecanumDrivetrain(hardwareMap)
    val runtime: ElapsedTime = ElapsedTime()

    val leftCommands = listOf(
            DriveToPositionCommand(Distance(0.0), Distance(-36.0), Rotation(0.0), 1.0)
    )

    val rightCommands = listOf(
            DriveToPositionCommand(Distance(0.0), Distance(36.0), Rotation(0.0), 1.0)
    )

    override fun runOpMode() {

        drivetrain.init()
        val commands = selectionPrompt()

        if (isStopRequested) return

        waitForStart()
        runtime.reset()

        for (command in commands) {
            drivetrain.driveToPosition(command)(::opModeIsActive) {
                telemetry.addLine("DRIVING")
                telemetry.update()
            }
        }

        telemetry.clear()
        telemetry.addLine("AAAA")
        telemetry.update()

    }

    fun selectionPrompt(): List<DriveToPositionCommand> {
        val intro = """
            USE THE LEFT AND RIGHT DPAD BUTTONS FOR SELECTION
            ================================================
            LEFT: GO LEFT
            RIGHT: GO RIGHT
        """.trimIndent()

        val lOutro = """
            YOU HAVE SELECTED LEFT COMMANDS
            ===============================
            RIGHT: ACCEPT
            LEFT: RETURN
        """.trimIndent()

        val rOutro = """
            YOU HAVE SELECTED RIGHT COMMANDS
            ================================
            RIGHT: ACCEPT
            LEFT: RETURN
        """.trimIndent()

        val left = Button()
        val right = Button()

        start@ while (!isStopRequested) {
            telemetry.clear()
            telemetry.addLine(intro)
            telemetry.update()

            while (left.update(gamepad1.dpad_left) == ButtonState.NOT_PRESSED
                    && right.update(gamepad1.dpad_right) == ButtonState.NOT_PRESSED
                    && opModeIsActive()) {
            }

            telemetry.clear()
            telemetry.update()

            if (left.state != ButtonState.NOT_PRESSED) {
                telemetry.addLine(lOutro)
                telemetry.update()

                while (right.update(gamepad1.dpad_right) != ButtonState.PRESSED && !isStopRequested) {
                    if (left.update(gamepad1.dpad_left) == ButtonState.PRESSED && !isStopRequested) {
                        continue@start
                    }
                }

                telemetry.apply {
                    clear()
                    addLine("Left is selected")
                    update()
                }

                return leftCommands
            } else if (right.state != ButtonState.NOT_PRESSED) {
                telemetry.addLine(rOutro)
                telemetry.update()

                while (right.update(gamepad1.dpad_right) != ButtonState.PRESSED && !isStopRequested) {
                    if (left.update(gamepad1.dpad_left) == ButtonState.PRESSED && !isStopRequested) {
                        continue@start
                    }
                }

                telemetry.apply {
                    clear()
                    addLine("Right is selected")
                    update()
                }

                return rightCommands
            }
        }

        return rightCommands

    }

}