package org.firstinspires.ftc.teamcode.RogueOpModes.Autonomous

import android.util.Log
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.RunMode
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit
import kotlin.math.PI

@Disabled
@Autonomous(name = "Crater Side OP", group = "Theoretical")
class CraterSideOP : LinearOpMode() {
    var FL: DcMotorEx? = null
    var FR: DcMotorEx? = null
    var BL: DcMotorEx? = null
    var BR: DcMotorEx? = null
    var Latcher: DcMotor? = null
    private var sampleState: SampleState? = null
    override fun runOpMode() {
        FL = hardwareMap.dcMotor["FL"] as DcMotorEx
        FR = hardwareMap.dcMotor["FR"] as DcMotorEx
        BL = hardwareMap.dcMotor["BL"] as DcMotorEx
        BR = hardwareMap.dcMotor["BR"] as DcMotorEx
        Latcher = hardwareMap.dcMotor["aux1"]
        telemetry.addLine("Initialized motors successfully.")
        telemetry.update()
        BR!!.direction = DcMotorSimple.Direction.REVERSE
        FR!!.direction = DcMotorSimple.Direction.REVERSE
        setRunMode(RunMode.STOP_AND_RESET_ENCODER)
        telemetry.addLine("Init phase clear.")
        telemetry.update()
        waitForStart()
        telemetry.clearAll()
        encodedDrive(Velocity(0.0, 6.0, 0.0, DistanceUnit.INCH, UnnormalizedAngleUnit.RADIANS), -3.0, DistanceUnit.INCH)
        encodedDrive(Velocity(6.0, 0.0, 0.0, DistanceUnit.INCH, UnnormalizedAngleUnit.RADIANS), 3.0, DistanceUnit.INCH)
        encodedDrive(Velocity(0.0, 6.0, 0.0, DistanceUnit.INCH, UnnormalizedAngleUnit.RADIANS), 3.0, DistanceUnit.INCH)
        encodedDrive(Velocity(9.0, 0.0, 0.0, DistanceUnit.INCH, UnnormalizedAngleUnit.DEGREES), 21 - 11.25, DistanceUnit.INCH)

        /*
        encodedTurn(new Velocity(0.0, 0.0, 45, DistanceUnit.INCH, UnnormalizedAngleUnit.DEGREES), 90, UnnormalizedAngleUnit.DEGREES);
        encodedDrive(new Velocity(6.0, 0.0, 0.0, DistanceUnit.INCH, UnnormalizedAngleUnit.RADIANS), 50, DistanceUnit.INCH);
        encodedTurn(new Velocity(0.0, 0.0, 45, DistanceUnit.INCH, UnnormalizedAngleUnit.DEGREES), -45, UnnormalizedAngleUnit.DEGREES);
        encodedDrive(new Velocity(12.0, 0.0, 0.0, DistanceUnit.INCH, UnnormalizedAngleUnit.DEGREES), 40, DistanceUnit.INCH);
*/
    }

    fun setRunMode(runMode: RunMode?) {
        FR!!.mode = runMode
        FL!!.mode = runMode
        BR!!.mode = runMode
        BL!!.mode = runMode
        idle()
    }

    fun setTarget(FLtgtPos: Int, FRtgtPos: Int, BLtgtPos: Int, BRtgtPos: Int) {
        FL!!.targetPosition = FL!!.currentPosition + FLtgtPos
        FR!!.targetPosition = FR!!.currentPosition + FRtgtPos
        BL!!.targetPosition = BL!!.currentPosition + BLtgtPos
        BR!!.targetPosition = BR!!.currentPosition + BRtgtPos
    }

    private fun encodedTurn(velocity: Velocity, targetAngle: Double, targetUnit: UnnormalizedAngleUnit) {
        val yo = ((inWHEEL_SEPERATION_LENGTH + inWHEEL_SEPERATION_WIDTH) / 2).toDouble()
        val FLvelocity = -yo * velocity.rotationalUnit.toRadians(velocity.z) / inWHEEL_RADIUS * 0.6
        val FRvelocity = yo * velocity.rotationalUnit.toRadians(velocity.z) / inWHEEL_RADIUS * 0.6
        val BLvelocity = -yo * velocity.rotationalUnit.toRadians(velocity.z) / inWHEEL_RADIUS * 0.6
        val BRvelocity = yo * velocity.rotationalUnit.toRadians(velocity.z) / inWHEEL_RADIUS * 0.6
        Log.i("FLvelocity", FLvelocity.toString())
        telemetry.addData("FLvelocity: ", FLvelocity.toString())
        telemetry.update()
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
        telemetry.addLine("Distance to travel: " + (FLtarget / COUNTS_PER_INCH * 0.6).toString())
        telemetry.update()
        setTarget(FLtarget, FRtarget, BLtarget, BRtarget)
        setRunMode(RunMode.RUN_TO_POSITION)
        FL!!.setVelocity(FLvelocity, AngleUnit.RADIANS)
        FR!!.setVelocity(FRvelocity, AngleUnit.RADIANS)
        BL!!.setVelocity(BLvelocity, AngleUnit.RADIANS)
        BR!!.setVelocity(BRvelocity, AngleUnit.RADIANS)
        while (opModeIsActive() && FL!!.isBusy && FR!!.isBusy && BL!!.isBusy && BR!!.isBusy) {
            telemetry.addData("FLMotor current pos: ", FL!!.currentPosition)
            telemetry.addData("FLMotor Target Position: ", FLtarget)
            telemetry.update()
        }
        FL!!.velocity = 0.0
        FR!!.velocity = 0.0
        BL!!.velocity = 0.0
        BR!!.velocity = 0.0
    }

