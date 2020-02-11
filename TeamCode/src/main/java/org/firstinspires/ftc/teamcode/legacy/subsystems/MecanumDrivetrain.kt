package org.firstinspires.ftc.teamcode.legacy.subsystems

import android.util.Log
import com.badlogic.gdx.math.Vector2
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.legacy.lib.Distance
import org.firstinspires.ftc.teamcode.legacy.lib.DriveToPositionCommand
import org.firstinspires.ftc.teamcode.legacy.lib.Pose
import org.firstinspires.ftc.teamcode.legacy.lib.Rotation
import org.firstinspires.ftc.teamcode.legacy.opmodes.auto.OurOpMode
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow

class MecanumDrivetrain(private val hardwareMap: HardwareMap, startHeading: Double = 0.0) {

    companion object {
        val WHEEL_RADIUS = Distance(50.0, DistanceUnit.MM)
        val DRIVETRAIN_WIDTH = Distance(5.25, DistanceUnit.INCH)
        val DRIVETRAIN_LENGTH = Distance(2.875, DistanceUnit.INCH)
        val GEAR_RATIO: Double = 8.0 / 14.0
        val TICKS_PER_REV: Double = 1425.2
    }

    private var lastM1 = 0
    private var lastM2 = 0
    private var lastM3 = 0
    private var lastM4 = 0

    var X = Distance()
        private set
    var Y = Distance()
        private set
    var Theta = Rotation()
        private set

    fun robotPose() = Pose(X, Y, Theta)

    var driveStatus: DriveStatus = DriveStatus.UNINIT
        private set


    private val FL: DcMotorEx
        get() = hardwareMap.dcMotor.get("fl") as DcMotorEx

    private val FR: DcMotorEx
        get() = hardwareMap.dcMotor.get("fr") as DcMotorEx

    private val BL: DcMotorEx
        get() = hardwareMap.dcMotor.get("bl") as DcMotorEx

    private val BR: DcMotorEx
        get() = hardwareMap.dcMotor.get("br") as DcMotorEx

    var robotHeading = startHeading
        private set

    fun init() {
        if (driveStatus != DriveStatus.UNINIT) return
        FL.direction = DcMotorSimple.Direction.REVERSE
        BL.direction = DcMotorSimple.Direction.REVERSE
        changeEncoderMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER)

        driveStatus = DriveStatus.INIT

        changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)
    }

    fun init(fieldPosition: Pose) {
        init()
        X = fieldPosition.x
        Y = fieldPosition.y
        Theta = fieldPosition.w
    }

    private fun changeEncoderMode(runMode: DcMotor.RunMode): DcMotor.RunMode {
        FL.mode = runMode
        FR.mode = runMode
        BL.mode = runMode
        BR.mode = runMode

        return FL.mode
    }

    fun arcadeDrive(lateral: Double, horizontal: Double, c: Double) {

        if (driveStatus != DriveStatus.DIRECT_CONTROL) {
            changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)
            driveStatus = DriveStatus.DIRECT_CONTROL
        }

        val y = lateral.pow(3)
        val x = horizontal.pow(3)
        val w = c.pow(3)

        FL.power = Range.clip(y + x + w, -1.0, 1.0)
        FR.power = Range.clip(y - x - w, -1.0, 1.0)
        BL.power = Range.clip(y - x + w, -1.0, 1.0)
        BR.power = Range.clip(y + x - w, -1.0, 1.0)
    }

    fun tankDrive(y1: Double, y2: Double, lt: Double, rt: Double) {

        if (driveStatus != DriveStatus.DIRECT_CONTROL) {
            changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)
            driveStatus = DriveStatus.DIRECT_CONTROL
        }

        FL.power = Range.clip(y1 - lt + rt, -1.0, 1.0)
        FR.power = Range.clip(y2 + lt - rt, -1.0, 1.0)
        BL.power = Range.clip(y1 + lt - rt, -1.0, 1.0)
        BR.power = Range.clip(y2 - lt + rt, -1.0, 1.0)
    }

    private fun turn(targetDegrees: Double, power: Double, opMode: OurOpMode) {
        FL.power = 0.0
        FR.power = 0.0
        BL.power = 0.0
        BR.power = 0.0

        changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)

        val radius = WHEEL_RADIUS.run { unit.toMeters(value) }
        val x = 0
        val y = 0
        val w = targetDegrees
        val drive = DRIVETRAIN_WIDTH.run { unit.toMeters(value) } + DRIVETRAIN_LENGTH.run { unit.toMeters(value) }
        val r = (1.0 / radius)

        val vfl = GEAR_RATIO * r * (x - y - (drive * w))
        val vfr = GEAR_RATIO * r * (x + y + (drive * w))
        val vbl = GEAR_RATIO * r * (x + y - (drive * w))
        val vbr = GEAR_RATIO * r * (x - y + (drive * w))

        val pfl = ((vfl / (2 * PI)) * TICKS_PER_REV)
        val pfr = ((vfr / (2 * PI)) * TICKS_PER_REV)
        val pbl = ((vbl / (2 * PI)) * TICKS_PER_REV)
        val pbr = ((vbr / (2 * PI)) * TICKS_PER_REV)

        FL.targetPosition = FL.currentPosition + pfl.toInt()
        FR.targetPosition = FR.currentPosition + pfr.toInt()
        BL.targetPosition = BL.currentPosition + pbl.toInt()
        BR.targetPosition = BR.currentPosition + pbr.toInt()

        driveStatus = DriveStatus.DRIVING_TO_POSITION

        Log.i("FL target position", "$pfl")

        FL.targetPosition = FL.currentPosition + pfl.toInt()
        FR.targetPosition = FR.currentPosition + pfr.toInt()
        BL.targetPosition = BL.currentPosition + pbl.toInt()
        BR.targetPosition = BR.currentPosition + pbr.toInt()

        changeEncoderMode(DcMotor.RunMode.RUN_TO_POSITION)

        FL.power = power
        FR.power = power
        BL.power = power
        BR.power = power

        while ((FL.isBusy || FR.isBusy || BL.isBusy || BR.isBusy)
                && opMode.opModeIsActive()) {
        }
        driveStatus = DriveStatus.READY

        FL.power = 0.0
        FR.power = 0.0
        BL.power = 0.0
        BR.power = 0.0

        changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)

        driveStatus

    }

    fun driveToPosition(command: DriveToPositionCommand): (OurOpMode, OurOpMode.() -> Unit) -> DriveStatus {

        val targetHeading = command.rotation.unit.toDegrees(command.rotation.value)

        FL.power = 0.0
        FR.power = 0.0
        BL.power = 0.0
        BR.power = 0.0

        changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)

        val radius = WHEEL_RADIUS.run { unit.toMeters(value) }
        val x = command.lateral.run { unit.toMeters(value) }
        val y = -command.horizontal.run { unit.toMeters(value) }
        val w = -command.rotation.run { unit.toRadians(value) }
        val drive = DRIVETRAIN_WIDTH.run { unit.toMeters(value) } + DRIVETRAIN_LENGTH.run { unit.toMeters(value) }
        val r = (1.0 / radius)

        Log.i("Numbers", "$radius $x $y $w $drive $r ${command.power} $GEAR_RATIO $TICKS_PER_REV")

        val vfl = GEAR_RATIO * r * (x - y - (drive * w))
        val vfr = GEAR_RATIO * r * (x + y + (drive * w))
        val vbl = GEAR_RATIO * r * (x + y - (drive * w))
        val vbr = GEAR_RATIO * r * (x - y + (drive * w))

        val pfl = ((vfl / (2 * PI)) * TICKS_PER_REV)
        val pfr = ((vfr / (2 * PI)) * TICKS_PER_REV)
        val pbl = ((vbl / (2 * PI)) * TICKS_PER_REV)
        val pbr = ((vbr / (2 * PI)) * TICKS_PER_REV)

        FL.targetPosition = FL.currentPosition + pfl.toInt()
        FR.targetPosition = FR.currentPosition + pfr.toInt()
        BL.targetPosition = BL.currentPosition + pbl.toInt()
        BR.targetPosition = BR.currentPosition + pbr.toInt()

        driveStatus = DriveStatus.DRIVING_TO_POSITION

        Log.i("FL target position", "$pfl")



        fun a(opMode: OurOpMode, busyAction: OurOpMode.() -> Unit = command.busyCode): DriveStatus {

            if (robotHeading epsilonEquals targetHeading)
                turn(targetHeading - robotHeading, command.power, opMode); command.rotation.unit.toDegrees(command.rotation.value)

            FL.targetPosition = FL.currentPosition + pfl.toInt()
            FR.targetPosition = FR.currentPosition + pfr.toInt()
            BL.targetPosition = BL.currentPosition + pbl.toInt()
            BR.targetPosition = BR.currentPosition + pbr.toInt()

            changeEncoderMode(DcMotor.RunMode.RUN_TO_POSITION)

            with(command) {
                FL.power = power
                FR.power = power
                BL.power = power
                BR.power = power
            }

            while ((FL.isBusy || FR.isBusy || BL.isBusy || BR.isBusy)
                    && opMode.opModeIsActive()) {
                opMode.busyAction()
            }
            driveStatus = DriveStatus.READY

            FL.power = 0.0
            FR.power = 0.0
            BL.power = 0.0
            BR.power = 0.0

            changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)

            return driveStatus
        }

        return ::a
    }

    fun localization(deltaTime: Double) {
        val r = WHEEL_RADIUS.run { unit.toInches(value) } / 4

        val velFL = FL.getVelocity(AngleUnit.RADIANS) * (14 / 8)
        val velFR = FR.getVelocity(AngleUnit.RADIANS) * (14 / 8)
        val velBL = BL.getVelocity(AngleUnit.RADIANS) * (14 / 8)
        val velBR = BR.getVelocity(AngleUnit.RADIANS) * (14 / 8)

        val velYr = r * (velFL + velFR + velBL + velBR)
        val velXr = r * (-velFL + velFR + velBL - velBR)
        val velTr = (r / (5.25 + 2.875)) * (-velFL + velFR - velBL + velBR)

        Theta = Rotation(Theta.unit.toRadians(Theta.value) + (velTr * deltaTime))

        val velVec = Vector2(velXr.toFloat(), velYr.toFloat()).rotate(Theta.value.toFloat())

        X = Distance(X.unit.toInches(X.value) + (velVec.x * deltaTime), DistanceUnit.INCH)
        Y = Distance(Y.unit.toInches(Y.value) + (velVec.y * deltaTime), DistanceUnit.INCH)

        /*
        val m1 = FL.currentPosition
        val m2 = FR.currentPosition
        val m3 = BL.currentPosition
        val m4 = BR.currentPosition

        if (m1 == lastM1 && m2 == lastM2 && m3 == lastM3 && m4 == lastM4) return

        val dispM1 = ((m1 - lastM1)/ TICKS_PER_REV) * 2 * PI * (14/8)
        val dispM2 = ((m2 - lastM2)/ TICKS_PER_REV) * 2 * PI * (14/8)
        val dispM3 = ((m3 - lastM3)/ TICKS_PER_REV) * 2 * PI * (14/8)
        val dispM4 = ((m4 - lastM4)/ TICKS_PER_REV) * 2 * PI * (14/8)

        val dispYr = (r/4) * (dispM1 + dispM2 + dispM3 + dispM4)
        val dispXr = (r/4) * (dispM1 - dispM2 - dispM3 + dispM4)
        val dispTr = (r/(4 * (5.25 + 2.875))) * (dispM1 - dispM2 + dispM3 - dispM4)

        Theta = Rotation(AngleUnit.normalizeRadians(dispTr + Theta.value), UnnormalizedAngleUnit.RADIANS)
        val workYr = dispYr - Y.unit.toInches(Y.value)
        val workXr = dispXr - X.unit.toInches(X.value)
        val vec = Vector2(workXr.toFloat(), workYr.toFloat()).rotate(Theta.unit.toRadians(Theta.value).toFloat())

        Y = Distance(Y.unit.toInches(Y.value) + vec.y, DistanceUnit.INCH)
        X = Distance(X.unit.toInches(X.value) + vec.x, DistanceUnit.INCH)

        lastM1 = m1
        lastM2 = m2
        lastM3 = m3
        lastM4 = m4
        */
    }

    enum class DriveStatus {
        UNINIT, INIT, READY, DIRECT_CONTROL, DRIVING_TO_POSITION
    }

}

infix fun Double.epsilonEquals(other: Double): Boolean = abs(this - other) < 1e-6

