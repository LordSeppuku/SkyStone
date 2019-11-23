package org.firstinspires.ftc.teamcode.legacy.opmodes.teleop

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.legacy.subsystems.Arm
import org.firstinspires.ftc.teamcode.legacy.subsystems.Intake
import org.firstinspires.ftc.teamcode.legacy.subsystems.MecanumDrivetrain
import kotlin.math.pow

@TeleOp(name = "Arcade Drive", group = "bad")
class ArcadeDrive : LinearOpMode() {

    private val drivetrain = MecanumDrivetrain()
    private val arm = Arm()
    private val intake = Intake()
    private val runtime = ElapsedTime()

    override fun runOpMode() {

        drivetrain.init(hardwareMap)
        arm.init(hardwareMap)
        intake.init(hardwareMap)

        waitForStart()

        runtime.reset()

        while (opModeIsActive()) {

            telemetry.clear()

            with(gamepad1) {
                drivetrain.arcadeDrive(
                        left_stick_y.toDouble().pow(3),
                        left_stick_x.toDouble().pow(3),
                        right_stick_x.toDouble().pow(3)
                )

                arm.update(gamepad1, telemetry)
                intake.update(gamepad1, telemetry)
            }

            telemetry.update()
        }

    }

}