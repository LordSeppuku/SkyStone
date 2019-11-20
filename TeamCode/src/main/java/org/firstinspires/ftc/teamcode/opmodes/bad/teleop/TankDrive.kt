package org.firstinspires.ftc.teamcode.opmodes.bad.teleop

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.badlib.MecanumDrivetrain

@TeleOp(name = "Tank Drive", group = "bad")
class TankDrive: LinearOpMode() {

    val drivetrain: MecanumDrivetrain = MecanumDrivetrain(hardwareMap)
    val runtime: ElapsedTime = ElapsedTime()

    override fun runOpMode() {

        drivetrain.init()

        waitForStart()

        runtime.reset()

        while (opModeIsActive()) {
            with(gamepad1) {
                drivetrain.tankDrive(
                        left_stick_y.toDouble(),
                        right_stick_y.toDouble(),
                        left_trigger.toDouble(),
                        right_trigger.toDouble()
                )
            }
        }

    }

}