package org.firstinspires.ftc.teamcode.legacy.opmodes.test

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit
import org.firstinspires.ftc.teamcode.legacy.lib.Distance
import org.firstinspires.ftc.teamcode.legacy.lib.DriveWithVelocityCommand
import org.firstinspires.ftc.teamcode.legacy.lib.Pose
import org.firstinspires.ftc.teamcode.legacy.lib.Rotation
import kotlin.math.PI
import kotlin.math.abs

@TeleOp(name = "Drivetrain Test", group = "Test")
class DrivetrainTest : LinearOpMode() {
    private val FL: DcMotorEx
        get() = hardwareMap.dcMotor.get("fl") as DcMotorEx

    private val FR: DcMotorEx
        get() = hardwareMap.dcMotor.get("fr") as DcMotorEx

    private val BL: DcMotorEx
        get() = hardwareMap.dcMotor.get("bl") as DcMotorEx

    private val BR: DcMotorEx
        get() = hardwareMap.dcMotor.get("br") as DcMotorEx

    private var lastX = 0.0
    private var lastY = 0.0
    private var lastTheta = 0.0
    private var lastFL = 0.0
    private var lastFR = 0.0
    private var lastBL = 0.0
    private var lastBR = 0.0
    var X = Distance()
        private set
    var Y = Distance()
        private set
    var Theta = Rotation()
        private set

    val WHEEL_RADIUS = Distance(50.0, DistanceUnit.MM)
    val DRIVETRAIN_WIDTH = Distance(5.25, DistanceUnit.INCH)
    val DRIVETRAIN_LENGTH = Distance(2.875, DistanceUnit.INCH)
    val GEAR_RATIO: Double = 8.0 / 14.0
    val TICKS_PER_REV: Double = 1425.2

    private fun changeEncoderMode(runMode: DcMotor.RunMode): DcMotor.RunMode {
        FL.mode = runMode
        FR.mode = runMode
        BL.mode = runMode
        BR.mode = runMode

        return FL.mode
    }

    private fun error(currentPose: Pose, targetPose: Pose): Pose {
        val workX = targetPose.x.unit.toMeters(targetPose.x.value) - currentPose.x.value
        val workY = targetPose.y.unit.toMeters(targetPose.y.value) - currentPose.y.value
        val workW = targetPose.w.unit.toRadians(targetPose.w.value) - currentPose.w.value


        val absX = abs(workX)
        val absY = abs(workY)
        val absW = abs(workW)

        if (absX <= 0.25 && absY <= 0.25 && absW <= UnnormalizedAngleUnit.DEGREES.toRadians(5.0)) return Pose()
        return Pose(Distance(workX, DistanceUnit.METER), Distance(workY, DistanceUnit.METER), Rotation(workW, UnnormalizedAngleUnit.RADIANS))
    }

    private fun error(targetPose: Pose) = error(robotPose(), targetPose)


    private fun robotPose() = Pose(X, Y, Theta)

    private fun produceDriveCommand(velocity: Pose) = DriveWithVelocityCommand(
            velocity.y, velocity.x, velocity.w
    )

    override fun runOpMode() {
        BL.direction = DcMotorSimple.Direction.REVERSE

        waitForStart()

        val tgtPose = Pose(Distance(24.0))

        while (opModeIsActive()) {
            localization()
            driveWithVelocity(produceDriveCommand(
                    error(tgtPose)
            ))
        }

    }

    fun driveWithVelocity(command: DriveWithVelocityCommand) {
        changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)

        val radius = WHEEL_RADIUS.run { unit.toMeters(value) }
        val x = command.lateral.run { unit.toMeters(value) }
        val y = command.horizontal.run { unit.toMeters(value) }
        val w = command.rotation.run { unit.toRadians(value) }
        val drive = DRIVETRAIN_WIDTH.run { unit.toMeters(value) } + DRIVETRAIN_LENGTH.run { unit.toMeters(value) }
        val r = (1.0 / radius)

        val vfl = (GEAR_RATIO * r) * (x + y - (drive * w))
        val vfr = (GEAR_RATIO * r) * (x - y + (drive * w))
        val vbl = (GEAR_RATIO * r) * (x - y - (drive * w))
        val vbr = (GEAR_RATIO * r) * (x + y + (drive * w))

        FL.setVelocity(vfl, AngleUnit.RADIANS)
        FR.setVelocity(vfr, AngleUnit.RADIANS)
        BL.setVelocity(vbl, AngleUnit.RADIANS)
        BR.setVelocity(vbr, AngleUnit.RADIANS)
    }

    fun localization() {
        val r = WHEEL_RADIUS.run { unit.toMeters(value) }

        val curFL = (FL.currentPosition / TICKS_PER_REV) * (2 * PI * (1 / GEAR_RATIO))
        val curFR = (FR.currentPosition / TICKS_PER_REV) * (2 * PI * (1 / GEAR_RATIO))
        val curBL = (BL.currentPosition / TICKS_PER_REV) * (2 * PI * (1 / GEAR_RATIO))
        val curBR = (BR.currentPosition / TICKS_PER_REV) * (2 * PI * (1 / GEAR_RATIO))

        val deltFL = curFL - lastFL
        val deltFR = curFR - lastFR
        val deltBL = curBL - lastBL
        val deltBR = curBR - lastBR

        val lY = (r / 4) * (deltFL + deltFR + deltBL + deltBR)
        val lX = (r / 4) * (deltFL - deltFR - deltBL + deltBR)
        val wZ = (r / (4 * DRIVETRAIN_WIDTH.run { unit.toMeters(value) } + DRIVETRAIN_LENGTH.run { unit.toMeters(value) })) * (
                -deltFL + deltFR - deltBL + deltBR
                )
        val workX = X.unit.toMeters(X.value + lY)
        val workY = Y.unit.toMeters(Y.value + lX)
        val workW = Theta.unit.toRadians(Theta.value + wZ) % (2 * PI)

        X = Distance(workY, DistanceUnit.METER)
        Y = Distance(workX, DistanceUnit.METER)
        Theta = Rotation(workW, UnnormalizedAngleUnit.RADIANS)

        lastX = X.value
        lastY = Y.value
        lastTheta = Theta.value
        lastFL = curFL
        lastFR = curFR
        lastBL = curBL
        lastBR = curBR
    }
}