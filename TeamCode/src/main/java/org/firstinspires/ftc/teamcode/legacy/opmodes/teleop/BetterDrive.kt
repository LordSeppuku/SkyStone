package org.firstinspires.ftc.teamcode.legacy.opmodes.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "Brandon is too much of a simp \nto have it all on one \ncontroller, and thus, deserves \nto have his vibe rights \nrescinded from here on out.")
class BetterDrive : ArcadeDrive() {
    override fun runOpMode() {
        drivetrain.init()
        intake.init()
        arm.init()
        waitForStart()
        while (opModeIsActive()) {
            gamepad1.run {
                drivetrain.arcadeDrive(
                        -left_stick_y.toDouble(),
                        left_stick_x.toDouble(),
                        right_stick_x.toDouble()
                )
                intake.update(this, telemetry)
            }
            gamepad2.run {
                arm.update(this)
            }
        }
    }
}