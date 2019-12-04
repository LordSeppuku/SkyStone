package org.firstinspires.ftc.teamcode.legacy.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.legacy.lib.AutoDriveCommand
import org.firstinspires.ftc.teamcode.legacy.lib.Distance
import kotlin.math.roundToInt

class MecanumDrivetrain {

    private lateinit var hardwareMap: HardwareMap
    val WHEEL_RADIUS = Distance(100.0, DistanceUnit.MM)
    val DRIVETRAIN_WIDTH = Distance(5.25, DistanceUnit.INCH)
    val DRIVETRAIN_LENGTH = Distance(2.875, DistanceUnit.INCH)
    val GEAR_RATIO = (8 / 14).toDouble()
    val TICKS_PER_REV = 1425.2

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
        this.hardwareMap = hardwareMap
        FR.direction = DcMotorSimple.Direction.REVERSE
        BR.direction = DcMotorSimple.Direction.REVERSE
        BL.direction = DcMotorSimple.Direction.REVERSE
        changeEncoderMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER)

        driveStatus = DriveStatus.INIT
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

        changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)

        FL.power = Range.clip(lateral + horizontal - c, -1.0, 1.0)
        FR.power = Range.clip(lateral - horizontal + c, -1.0, 1.0)
        BL.power = Range.clip(lateral - horizontal - c, -1.0, 1.0)
        BR.power = Range.clip(lateral + horizontal + c, -1.0, 1.0)
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

    fun setAutoDrive(command: AutoDriveCommand): (() -> Boolean, () -> Unit) -> DriveStatus {

        changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)

        val radius = WHEEL_RADIUS.run { unit.toMeters(value) }
        val x = command.lateral.run { unit.toMeters(value) }
        val y = command.horizontal.run { unit.toMeters(value) }
        val w = command.rotation.run { unit.toRadians(value) }
        val drive = DRIVETRAIN_WIDTH.run { unit.toMeters(value) } + DRIVETRAIN_LENGTH.run { unit.toMeters(value) }
        val r = (1.0 / radius)

        val vfl = GEAR_RATIO * r * (x + y - (drive * w))
        val vfr = GEAR_RATIO * r * (x - y + (drive * w))
        val vbl = GEAR_RATIO * r * (x - y - (drive * w))
        val vbr = GEAR_RATIO * r * (x + y + (drive * w))

        val tfl = (vfl * command.time * TICKS_PER_REV).roundToInt()
        val tfr = (vfr * command.time * TICKS_PER_REV).roundToInt()
        val tbl = (vbl * command.time * TICKS_PER_REV).roundToInt()
        val tbr = (vbr * command.time * TICKS_PER_REV).roundToInt()

        FL.targetPosition = FL.targetPosition + tfl
        FR.targetPosition = FR.targetPosition + tfr
        BL.targetPosition = BL.targetPosition + tbl
        BR.targetPosition = BR.targetPosition + tbr

        driveStatus = DriveStatus.DRIVING_TO_POS

        changeEncoderMode(DcMotor.RunMode.RUN_TO_POSITION)

        FL.setVelocity(vfl, AngleUnit.RADIANS)
        FR.setVelocity(vfr, AngleUnit.RADIANS)
        BL.setVelocity(vbl, AngleUnit.RADIANS)
        BR.setVelocity(vbr, AngleUnit.RADIANS)

        return { activeOpMode: () -> Boolean, busyAction: () -> Unit ->
            while (FL.isBusy && activeOpMode()) busyAction()
            driveStatus = DriveStatus.COMPLETED
            driveStatus
        }

    }

    enum class DriveStatus {
        UNINIT, INIT, READY, DIRECT_CONTROL, DRIVING_TO_POS, COMPLETED
    }

}

