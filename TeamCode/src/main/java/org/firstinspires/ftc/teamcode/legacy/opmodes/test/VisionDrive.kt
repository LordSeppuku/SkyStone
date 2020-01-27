package org.firstinspires.ftc.teamcode.legacy.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.legacy.subsystems.Arm
import org.firstinspires.ftc.teamcode.legacy.subsystems.Intake
import org.firstinspires.ftc.teamcode.legacy.subsystems.MecanumDrivetrain
import org.firstinspires.ftc.teamcode.legacy.subsystems.Vision
import kotlin.math.pow

@Disabled
@TeleOp(name = "Vision Drive", group = "Test")
class VisionDrive : LinearOpMode() {

    private val drivetrain by lazy {
        MecanumDrivetrain(hardwareMap)
    }
    private val arm by lazy {
        Arm(hardwareMap)
    }
    private val intake by lazy {
        Intake(hardwareMap)
    }
    private val vision by lazy {
        Vision(hardwareMap)
    }
    private val runtime = ElapsedTime()

    override fun runOpMode() {

        drivetrain.init()
        arm.init()
        intake.init()
        vision.init()

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

                arm.update(gamepad1)
                intake.update(gamepad1, telemetry)
            }

            telemetry.addLine(vision.discern().toString())

            telemetry.update()
        }

        vision.shitdown()

    }

}