package org.firstinspires.ftc.teamcode.opmodes.bad.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.badlib.AutoDriveCommand
import org.firstinspires.ftc.teamcode.badlib.MecanumDrivetrain

@Autonomous(name = "Drive to Center", group = "bad")
class CenterDrive: LinearOpMode() {

    val drivetrain: MecanumDrivetrain = MecanumDrivetrain(hardwareMap)
    val runtime: ElapsedTime = ElapsedTime()

    val redCommands = arrayOf<AutoDriveCommand>(

    )

    val blueCommands = arrayOf<AutoDriveCommand>(

    )

    override fun runOpMode() {



    }

    fun selectionPrompt() {
        val intro = """
            USE THE LEFT AND RIGHT DPAD BUTTONS FOR SELECTION
            =================================================
            LEFT: GO LEFT
            RIGHT: GO RIGHT
        """.trimIndent()

        telemetry.clear()
        telemetry.addLine(intro)
        telemetry.update()

    }

}