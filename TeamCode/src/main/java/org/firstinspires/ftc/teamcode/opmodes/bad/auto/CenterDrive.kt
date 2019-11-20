package org.firstinspires.ftc.teamcode.opmodes.bad.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.badlib.AutoDriveCommand
import org.firstinspires.ftc.teamcode.badlib.Button
import org.firstinspires.ftc.teamcode.badlib.ButtonState
import org.firstinspires.ftc.teamcode.badlib.MecanumDrivetrain

@Autonomous(name = "Drive to Center", group = "bad")
class CenterDrive: LinearOpMode() {

    val drivetrain: MecanumDrivetrain = MecanumDrivetrain(hardwareMap)
    val runtime: ElapsedTime = ElapsedTime()

    val leftCommands = arrayOf<AutoDriveCommand>(

    )

    val rightCommands = arrayOf<AutoDriveCommand>(

    )

    override fun runOpMode() {

        drivetrain.init()
        val commands = selectionPrompt()

        waitForStart()
        runtime.reset()
        commands.forEach {
            val done = drivetrain.setAutoDrive(it)
            while (done.invoke()) {}
        }

    }

    fun selectionPrompt(): Array<AutoDriveCommand> {
        val intro = """
            USE THE LEFT AND RIGHT DPAD BUTTONS FOR SELECTION
            =================================================
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

        if (left.state != ButtonState.NOT_PRESSED) return leftCommands
        else return rightCommands

    }

}