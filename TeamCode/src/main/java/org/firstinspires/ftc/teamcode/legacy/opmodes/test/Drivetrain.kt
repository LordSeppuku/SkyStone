package org.firstinspires.ftc.teamcode.RogueOpModes.Autonomous

import android.util.Log
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor.RunMode
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit
import kotlin.math.PI

class Drivetrain(val opMode: LinearOpMode) {
    val hardwareMap by lazy {
        opMode.hardwareMap
    }

    val FL by lazy {
        (hardwareMap["fl"] as DcMotorEx)
    }
    val FR by lazy {
        (hardwareMap["fr"] as DcMotorEx).apply {
            direction = DcMotorSimple.Direction.REVERSE
        }
    }
    val BL by lazy {
        (hardwareMap["bl"] as DcMotorEx)
    }
    val BR by lazy {
        (hardwareMap["br"] as DcMotorEx).apply {
            direction = DcMotorSimple.Direction.REVERSE
        }
    }

    fun setRunMode(runMode: RunMode?) {
        FR.mode = runMode
        FL.mode = runMode
        BR.mode = runMode
        BL.mode = runMode
        opMode.idle()
    }

    fun setTarget(FLtgtPos: Int, FRtgtPos: Int, BLtgtPos: Int, BRtgtPos: Int) {
        FL.targetPosition = FL.currentPosition + FLtgtPos
        FR.targetPosition = FR.currentPosition + FRtgtPos
        BL.targetPosition = BL.currentPosition + BLtgtPos
        BR.targetPosition = BR.currentPosition + BRtgtPos
    }

    fun encodedTurn(velocity: Velocity = Velocity(), targetAngle: Double = 0.0, targetUnit: UnnormalizedAngleUnit = UnnormalizedAngleUnit.DEGREES) {
        val yo = ((inWHEEL_SEPERATION_LENGTH + inWHEEL_SEPERATION_WIDTH)).toDouble()
        val FLvelocity = -yo * velocity.rotationalUnit.toRadians(velocity.z) / inWHEEL_RADIUS
        val FRvelocity = yo * velocity.rotationalUnit.toRadians(velocity.z) / inWHEEL_RADIUS
        val BLvelocity = -yo * velocity.rotationalUnit.toRadians(velocity.z) / inWHEEL_RADIUS
        val BRvelocity = yo * velocity.rotationalUnit.toRadians(velocity.z) / inWHEEL_RADIUS
        Log.i("FLvelocity", FLvelocity.toString())
        opMode.telemetry.addData("FLvelocity: ", FLvelocity.toString())
        opMode.telemetry.update()
        var time = targetUnit.toDegrees(targetAngle) / velocity.rotationalUnit.toDegrees(velocity.z)
        if (java.lang.Double.isInfinite(time)) {
            time = 0.0
        }
        val FLtarget = (FLvelocity * time * COUNTS_PER_INCH * inWHEEL_RADIUS).toInt()
        val FRtarget = (FRvelocity * time * COUNTS_PER_INCH * inWHEEL_RADIUS).toInt()
        val BLtarget = (BLvelocity * time * COUNTS_PER_INCH * inWHEEL_RADIUS).toInt()
        val BRtarget = (BRvelocity * time * COUNTS_PER_INCH * inWHEEL_RADIUS).toInt()
        Log.i("FL Target Position", FLtarget.toString())
        Log.i("Distance to travel", (FLtarget / COUNTS_PER_INCH * 0.6).toString())
        opMode.telemetry.addLine("Distance to travel: " + (FLtarget / COUNTS_PER_INCH * 0.6).toString())
        opMode.telemetry.update()
        setTarget(FLtarget, FRtarget, BLtarget, BRtarget)
        setRunMode(RunMode.RUN_TO_POSITION)
        FL.setVelocity(FLvelocity, AngleUnit.RADIANS)
        FR.setVelocity(FRvelocity, AngleUnit.RADIANS)
        BL.setVelocity(BLvelocity, AngleUnit.RADIANS)
        BR.setVelocity(BRvelocity, AngleUnit.RADIANS)
        while (opMode.opModeIsActive() && FL.isBusy && FR.isBusy && BL.isBusy && BR.isBusy) {
            opMode.telemetry.addData("FLMotor current pos: ", FL.currentPosition)
            opMode.telemetry.addData("FLMotor Target Position: ", FLtarget)
            opMode.telemetry.update()
        }
        FL.velocity = 0.0
        FR.velocity = 0.0
        BL.velocity = 0.0
        BR.velocity = 0.0
    }

