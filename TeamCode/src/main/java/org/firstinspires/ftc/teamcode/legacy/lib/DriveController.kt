package org.firstinspires.ftc.teamcode.legacy.lib

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit
import org.firstinspires.ftc.teamcode.legacy.subsystems.MecanumDrivetrain
import kotlin.math.PI

object DriveController {

    @JvmStatic
    fun singleAxisTargetLinVel(target: Distance, current: Distance = Distance()): Distance {
        val tgtX = target.unit.toInches(target.value)
        val curX = current.unit.toInches(current.value)

        return Distance(tgtX - curX, DistanceUnit.INCH)
    }

    @JvmStatic
    fun singleAxisTargetRotVel(target: Rotation, current: Rotation = Rotation()): Rotation =
            Rotation((target.unit.toRadians(target.value)
                    - current.unit.toRadians(current.value)) % (2 * PI), UnnormalizedAngleUnit.RADIANS)

    @JvmStatic
    fun produceDriveCommand(drivetrain: MecanumDrivetrain, targetPose: Pose): DriveWithVelocityCommand {
        var rotFieldError = (targetPose.w.unit.toRadians(targetPose.w.value)
                - drivetrain.Theta.unit.toRadians(drivetrain.Theta.value)) % (2 * PI)

        if (rotFieldError > PI) rotFieldError -= 2 * PI

        return DriveWithVelocityCommand(
                singleAxisTargetLinVel(targetPose.y, drivetrain.Y),
                singleAxisTargetLinVel(targetPose.x, drivetrain.X),
                singleAxisTargetRotVel(Rotation(rotFieldError, UnnormalizedAngleUnit.RADIANS), Rotation())
        )
    }
}