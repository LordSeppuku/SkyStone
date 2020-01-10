package org.firstinspires.ftc.teamcode.legacy.opmodes.test

import com.badlogic.gdx.math.Vector2
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.lib.components.DrivetrainKinematics
import org.firstinspires.ftc.teamcode.lib.components.MecanumKinematics
import kotlin.math.pow
import kotlin.system.measureTimeMillis

@TeleOp(name = "True Drivetrain Test", group = "Test")
class RedBullGivesYouWings : LinearOpMode() {

    private var positionF = Vector2.Zero
    private var heading = 0.0
    private var linearVel = Vector2.Zero
    private var robotVel = Vector2.Zero
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
        val gearRatio = 8.0 / 14.0
        val ticksPerRev = 1425.2
        val wheelRadius = DistanceUnit.INCH.fromMm(50.0)
    }

    override fun runOpMode() {
        var deltaTime = 0.0

        waitForStart()
        while (opModeIsActive()) {
            deltaTime = (update(deltaTime) / 1000).toDouble()
            telemetry.addLine().addData("Delta Time: ", deltaTime)
            telemetry.update()
        }
    }

    fun update(deltaTime: Double) = measureTimeMillis {
        val y = gamepad1.left_stick_y.pow(3).toDouble()
        val x = -gamepad1.left_stick_x.pow(3)
        val w = gamepad1.right_stick_x.pow(3)

        FL.power = Range.clip(y - x - w, -1.0, 1.0)
        FR.power = Range.clip(y + x + w, -1.0, 1.0)
        BL.power = Range.clip(y + x - w, -1.0, 1.0)
        BR.power = Range.clip(y - x + w, -1.0, 1.0)

        localization(deltaTime)
        telemetry.addLine().apply {
            addData("Field Position: ", positionF)
            addData("Heading: ", heading)
            addData("Field Velocity: ", linearVel)
            addData("Robot Velocity: ", robotVel)
            addData("Heading Velocity: ", headingVel)
        }
    }

    fun localization(deltaTime: Double) {
        headingVel = kinematics.wheelRotationsToRotationalVelocity(wheelRadius,
                DrivetrainKinematics.ticksToWheelRotations(FL.velocity.toInt(), gearRatio, ticksPerRev),
                DrivetrainKinematics.ticksToWheelRotations(FR.currentPosition, gearRatio, ticksPerRev),
                DrivetrainKinematics.ticksToWheelRotations(BL.currentPosition, gearRatio, ticksPerRev),
                DrivetrainKinematics.ticksToWheelRotations(BR.currentPosition, gearRatio, ticksPerRev))
        heading += headingVel * deltaTime

        robotVel = kinematics.wheelRotationsToRobotVelocity(wheelRadius,
                DrivetrainKinematics.ticksToWheelRotations(FL.currentPosition, gearRatio, ticksPerRev),
                DrivetrainKinematics.ticksToWheelRotations(FR.currentPosition, gearRatio, ticksPerRev),
                DrivetrainKinematics.ticksToWheelRotations(BL.currentPosition, gearRatio, ticksPerRev),
                DrivetrainKinematics.ticksToWheelRotations(BR.currentPosition, gearRatio, ticksPerRev))

        linearVel = DrivetrainKinematics.robotVelocityToLinearVelocity(robotVel, heading)
        positionF = linearVel.scl(deltaTime.toFloat())
    }
}