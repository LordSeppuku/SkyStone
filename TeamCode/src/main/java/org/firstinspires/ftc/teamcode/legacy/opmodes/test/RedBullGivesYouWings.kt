package org.firstinspires.ftc.teamcode.legacy.opmodes.test

import com.badlogic.gdx.math.Vector2
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.lib.components.MecanumKinematics
import kotlin.system.measureTimeMillis

@Disabled
@TeleOp(name = "True Drivetrain Test", group = "Test")
class RedBullGivesYouWings : LinearOpMode() {

    private var positionF = Vector2(0f, 0f)
    private var heading = 0.0
    private var linearVel = Vector2(0f, 0f)
    private var robotVel = Vector2(0f, 0f)
    private var headingVel = 0.0

    private val FL: DcMotorEx
        get() = hardwareMap.dcMotor.get("fl") as DcMotorEx

    private val FR: DcMotorEx
        get() = hardwareMap.dcMotor.get("fr") as DcMotorEx

    private val BL: DcMotorEx
        get() = hardwareMap.dcMotor.get("bl") as DcMotorEx

    private val BR: DcMotorEx
        get() = hardwareMap.dcMotor.get("br") as DcMotorEx

    companion object {
        val kinematics = MecanumKinematics(2.875, 5.25)
        val gearRatio = 14.0 / 8.0
        val ticksPerRev = 1425.2
        val wheelRadius = DistanceUnit.INCH.fromMm(50.0)
    }

    override fun runOpMode() {

        FR.direction = DcMotorSimple.Direction.REVERSE
        BR.direction = DcMotorSimple.Direction.REVERSE
        BL.direction = DcMotorSimple.Direction.REVERSE

        changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)

        waitForStart()
        val elapsedTime = ElapsedTime()
        var deltaTime = 0.0
        while (opModeIsActive()) {
            deltaTime = (update(deltaTime) / 1000).toDouble()
            telemetry.addLine().addData("Delta Time: ", deltaTime)
            telemetry.update()
            deltaTime = elapsedTime.seconds()
            elapsedTime.reset()
        }
    }

    fun update(deltaTime: Double) = measureTimeMillis {
        val y = gamepad1.left_stick_y.toDouble()
        val x = -gamepad1.left_stick_x.toDouble()
        val w = -gamepad1.right_stick_x.toDouble()

        FL.power = Range.clip(y + x + w, -1.0, 1.0)
        FR.power = Range.clip(y - x - w, -1.0, 1.0)
        BL.power = Range.clip(y - x + w, -1.0, 1.0)
        BR.power = Range.clip(y + x - w, -1.0, 1.0)

        localization(deltaTime)
        telemetry.addLine("Localization: ").apply {
            addData("\n" +
                    "     Field Position: ", positionF)
            addData("\n" +
                    "     Heading: ", heading)
            addData("\n" +
                    "     Field Velocity: ", linearVel)
            addData("\n" +
                    "     Robot Velocity: ", robotVel)
            addData("\n" +
                    "     Heading Velocity: ", headingVel)
        }
        telemetry.addLine("Motor Statuses:").apply {
            addData("\n" +
                    "     FL velocity: ", FL.velocity)
            addData("\n" +
                    "     FR velocity: ", FR.velocity)
            addData("\n" +
                    "     BL velocity: ", BL.velocity)
            addData("\n" +
                    "     BR velocity: ", BR.velocity)
        }
    }

    fun localization(deltaTime: Double) {
        headingVel = kinematics.wheelRotationsToRotationalVelocity(wheelRadius,
                kinematics.ticksToWheelRotations(FL.velocity.toInt(), gearRatio, ticksPerRev),
                kinematics.ticksToWheelRotations(FR.velocity.toInt(), gearRatio, ticksPerRev),
                kinematics.ticksToWheelRotations(BL.velocity.toInt(), gearRatio, ticksPerRev),
                kinematics.ticksToWheelRotations(BR.velocity.toInt(), gearRatio, ticksPerRev))
        heading += headingVel * deltaTime

        robotVel = kinematics.wheelRotationsToRobotVelocity(wheelRadius,
                kinematics.ticksToWheelRotations(FL.velocity.toInt(), gearRatio, ticksPerRev),
                kinematics.ticksToWheelRotations(FR.velocity.toInt(), gearRatio, ticksPerRev),
                kinematics.ticksToWheelRotations(BL.velocity.toInt(), gearRatio, ticksPerRev),
                kinematics.ticksToWheelRotations(BR.velocity.toInt(), gearRatio, ticksPerRev))

        linearVel = kinematics.robotVelocityToLinearVelocity(robotVel, heading)
        positionF = positionF.add(linearVel.scl(deltaTime.toFloat()))
    }

    private fun changeEncoderMode(runMode: DcMotor.RunMode): DcMotor.RunMode {
        FL.mode = runMode
        FR.mode = runMode
        BL.mode = runMode
        BR.mode = runMode

        return FL.mode
    }
}