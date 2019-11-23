package org.firstinspires.ftc.teamcode.legacy.opmodes.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.legacy.lib.*
import org.firstinspires.ftc.teamcode.legacy.subsystems.MecanumDrivetrain

@Autonomous(name = "Drive to Center", group = "bad")
class CenterDrive: LinearOpMode() {

    val drivetrain: MecanumDrivetrain = MecanumDrivetrain()
    val runtime: ElapsedTime = ElapsedTime()

    val leftCommands = arrayOf<AutoDriveCommand>(
            AutoDriveCommand(Distance(0.0), Distance(-28.0), Rotation(0.0), 6.0)
    )

    val rightCommands = arrayOf<AutoDriveCommand>(
            AutoDriveCommand(Distance(0.0), Distance(28.0), Rotation(0.0), 6.0)
    )

    override fun runOpMode() {

        drivetrain.init(hardwareMap)
        val commands = selectionPrompt()

        waitForStart()
        runtime.reset()
        commands.forEach {
            val done = drivetrain.setAutoDrive(it)
            while (done.invoke() && opModeIsActive()) {}
        }

    }

    fun selectionPrompt(): Array<AutoDriveCommand> {
        val intro = """
            USE THE LEFT AND RIGHT DPAD BUTTONS FOR SELECTION
            ================================================
            LEFT: GO LEFT
            RIGHT: GO RIGHT
        """.trimIndent()

        val left = Button()
        val right = Button()

        telemetry.clear()
        telemetry.addLine(intro)
        telemetry.update()

        while (left.update(gamepad1.dpad_left) == ButtonState.NOT_PRESSED && right.update(gamepad1.dpad_right) == ButtonState.NOT_PRESSED) {}

        telemetry.clear()
        telemetry.update()

        if (left.state != ButtonState.NOT_PRESSED) {
            telemetry.addLine("Left selected")
            telemetry.update()
            return leftCommands
        }
        else {
            telemetry.addLine("Right selected")
            telemetry.update()
            return rightCommands
        }
    }

}