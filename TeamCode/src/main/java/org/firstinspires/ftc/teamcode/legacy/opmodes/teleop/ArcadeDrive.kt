package org.firstinspires.ftc.teamcode.legacy.opmodes.teleop

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.legacy.subsystems.Arm
import org.firstinspires.ftc.teamcode.legacy.subsystems.FoundationGripper
import org.firstinspires.ftc.teamcode.legacy.subsystems.Intake
import org.firstinspires.ftc.teamcode.legacy.subsystems.MecanumDrivetrain
import kotlin.math.pow

@TeleOp(name = "Arcade Drive", group = "bad")
class ArcadeDrive : LinearOpMode() {

    protected val drivetrain by lazy {
        MecanumDrivetrain(hardwareMap)
    }
    protected val arm by lazy {
        Arm(hardwareMap)
    }
    protected val intake by lazy {
        Intake(hardwareMap)
    }
    protected val foundationGripper by lazy {
        FoundationGripper(hardwareMap)
    }
    private val runtime = ElapsedTime()

    override fun runOpMode() {

        drivetrain.init()
        arm.init()
        intake.init()
        foundationGripper.init()

        waitForStart()

        runtime.reset()

        while (opModeIsActive()) {

            telemetry.clear()

            with(gamepad1) {
                drivetrain.arcadeDrive(
                        left_stick_y.toDouble().pow(3),
                        -left_stick_x.toDouble().pow(3),
                        right_stick_x.toDouble().pow(3)
                )

                arm.update(gamepad1)
                intake.update(gamepad1, telemetry)
                foundationGripper.update(gamepad1)
            }

            telemetry.update()
        }

    }

}