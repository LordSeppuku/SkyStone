package org.firstinspires.ftc.teamcode.legacy.lib

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit

data class DriveToPositionCommand(val lateral: Distance = Distance(),
                                  val horizontal: Distance = Distance(),
                                  val rotation: Rotation = Rotation(),
                                  val power: Double = 0.3,
                                  val startTime: Double = 0.0)

data class DriveWithVelocityCommand(val lateral: Distance = Distance(),
                                    val horizontal: Distance = Distance(),
                                    val rotation: Rotation = Rotation(),
                                    val startTime: Double = 0.0)

data class Pose(val x: Distance = Distance(),
                val y: Distance = Distance(),
                val w: Rotation = Rotation())

data class Distance(val value: Double = 0.0, val unit: DistanceUnit = DistanceUnit.INCH)

data class Rotation(val value: Double = 0.0, val unit: UnnormalizedAngleUnit = UnnormalizedAngleUnit.DEGREES)