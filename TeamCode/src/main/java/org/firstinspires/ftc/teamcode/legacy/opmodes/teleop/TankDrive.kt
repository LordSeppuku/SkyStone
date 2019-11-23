package org.firstinspires.ftc.teamcode.legacy.opmodes.teleop

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.legacy.subsystems.MecanumDrivetrain

@TeleOp(name = "Tank Drive", group = "bad")
class TankDrive: LinearOpMode() {

    val drivetrain: MecanumDrivetrain = MecanumDrivetrain()
    val runtime: ElapsedTime = ElapsedTime()

    override fun runOpMode() {

        drivetrain.init(hardwareMap)

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