    fun encodedDrive(robotVelocity: Velocity = Velocity(), distance: Double = 0.0, distanceUnit: DistanceUnit = DistanceUnit.INCH) {
        val y = robotVelocity.x
        val x = robotVelocity.y

        val FLvelocity = (robotVelocity.velocityUnit.toMeters(-x) - robotVelocity.velocityUnit.toMeters(y)) / DistanceUnit.INCH.toMeters(inWHEEL_RADIUS.toDouble())
        val FRvelocity = (robotVelocity.velocityUnit.toMeters(-x) + robotVelocity.velocityUnit.toMeters(y)) / DistanceUnit.INCH.toMeters(inWHEEL_RADIUS.toDouble())
        val BLvelocity = (robotVelocity.velocityUnit.toMeters(-x) + robotVelocity.velocityUnit.toMeters(y)) / DistanceUnit.INCH.toMeters(inWHEEL_RADIUS.toDouble())
        val BRvelocity = (robotVelocity.velocityUnit.toMeters(-x) - robotVelocity.velocityUnit.toMeters(y)) / DistanceUnit.INCH.toMeters(inWHEEL_RADIUS.toDouble())
        Log.i("FLvelocity", FLvelocity.toString())
        val Xtime = if (java.lang.Double.isInfinite(distanceUnit.toMeters(distance) / robotVelocity.velocityUnit.toMeters(robotVelocity.x))) 0.0 else distanceUnit.toMeters(distance) / robotVelocity.velocityUnit.toMeters(robotVelocity.x)
        val Ytime = if (java.lang.Double.isInfinite(distanceUnit.toMeters(distance) / robotVelocity.velocityUnit.toMeters(robotVelocity.y))) 0.0 else distanceUnit.toMeters(distance) / robotVelocity.velocityUnit.toMeters(robotVelocity.y)
        var time = 0.0
        if (Xtime != 0.0 && Ytime == 0.0) {
            time = Xtime
        } else if (Ytime != 0.0 && Xtime == 0.0) {
            time = Ytime
        } else {
            time += Xtime + Ytime
            time /= 2
        }
        Log.i("Time", time.toString())
        val FLtarget = (FLvelocity * time * COUNTS_PER_INCH * inWHEEL_RADIUS).toInt()
        val FRtarget = (FRvelocity * time * COUNTS_PER_INCH * inWHEEL_RADIUS).toInt()
        val BLtarget = (BLvelocity * time * COUNTS_PER_INCH * inWHEEL_RADIUS).toInt()
        val BRtarget = (BRvelocity * time * COUNTS_PER_INCH * inWHEEL_RADIUS).toInt()
        Log.i("FL Target Position", FLtarget.toString())
        Log.i("Distance to travel", (FLtarget / COUNTS_PER_INCH * 0.6).toString())
        opMode.telemetry.addLine("Distance to travel: " + (FLtarget / COUNTS_PER_INCH * 0.6).toString())
        opMode.telemetry.update()
        setTarget(FLtarget, FRtarget, BLtarget, BRtarget)
        setRunMode(RunMode.RUN_TO_POSITION)
        FL.setVelocity(FLvelocity, AngleUnit.RADIANS)
        FR.setVelocity(FRvelocity, AngleUnit.RADIANS)
        BL.setVelocity(BLvelocity, AngleUnit.RADIANS)
        BR.setVelocity(BRvelocity, AngleUnit.RADIANS)
        while (opMode.opModeIsActive() && FL.isBusy && FR.isBusy && BL.isBusy && BR.isBusy) {
            opMode.telemetry.addData("FLMotor current pos: ", FL.currentPosition)
            opMode.telemetry.addData("FLMotor Target Position: ", FLtarget)
            opMode.telemetry.update()
        }
        FL.velocity = 0.0
        FR.velocity = 0.0
        BL.velocity = 0.0
        BR.velocity = 0.0
        setRunMode(RunMode.STOP_AND_RESET_ENCODER)
    }

    class Velocity(val x: Double = 0.0, val y: Double = 0.0, val z: Double = 0.0, val velocityUnit: DistanceUnit = DistanceUnit.INCH, val rotationalUnit: UnnormalizedAngleUnit = UnnormalizedAngleUnit.DEGREES) {
        fun toUnit(newVelocityUnit: DistanceUnit, newRotationalUnit: UnnormalizedAngleUnit): Velocity {
            return Velocity(
                    newVelocityUnit.fromUnit(velocityUnit, x),
                    newVelocityUnit.fromUnit(velocityUnit, y),
                    newRotationalUnit.fromUnit(rotationalUnit, z),
                    newVelocityUnit,
                    newRotationalUnit
            )
        }

    }

    companion object {
        // Encoder Details (Andymark NeveRest Classic 40)
        const val amNeverestClassicFourty_COUNTS_PER_REVOLUTION = 1425.2 * 0.9
        val inWHEEL_RADIUS = DistanceUnit.INCH.fromMm(50.0)
        val COUNTS_PER_INCH: Double = (amNeverestClassicFourty_COUNTS_PER_REVOLUTION / (inWHEEL_RADIUS * 2 * PI)) * 8.0 / 14.0
        const val inWHEEL_SEPERATION_WIDTH = 2.875
        const val inWHEEL_SEPERATION_LENGTH = 5.25

        const val latcherTgtPos = -6850
    }
}