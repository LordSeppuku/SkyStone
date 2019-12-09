package org.firstinspires.ftc.teamcode.legacy.lib

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit

data class AutoDriveCommand(val lateral: Distance = Distance(),
                            val horizontal: Distance = Distance(),
                            val rotation: Rotation = Rotation(),
                            val power: Double = 0.5)

data class Distance(val value: Double = 0.0, val unit: DistanceUnit = DistanceUnit.INCH)

data class Rotation(val value: Double = 0.0, val unit: UnnormalizedAngleUnit = UnnormalizedAngleUnit.DEGREES)