package org.firstinspires.ftc.teamcode.badlib

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit

class MecanumDrivetrain(val opMode: OpMode) {

    val hardwareMap = opMode.hardwareMap

    val WHEEL_RADIUS = Distance(100.0, DistanceUnit.MM)
    val DRIVETRAIN_WIDTH = Distance(5.25, DistanceUnit.INCH)
    val DRIVETRAIN_LENGTH = Distance(2.875, DistanceUnit.INCH)

    val FL: DcMotorEx
        get() = hardwareMap.dcMotor.get("fl") as DcMotorEx

    val FR: DcMotorEx
        get() = hardwareMap.dcMotor.get("fr") as DcMotorEx

    val BL: DcMotorEx
        get() = hardwareMap.dcMotor.get("bl") as DcMotorEx

    val BR: DcMotorEx
        get() = hardwareMap.dcMotor.get("br") as DcMotorEx

    fun arcadeDrive(x: Double, y: Double, c: Double) {
        FL.power = Range.clip(x + y + c, -1.0, 1.0)
        FR.power = Range.clip(-x + y - c, -1.0, 1.0)
        BL.power = Range.clip(-x + y + c, -1.0, 1.0)
        BR.power = Range.clip(x + y -c, -1.0, 1.0)
    }

    fun tankDrive(y1: Double, y2: Double, lt: Double, rt: Double) {
        FL.power = Range.clip(y1 - lt, -1.0, 1.0)
        FR.power = Range.clip(y2 - rt, -1.0, 1.0)
        BL.power = Range.clip(y1 + lt, -1.0, 1.0)
        BR.power = Range.clip(y2 + rt, -1.0, 1.0)
    }

    fun setAutoDrive(command: AutoDriveCommand) {

        val radius = WHEEL_RADIUS.run { unit.toMeters(value) }
        val x = command.x.run { unit.toMeters(value) }
        val y = command.y.run { unit.toMeters(value) }
        val w = command.rotation.run { unit.toRadians(value) }
        val drive = DRIVETRAIN_WIDTH.run { unit.toMeters(value) } + DRIVETRAIN_LENGTH.run { unit.toMeters(value) }
        val r = (1.0/radius)

        FL.setVelocity(r * (x - y - (drive * w)), AngleUnit.RADIANS)
        FR.setVelocity(r * (x + y + (drive * w)), AngleUnit.RADIANS)
        BL.setVelocity(r * (x + y - (drive * w)), AngleUnit.RADIANS)
        BR.setVelocity(r * (x - y + (drive * w)), AngleUnit.RADIANS)



    }

    enum class DriveStatus {
        COMPLETED
    }

}

data class AutoDriveCommand(val x: Distance, val y: Distance, val rotation: Rotation, val powah: Double)

data class Distance(val value: Double = 0.0, val unit: DistanceUnit = DistanceUnit.INCH)

data class Rotation(val value: Double = 0.0, val unit: UnnormalizedAngleUnit = UnnormalizedAngleUnit.DEGREES)