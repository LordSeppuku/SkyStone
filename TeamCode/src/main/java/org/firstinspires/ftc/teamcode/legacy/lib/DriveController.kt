package org.firstinspires.ftc.teamcode.legacy.lib

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit
import org.firstinspires.ftc.teamcode.legacy.subsystems.MecanumDrivetrain
import kotlin.math.PI
import kotlin.math.abs

const val EPSILON = 1e-6

infix fun Double.epsilonEquals(other: Double) = abs(this - other) < EPSILON

object DriveController {

    @JvmStatic
    fun singleAxisTargetLinVel(target: Distance, current: Distance = Distance()): Distance {
        val tgtX = target.unit.toInches(target.value)
        val curX = current.unit.toInches(current.value)
        val work = tgtX - curX

        if (work epsilonEquals 0.0) return Distance(0.0)

        return Distance(work, DistanceUnit.INCH)
    }

    @JvmStatic
    fun singleAxisTargetRotVel(target: Rotation, current: Rotation = Rotation()): Rotation =
            Rotation(((target.unit.toRadians(target.value)
                    - current.unit.toRadians(current.value))) % (2 * PI), UnnormalizedAngleUnit.RADIANS).run {
                if (this.value epsilonEquals 0.0) return Rotation()
                return this
            }

    @JvmStatic
    fun produceDriveCommand(drivetrain: MecanumDrivetrain, targetPose: Pose): DriveWithVelocityCommand {
        /*

        var rotFieldError = (targetPose.w.unit.toRadians(targetPose.w.value)
                - drivetrain.Theta.unit.toRadians(drivetrain.Theta.value)) + (20 * PI) % (2 * PI)

        if (rotFieldError > PI) rotFieldError -= 2 * PI

         */

        return DriveWithVelocityCommand(
                singleAxisTargetLinVel(targetPose.y, drivetrain.X),
                singleAxisTargetLinVel(targetPose.x, drivetrain.Y),
                singleAxisTargetRotVel(drivetrain.Theta, Rotation())
        )
    }
}