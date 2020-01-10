package org.firstinspires.ftc.teamcode.legacy.subsystems

import android.util.Log
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit
import org.firstinspires.ftc.teamcode.legacy.lib.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class MecanumDrivetrain {

    private lateinit var hardwareMap: HardwareMap

    companion object {
        val WHEEL_RADIUS = Distance(50.0, DistanceUnit.MM)
        val DRIVETRAIN_WIDTH = Distance(5.25, DistanceUnit.INCH)
        val DRIVETRAIN_LENGTH = Distance(2.875, DistanceUnit.INCH)
        val GEAR_RATIO: Double = 8.0 / 14.0
        val TICKS_PER_REV: Double = 1425.2
        val WHEEL_DISPL_PER_COUNT = Distance((WHEEL_RADIUS.unit.toInches(WHEEL_RADIUS.value) * 2 * PI * (1 / GEAR_RATIO)) / TICKS_PER_REV)
        val MAX_MOTOR_VELOCITY = Rotation(1.95 * 2 * PI, UnnormalizedAngleUnit.RADIANS)
        val MAX_WHEEL_VELOCITY = Rotation(1.95 * 2 * PI * (1 / GEAR_RATIO), UnnormalizedAngleUnit.RADIANS)
    }


    private var lastM0 = 0
    private var lastM1 = 0
    private var lastM2 = 0
    private var lastM3 = 0
    private var lastTheta = 0.0

    private var lastX = 0.0
    private var lastY = 0.0
    var X = Distance()
        private set
    var Y = Distance()
        private set
    var Theta = Rotation()
        private set

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

    fun init(hardwareMap: HardwareMap) {
        if (driveStatus != DriveStatus.UNINIT) return

        this.hardwareMap = hardwareMap
        FL.direction = DcMotorSimple.Direction.REVERSE
        /*
        FR.direction = DcMotorSimple.Direction.REVERSE
        BR.direction = DcMotorSimple.Direction.REVERSE
        BL.direction = DcMotorSimple.Direction.REVERSE
         */
        changeEncoderMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER)

        driveStatus = DriveStatus.INIT

        changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)
    }

    fun init(hardwareMap: HardwareMap, fieldPosition: Pose) {
        init(hardwareMap)
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
        val x = -horizontal.pow(3)
        val w = c.pow(3)

        FL.power = Range.clip(y - x - w, -1.0, 1.0)
        FR.power = Range.clip(y + x + w, -1.0, 1.0)
        BL.power = Range.clip(y + x - w, -1.0, 1.0)
        BR.power = Range.clip(y - x + w, -1.0, 1.0)
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

    fun driveWithVelocity(command: DriveWithVelocityCommand) {
        changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)

        val radius = WHEEL_RADIUS.run { unit.toMeters(value) }
        val x = command.lateral.run { unit.toMeters(value) }
        val y = command.horizontal.run { unit.toMeters(value) }
        val w = command.rotation.run { unit.toRadians(value) }
        val drive = DRIVETRAIN_WIDTH.run { unit.toMeters(value) } + DRIVETRAIN_LENGTH.run { unit.toMeters(value) }
        val r = (1.0 / radius)

        val vfl = (GEAR_RATIO * r) * (x - y - (drive * w))
        val vfr = (GEAR_RATIO * r) * (x + y + (drive * w))
        val vbl = (GEAR_RATIO * r) * (x + y - (drive * w))
        val vbr = (GEAR_RATIO * r) * (x - y + (drive * w))

        FL.setVelocity(vfl, AngleUnit.RADIANS)
        FR.setVelocity(vfr, AngleUnit.RADIANS)
        BL.setVelocity(vbl, AngleUnit.RADIANS)
        BR.setVelocity(vbr, AngleUnit.RADIANS)
    }

    fun driveToPosition(command: DriveToPositionCommand): (() -> Boolean, () -> Unit) -> DriveStatus {

        FL.power = 0.0
        FR.power = 0.0
        BL.power = 0.0
        BR.power = 0.0

        changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)

        val radius = WHEEL_RADIUS.run { unit.toMeters(value) }
        val x = command.lateral.run { unit.toMeters(value) }
        val y = command.horizontal.run { unit.toMeters(value) }
        val w = command.rotation.run { unit.toRadians(value) }
        val drive = DRIVETRAIN_WIDTH.run { unit.toMeters(value) } + DRIVETRAIN_LENGTH.run { unit.toMeters(value) }
        val r = (1.0 / radius)

        Log.i("Numbers", "$radius $x $y $w $drive $r ${command.power} $GEAR_RATIO $TICKS_PER_REV")

        val vfl = GEAR_RATIO * r * (-x - y - (drive * w))
        val vfr = GEAR_RATIO * r * (-x + y + (drive * w))
        val vbl = GEAR_RATIO * r * (-x + y - (drive * w))
        val vbr = GEAR_RATIO * r * (-x - y + (drive * w))

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

        return { activeOpMode: () -> Boolean, busyAction: () -> Unit ->

            changeEncoderMode(DcMotor.RunMode.RUN_TO_POSITION)

            with(command) {
                FL.power = power
                FR.power = power
                BL.power = power
                BR.power = power
            }

            while (FL.isBusy
                    && activeOpMode()) {
                busyAction()
            }
            driveStatus = DriveStatus.READY

            FL.power = 0.0
            FR.power = 0.0
            BL.power = 0.0
            BR.power = 0.0

            changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)

            driveStatus

        }
    }

    fun localization(deltaTime: Double) {
        val r = WHEEL_RADIUS.run { unit.toMeters(value) }

        val velW1 = FL.getVelocity(AngleUnit.RADIANS) * 1 / GEAR_RATIO
        val velW2 = FR.getVelocity(AngleUnit.RADIANS) * 1 / GEAR_RATIO
        val velW3 = BL.getVelocity(AngleUnit.RADIANS) * 1 / GEAR_RATIO
        val velW4 = BR.getVelocity(AngleUnit.RADIANS) * 1 / GEAR_RATIO

        val vX = r / 4 * (velW1 + velW2 + velW3 + velW4)
        val vY = r / 4 * (-velW1 + velW2 + velW3 - velW4)
        val wZ = r / (4 * (DRIVETRAIN_WIDTH.run { unit.toMeters(value) } + DRIVETRAIN_LENGTH.run { unit.toMeters(value) })) * (-velW1 + velW2 - velW3 + velW4)

        val deltX = vX * deltaTime
        val deltY = vY * deltaTime
        val deltW = wZ * deltaTime

        val workX = X.unit.toMeters(X.value)
        val workY = Y.unit.toMeters(Y.value)
        val workW = Theta.unit.toRadians(Theta.value)

        X = Distance(workY + deltY, DistanceUnit.METER)
        Y = Distance(workX + deltX, DistanceUnit.METER)
        Theta = Rotation(workW + deltW, UnnormalizedAngleUnit.RADIANS)

        lastX = X.value
        lastY = Y.value
        lastTheta = Theta.value
    }

    fun localization(imu: IMU) {
        val deltM0 = FL.currentPosition - lastM0
        val deltM1 = FR.currentPosition - lastM1
        val deltM2 = BL.currentPosition - lastM2
        val deltM3 = BR.currentPosition - lastM3

        val displM0 = deltM0 * WHEEL_DISPL_PER_COUNT.unit.toInches(WHEEL_DISPL_PER_COUNT.value)
        val displM1 = deltM1 * WHEEL_DISPL_PER_COUNT.unit.toInches(WHEEL_DISPL_PER_COUNT.value)
        val displM2 = deltM2 * WHEEL_DISPL_PER_COUNT.unit.toInches(WHEEL_DISPL_PER_COUNT.value)
        val displM3 = deltM3 * WHEEL_DISPL_PER_COUNT.unit.toInches(WHEEL_DISPL_PER_COUNT.value)

        val displAvg = (displM0 + displM1 + displM2 + displM3) / 4.0

        val yo = (WHEEL_RADIUS.unit.toInches(WHEEL_RADIUS.value) / (4) * DRIVETRAIN_WIDTH.run { unit.toMeters(value) } + DRIVETRAIN_LENGTH.run { unit.toMeters(value) })

        val devM0 = displM0 + displAvg * yo
        val devM1 = displM1 - displAvg * yo
        val devM2 = displM2 + displAvg * yo
        val devM3 = displM3 - displAvg * yo

        val deltXr = (-devM0 + devM1 + devM2 - devM3) * (WHEEL_RADIUS.unit.toInches(WHEEL_RADIUS.value) / 4)
        val deltYr = (devM0 + devM1 + devM2 + devM3) * (WHEEL_RADIUS.unit.toInches(WHEEL_RADIUS.value) / 4)
        /*
        TODO: Utilize just displAvg to get this bad boy, with positive or negative determined by the sign of displM#

        val deltWr = (-devM0 + devM1 - devM2 + devM3) * (WHEEL_RADIUS.unit.toInches(WHEEL_RADIUS.value)/(4 * (
                DRIVETRAIN_LENGTH.unit.toInches(DRIVETRAIN_LENGTH.value) +
                        DRIVETRAIN_WIDTH.unit.toInches(DRIVETRAIN_WIDTH.value)
                )))
        */

        val robotTheta = imu.angles().firstAngle
        val sinTheta = sin(robotTheta)
        val cosTheta = cos(robotTheta)

        val deltXf = deltXr * cosTheta - deltYr * sinTheta
        val deltYf = deltYr * cosTheta + deltXr * sinTheta

        X = Distance(lastX + deltYf)
        Y = Distance(lastY + deltXf)
        lastX = X.value
        lastY = Y.value
        //Theta = Rotation(Theta.value + (robotTheta.toDouble() - lastTheta))
        lastM0 = FL.currentPosition
        lastM1 = FR.currentPosition
        lastM2 = BL.currentPosition
        lastM3 = BR.currentPosition
    }

    enum class DriveStatus {
        UNINIT, INIT, READY, DIRECT_CONTROL, DRIVING_TO_POSITION
    }

}