    private fun encodedDrive(robotVelocity: Velocity, distance: Double, distanceUnit: DistanceUnit) {
        val FLvelocity = (robotVelocity.velocityUnit.toMeters(-robotVelocity.x) - robotVelocity.velocityUnit.toMeters(robotVelocity.y)) / DistanceUnit.INCH.toMeters(inWHEEL_RADIUS.toDouble()) * 0.6
        val FRvelocity = (robotVelocity.velocityUnit.toMeters(-robotVelocity.x) + robotVelocity.velocityUnit.toMeters(robotVelocity.y)) / DistanceUnit.INCH.toMeters(inWHEEL_RADIUS.toDouble()) * 0.6
        val BLvelocity = (robotVelocity.velocityUnit.toMeters(-robotVelocity.x) + robotVelocity.velocityUnit.toMeters(robotVelocity.y)) / DistanceUnit.INCH.toMeters(inWHEEL_RADIUS.toDouble()) * 0.6
        val BRvelocity = (robotVelocity.velocityUnit.toMeters(-robotVelocity.x) - robotVelocity.velocityUnit.toMeters(robotVelocity.y)) / DistanceUnit.INCH.toMeters(inWHEEL_RADIUS.toDouble()) * 0.6
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
            time = time / 2
        }
        Log.i("Time", time.toString())
        val FLtarget = (FLvelocity * time * COUNTS_PER_INCH * inWHEEL_RADIUS).toInt()
        val FRtarget = (FRvelocity * time * COUNTS_PER_INCH * inWHEEL_RADIUS).toInt()
        val BLtarget = (BLvelocity * time * COUNTS_PER_INCH * inWHEEL_RADIUS).toInt()
        val BRtarget = (BRvelocity * time * COUNTS_PER_INCH * inWHEEL_RADIUS).toInt()
        Log.i("FL Target Position", FLtarget.toString())
        Log.i("Distance to travel", (FLtarget / COUNTS_PER_INCH * 0.6).toString())
        telemetry.addLine("Distance to travel: " + (FLtarget / COUNTS_PER_INCH * 0.6).toString())
        telemetry.update()
        setTarget(FLtarget, FRtarget, BLtarget, BRtarget)
        setRunMode(RunMode.RUN_TO_POSITION)
        FL!!.setVelocity(FLvelocity, AngleUnit.RADIANS)
        FR!!.setVelocity(FRvelocity, AngleUnit.RADIANS)
        BL!!.setVelocity(BLvelocity, AngleUnit.RADIANS)
        BR!!.setVelocity(BRvelocity, AngleUnit.RADIANS)
        while (opModeIsActive() && FL!!.isBusy && FR!!.isBusy && BL!!.isBusy && BR!!.isBusy) {
            telemetry.addData("FLMotor current pos: ", FL!!.currentPosition)
            telemetry.addData("FLMotor Target Position: ", FLtarget)
            telemetry.update()
        }
        FL!!.velocity = 0.0
        FR!!.velocity = 0.0
        BL!!.velocity = 0.0
        BR!!.velocity = 0.0
        setRunMode(RunMode.STOP_AND_RESET_ENCODER)
    }

    internal inner class Velocity(val x: Double, val y: Double, val z: Double, val velocityUnit: DistanceUnit, val rotationalUnit: UnnormalizedAngleUnit) {
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

    internal enum class SampleState {
        LEFT, CENTER, RIGHT
    }

    companion object {
        // Encoder Details (Andymark NeveRest Classic 40)
        const val amNeverestClassicFourty_COUNTS_PER_REVOLUTION = 1425.2
        val inWHEEL_RADIUS = DistanceUnit.INCH.fromMm(100.0)
        val COUNTS_PER_INCH: Double = (amNeverestClassicFourty_COUNTS_PER_REVOLUTION / (inWHEEL_RADIUS * 2 * PI)) * 8.0 / 14.0
        const val inWHEEL_SEPERATION_WIDTH = 2.875
        const val inWHEEL_SEPERATION_LENGTH = 5.25

        const val latcherTgtPos = -6850
    }
}