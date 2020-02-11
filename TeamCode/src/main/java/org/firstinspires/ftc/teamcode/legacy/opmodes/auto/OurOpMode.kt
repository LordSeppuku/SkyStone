package org.firstinspires.ftc.teamcode.legacy.opmodes.auto

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import com.tinder.StateMachine
import org.firstinspires.ftc.teamcode.legacy.autonomous.StateMachine.*
import org.firstinspires.ftc.teamcode.legacy.lib.Button
import org.firstinspires.ftc.teamcode.legacy.lib.ButtonState
import org.firstinspires.ftc.teamcode.legacy.subsystems.Arm
import org.firstinspires.ftc.teamcode.legacy.subsystems.Intake
import org.firstinspires.ftc.teamcode.legacy.subsystems.MecanumDrivetrain
import org.firstinspires.ftc.teamcode.legacy.subsystems.Vision

open class OurOpMode : LinearOpMode() {
    val drivetrain by lazy {
        MecanumDrivetrain(hardwareMap)
    }
    val arm by lazy {
        Arm(hardwareMap)
    }
    val intake by lazy {
        Intake(hardwareMap)
    }
    val vision by lazy {
        Vision(hardwareMap)
    }
    val stateMachine by lazy {
        AutoStateMachine(this).apply {
            stateMachine
        }
    }
    val runtime = ElapsedTime()
    var startPose: Pose = Pose.StartAlpha
        protected set
    var alliance: Alliance = Alliance.Red
        protected set

    open fun run() {}

    override fun runOpMode() {
        arm.init()
        intake.init()
        vision.init()
        drivetrain.init()
        selectionPrompt()
        telemetry.addLine("Raring to go")
        telemetry.update()

        waitForStart()

        runtime.reset()
        run()

        vision.shitdown()
    }

    fun selectionPrompt(): Alliance {
        val intro = """
            USE THE LEFT AND RIGHT DPAD BUTTONS FOR SELECTION
            ================================================
            LEFT: BLUE
            RIGHT: RED
        """.trimIndent()

        val lOutro = """
            YOU HAVE SELECTED BLUE
            ===============================
            RIGHT: ACCEPT
            LEFT: RETURN
        """.trimIndent()

        val rOutro = """
            YOU HAVE SELECTED RED
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
                    addLine("Blue is selected")
                    update()
                }

                return Alliance.Blue
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
                    addLine("Red is selected")
                    update()
                }

                return Alliance.Red
            }
        }

        return Alliance.Red

    }

    protected inline infix fun StateMachine<State, Event, SideEffect>.navigateTo(newPose: Pose) = transition(AutoEvent.GoToPose(newPose))

}

sealed class Alliance {
    object Blue : Alliance()
    object Red : Alliance()
}
