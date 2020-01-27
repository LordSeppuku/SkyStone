package org.firstinspires.ftc.teamcode.legacy.opmodes.auto.time

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.legacy.lib.Button
import org.firstinspires.ftc.teamcode.legacy.lib.ButtonState
import org.firstinspires.ftc.teamcode.legacy.subsystems.*

abstract class TimeBasedAuto : LinearOpMode() {
    val driveSpeed: Double = 0.5

    protected val drivetrain by lazy {
        MecanumDrivetrain(hardwareMap)
    }
    protected val arm by lazy {
        Arm(hardwareMap)
    }
    protected val intake by lazy {
        Intake(hardwareMap)
    }
    protected val vision by lazy {
        Vision(hardwareMap)
    }
    protected val foundationGripper by lazy {
        FoundationGripper(hardwareMap)
    }
    protected val runtime = ElapsedTime()

    override fun runOpMode() {
        drivetrain.init()
        arm.init()
        intake.init()
        foundationGripper.init()
        vision.init()
        val procedure = selectionPrompt()

        waitForStart()
        runtime.reset()
        runtime.startTime()

        procedure()

        vision.shitdown()
    }

    protected inline fun <T> whileTill(endTime: Double, code: () -> T): T {
        val elapsedTime = ElapsedTime()
        elapsedTime.startTime()
        var result: T? = null
        while (elapsedTime.seconds() < endTime && opModeIsActive()) {
            result = code()
        }
        return result!!
    }

    protected inline fun whileTill(endTime: Double, code: () -> Unit) {
        val elapsedTime = ElapsedTime()
        elapsedTime.startTime()
        while (elapsedTime.seconds() < endTime && opModeIsActive()) {
            code()
        }
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

                return ::blueProcedure
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

                return ::redProcedure
            }
        }

        return ::redProcedure

    }

    fun MecanumDrivetrain.drive(code: MecanumDrivetrain.() -> Unit) = code()

    fun MecanumDrivetrain.For(endTime: Double, lateral: Double = 0.0, horizontal: Double = 0.0, rotation: Double = 0.0) {
        whileTill(endTime) {
            arcadeDrive(lateral, horizontal, rotation)
        }
        arcadeDrive(0.0, 0.0, 0.0)
        whileTill(0.15) {}
    }

    fun <T> MecanumDrivetrain.ForAlso(endTime: Double, lateral: Double = 0.0, horizontal: Double = 0.0, rotation: Double = 0.0, also: () -> T) {
        val result = whileTill(endTime) {
            arcadeDrive(lateral, horizontal, rotation)
            also
        }
        arcadeDrive(0.0, 0.0, 0.0)
        whileTill(0.15) {}
        result
    }

    abstract fun redProcedure()
    abstract fun blueProcedure()
}