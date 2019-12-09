package org.firstinspires.ftc.teamcode.legacy.subsystems

import android.util.Log
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit
import org.firstinspires.ftc.teamcode.legacy.lib.AutoDriveCommand
import org.firstinspires.ftc.teamcode.legacy.lib.Distance
import org.firstinspires.ftc.teamcode.legacy.lib.Rotation
import kotlin.math.PI
import kotlin.math.roundToInt

class MecanumDrivetrain {

    private lateinit var hardwareMap: HardwareMap
    val WHEEL_RADIUS = Distance(50.0, DistanceUnit.MM)
    val DRIVETRAIN_WIDTH = Distance(5.25, DistanceUnit.INCH)
    val DRIVETRAIN_LENGTH = Distance(2.875, DistanceUnit.INCH)
    val GEAR_RATIO: Double = 8.0 / 14.0
    val TICKS_PER_REV: Double = 1425.2
    val MAX_MOTOR_VELOCITY = Rotation(1.95 * 2 * PI, UnnormalizedAngleUnit.RADIANS)
    val MAX_WHEEL_VELOCITY = Rotation(1.95 * 2 * PI * (1 / GEAR_RATIO), UnnormalizedAngleUnit.RADIANS)

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
        FR.direction = DcMotorSimple.Direction.REVERSE
        BR.direction = DcMotorSimple.Direction.REVERSE
        BL.direction = DcMotorSimple.Direction.REVERSE
        changeEncoderMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER)

        driveStatus = DriveStatus.INIT

        changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)
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

        Log.i("Numbers", "$radius $x $y $w $drive $r ${command.power} $GEAR_RATIO $TICKS_PER_REV")

        val vfl = GEAR_RATIO * r * (x + y - (drive * w))
        val vfr = GEAR_RATIO * r * (x - y + (drive * w))
        val vbl = GEAR_RATIO * r * (x - y - (drive * w))
        val vbr = GEAR_RATIO * r * (x + y + (drive * w))

        val pfl = ((vfl / (2 * PI)) * TICKS_PER_REV)
        val pfr = ((vfr / (2 * PI)) * TICKS_PER_REV)
        val pbl = ((vbl / (2 * PI)) * TICKS_PER_REV)
        val pbr = ((vbr / (2 * PI)) * TICKS_PER_REV)

        FL.targetPosition += pfl.roundToInt()
        FR.targetPosition += pfr.roundToInt()
        BL.targetPosition += pbl.roundToInt()
        BR.targetPosition += pbr.roundToInt()

        driveStatus = DriveStatus.DRIVING_TO_POSITION

        Log.i("FL target position", "$pfl")

        changeEncoderMode(DcMotor.RunMode.RUN_TO_POSITION)

        with(command) {
            FL.power = power
            FR.power = power
            BL.power = power
            BR.power = power
        }

        return { activeOpMode: () -> Boolean, busyAction: () -> Unit ->
            while (FL.isBusy && activeOpMode()) {
                busyAction()
            }
            driveStatus = DriveStatus.READY

            changeEncoderMode(DcMotor.RunMode.RUN_USING_ENCODER)

            FL.power = 0.0
            FR.power = 0.0
            BL.power = 0.0
            BR.power = 0.0

            driveStatus

        }


    }

    enum class DriveStatus {
        UNINIT, INIT, READY, DIRECT_CONTROL, DRIVING_TO_POSITION
    }

}

