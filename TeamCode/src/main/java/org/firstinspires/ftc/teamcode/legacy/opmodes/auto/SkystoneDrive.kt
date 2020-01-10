package org.firstinspires.ftc.teamcode.legacy.opmodes.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.legacy.lib.*
import org.firstinspires.ftc.teamcode.legacy.subsystems.Arm
import org.firstinspires.ftc.teamcode.legacy.subsystems.Intake
import org.firstinspires.ftc.teamcode.legacy.subsystems.MecanumDrivetrain
import org.firstinspires.ftc.teamcode.legacy.subsystems.Vision

@Disabled
@Autonomous(name = "Skystone Drive", group = "bad")
class SkystoneDrive : LinearOpMode() {

    val drivetrain: MecanumDrivetrain by lazy {
        MecanumDrivetrain()
    }
    val vision: Vision by lazy {
        Vision(hardwareMap)
    }
    val intake: Intake by lazy {
        Intake()
    }
    val arm: Arm by lazy {
        Arm()
    }
    val runtime: ElapsedTime = ElapsedTime()

    var temp = 0.0

    val blueCommands: () -> Unit = {
        drivetrain.driveToPosition(DriveToPositionCommand(Distance(24.0)))(::opModeIsActive) {}
        while (vision.discern() == Vision.SkystonePosition.UNKNOWN) {
        }
        vision.discern().acquireSkystone(drivetrain, intake, ::opModeIsActive)
        temp = runtime.seconds()
        while (runtime.seconds() < (temp + 0.75) && opModeIsActive()) arm.update(Gamepad().apply {
            dpad_down = true
            left_bumper = true
        })
        arm.update(Gamepad())
        drivetrain.driveToPosition(DriveToPositionCommand(rotation = Rotation(90.0)))
        drivetrain.driveToPosition(DriveToPositionCommand(Distance(48.0)))
        arm.update(Gamepad().apply {
            right_bumper = true
        })
        arm.update(Gamepad())
        temp = runtime.seconds()
        while (runtime.seconds() < (temp + 1.0) && opModeIsActive()) arm.update(Gamepad().apply {
            right_trigger = 1.0f
        })
        arm.update(Gamepad().apply {
            left_bumper = true
        })
        temp = runtime.seconds()
        while (runtime.seconds() < (temp + 1.0) && opModeIsActive()) arm.update(Gamepad().apply {
            left_trigger = 1.0f
        })
        arm.update(Gamepad())
        drivetrain.driveToPosition(DriveToPositionCommand(Distance(-24.0)))
    }

    val redCommands: () -> Unit = {
        drivetrain.driveToPosition(DriveToPositionCommand(Distance(36.0)))(::opModeIsActive) {}
        vision.discern().acquireSkystone(drivetrain, intake, ::opModeIsActive)
        temp = runtime.seconds()
        while (runtime.seconds() < (temp + 0.75) && opModeIsActive()) arm.update(Gamepad().apply {
            dpad_down = true
            left_bumper = true
        })
        arm.update(Gamepad())
        drivetrain.driveToPosition(DriveToPositionCommand(rotation = Rotation(-90.0)))
        drivetrain.driveToPosition(DriveToPositionCommand(Distance(48.0)))
        arm.update(Gamepad().apply {
            right_bumper = true
        })
        arm.update(Gamepad())
        temp = runtime.seconds()
        while (runtime.seconds() < (temp + 1.0) && opModeIsActive()) arm.update(Gamepad().apply {
            right_trigger = 1.0f
        })
        arm.update(Gamepad().apply {
            left_bumper = true
        })
        temp = runtime.seconds()
        while (runtime.seconds() < (temp + 1.0) && opModeIsActive()) arm.update(Gamepad().apply {
            left_trigger = 1.0f
        })
        arm.update(Gamepad())
        drivetrain.driveToPosition(DriveToPositionCommand(Distance(-24.0)))
    }

    override fun runOpMode() {

        drivetrain.init(hardwareMap)
        intake.init(hardwareMap)
        arm.init(hardwareMap)
        vision.init()
        val commands = selectionPrompt()

        if (isStopRequested) return

        waitForStart()
        runtime.reset()

        temp = runtime.seconds()
        while (opModeIsActive() && runtime.seconds() < (0.75 + temp)) {
            arm.update(Gamepad().apply {
                dpad_up = true
            })
            intake.update(Gamepad().apply {
                x = true
            })
        }
        arm.update(Gamepad())

        commands()

        telemetry.clear()
        telemetry.addLine("AAAA")
        telemetry.update()

        vision.shitdown()

    }

    fun selectionPrompt(): () -> Unit {
        val intro = """
            USE THE LEFT AND RIGHT DPAD BUTTONS FOR SELECTION
            ================================================
            LEFT: BLUE ALLIANCE
            RIGHT: RED ALLIANCE
        """.trimIndent()

        val lOutro = """
            YOU HAVE SELECTED BLUE ALLIANCE
            ===============================
            RIGHT: ACCEPT
            LEFT: RETURN
        """.trimIndent()

        val rOutro = """
            YOU HAVE SELECTED RED ALLIANCE
            ================================
            RIGHT: ACCEPT
            LEFT: RETURN
        """.trimIndent()

        val left = Button()
        val right = Button()

        start@ while (!isStopRequested) {
            telemetry.clear()
            telemetry.addLine(intro)
            telemetry.update()

            while (left.update(gamepad1.dpad_left) == ButtonState.NOT_PRESSED
                    && right.update(gamepad1.dpad_right) == ButtonState.NOT_PRESSED
                    && opModeIsActive()) {
            }

            telemetry.clear()
            telemetry.update()

            if (left.state != ButtonState.NOT_PRESSED) {
                telemetry.addLine(lOutro)
                telemetry.update()

                while (right.update(gamepad1.dpad_right) != ButtonState.PRESSED && !isStopRequested) {
                    if (left.update(gamepad1.dpad_left) == ButtonState.PRESSED && !isStopRequested) {
                        continue@start
                    }
                }

                telemetry.apply {
                    clear()
                    addLine("Left is selected")
                    update()
                }

                return blueCommands
            } else if (right.state != ButtonState.NOT_PRESSED) {
                telemetry.addLine(rOutro)
                telemetry.update()

                while (right.update(gamepad1.dpad_right) != ButtonState.PRESSED && !isStopRequested) {
                    if (left.update(gamepad1.dpad_left) == ButtonState.PRESSED && !isStopRequested) {
                        continue@start
                    }
                }

                telemetry.apply {
                    clear()
                    addLine("Right is selected")
                    update()
                }

                return redCommands
            }
        }

        return redCommands

    }

}