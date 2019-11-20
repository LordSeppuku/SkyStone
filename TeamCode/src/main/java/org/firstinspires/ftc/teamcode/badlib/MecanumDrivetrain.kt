package org.firstinspires.ftc.teamcode.badlib

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit
import kotlin.math.roundToInt

class MecanumDrivetrain(private val hardwareMap: HardwareMap) {

    val WHEEL_RADIUS = Distance(100.0, DistanceUnit.MM)
    val DRIVETRAIN_WIDTH = Distance(5.25, DistanceUnit.INCH)
    val DRIVETRAIN_LENGTH = Distance(2.875, DistanceUnit.INCH)
    val GEAR_RATIO = (8 / 14).toDouble()
    val TICKS_PER_REV = 1425.2

    private val FL: DcMotorEx
        get() = hardwareMap.dcMotor.get("fl") as DcMotorEx

    private val FR: DcMotorEx
        get() = hardwareMap.dcMotor.get("fr") as DcMotorEx

    private val BL: DcMotorEx
        get() = hardwareMap.dcMotor.get("bl") as DcMotorEx

    private val BR: DcMotorEx
        get() = hardwareMap.dcMotor.get("br") as DcMotorEx

    fun init() {
        changeEncoderMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER)
    }

    fun changeEncoderMode(runMode: DcMotor.RunMode): DcMotor.RunMode {

        if (FL.mode == runMode) return FL.mode

        FL.mode = runMode
        FR.mode = runMode
        BL.mode = runMode
        BR.mode = runMode

        return FL.mode
    }



    fun arcadeDrive(x: Double, y: Double, c: Double) {
        changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)

        FL.power = Range.clip(x + y + c, -1.0, 1.0)
        FR.power = Range.clip(-x + y - c, -1.0, 1.0)
        BL.power = Range.clip(-x + y + c, -1.0, 1.0)
        BR.power = Range.clip(x + y - c, -1.0, 1.0)
    }

    fun tankDrive(y1: Double, y2: Double, lt: Double, rt: Double) {
        changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)

        FL.power = Range.clip(y1 - lt, -1.0, 1.0)
        FR.power = Range.clip(y2 - rt, -1.0, 1.0)
        BL.power = Range.clip(y1 + lt, -1.0, 1.0)
        BR.power = Range.clip(y2 + rt, -1.0, 1.0)
    }

    fun setAutoDrive(command: AutoDriveCommand): () -> Boolean {

        changeEncoderMode(DcMotor.RunMode.RUN_TO_POSITION)

        val radius = WHEEL_RADIUS.run { unit.toMeters(value) }
        val x = command.x.run { unit.toMeters(value) }
        val y = command.y.run { unit.toMeters(value) }
        val w = command.rotation.run { unit.toRadians(value) }
        val drive = DRIVETRAIN_WIDTH.run { unit.toMeters(value) } + DRIVETRAIN_LENGTH.run { unit.toMeters(value) }
        val r = (1.0 / radius)

        val vfl = GEAR_RATIO * r * (x - y - (drive * w))
        val vfr = GEAR_RATIO * r * (x + y + (drive * w))
        val vbl = GEAR_RATIO * r * (x + y - (drive * w))
        val vbr = GEAR_RATIO * r * (x - y + (drive * w))

        val tfl = (vfl * command.time * TICKS_PER_REV).roundToInt()
        val tfr = (vfr * command.time * TICKS_PER_REV).roundToInt()
        val tbl = (vbl * command.time * TICKS_PER_REV).roundToInt()
        val tbr = (vbr * command.time * TICKS_PER_REV).roundToInt()

        FL.targetPosition += tfl
        FR.targetPosition += tfr
        BL.targetPosition += tbl
        BR.targetPosition += tbr

        FL.setVelocity(vfl, AngleUnit.RADIANS)
        FR.setVelocity(vfr, AngleUnit.RADIANS)
        BL.setVelocity(vbl, AngleUnit.RADIANS)
        BR.setVelocity(vbr, AngleUnit.RADIANS)

        return { FL.isBusy }

    }
}

data class AutoDriveCommand(val x: Distance, val y: Distance, val rotation: Rotation, val time: Double)

data class Distance(val value: Double = 0.0, val unit: DistanceUnit = DistanceUnit.INCH)

data class Rotation(val value: Double = 0.0, val unit: UnnormalizedAngleUnit = UnnormalizedAngleUnit.DEGREES)