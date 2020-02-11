package org.firstinspires.ftc.teamcode.legacy.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.legacy.lib.Distance
import org.firstinspires.ftc.teamcode.legacy.lib.DriveToPositionCommand
import org.firstinspires.ftc.teamcode.legacy.lib.Rotation
import org.firstinspires.ftc.teamcode.legacy.opmodes.auto.OurOpMode

@Autonomous(name = "LeftAcquisitionStratagem", group = "Test")
class LeftAcquisitionStratagem : OurOpMode() {

    val succDistance = 6.0

    private val commands: List<DriveToPositionCommand> = listOf(
            DriveToPositionCommand(rotation = Rotation(-90.0)),
            DriveToPositionCommand(lateral = Distance(-succDistance)),
            DriveToPositionCommand(horizontal = Distance(13.25)),
            DriveToPositionCommand(lateral = Distance(succDistance)),
            DriveToPositionCommand(horizontal = Distance(-13.25))
    )

    override fun run() {
        for (command in commands) drivetrain.driveToPosition(command)(this) {
            intake.update(Gamepad().apply {
                b = true
                x = true
            }, telemetry)
        }
    }

}