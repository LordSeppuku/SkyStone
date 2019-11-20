package org.firstinspires.ftc.teamcode.opmodes.bad.teleop

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.badlib.MecanumDrivetrain

@TeleOp(name = "Arcade Drive", group = "bad")
class ArcadeDrive: LinearOpMode() {

    val drivetrain: MecanumDrivetrain = MecanumDrivetrain(hardwareMap)
    val runtime: ElapsedTime = ElapsedTime()

    override fun runOpMode() {

        drivetrain.init()

        waitForStart()

        runtime.reset()

        while (opModeIsActive()) {
            with(gamepad1) {
                drivetrain.arcadeDrive(
                        left_stick_y.toDouble(),
                        left_stick_x.toDouble(),
                        right_stick_x.toDouble()
                )
            }
        }

    }

}