package org.firstinspires.ftc.teamcode.legacy.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.legacy.subsystems.*
import kotlin.math.pow

@TeleOp(name = "Vision Drive", group = "Test")
class VisionDrive : LinearOpMode() {

    private val drivetrain = MecanumDrivetrain()
    private val arm = Arm()
    private val intake = Intake()
    private val runtime = ElapsedTime()
    private val imu by lazy {
        IMU(hardwareMap)
    }
    private val vision by lazy {
        Vision(hardwareMap)
    }

    override fun runOpMode() {

        drivetrain.init(hardwareMap)
        arm.init(hardwareMap)
        intake.init(hardwareMap)
        vision.init()
        imu.init()

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

            drivetrain.run {
                localization(imu)
                telemetry.addLine().run {
                    addData("IMU Theta", Theta)
                    addData("Localization X", X)
                    addData("Localization Y", Y)
                }
            }

            telemetry.addLine(vision.discern().toString())

            telemetry.update()
        }

        vision.shitdown()

    }

}