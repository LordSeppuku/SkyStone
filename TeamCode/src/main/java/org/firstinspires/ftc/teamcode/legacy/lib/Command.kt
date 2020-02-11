package org.firstinspires.ftc.teamcode.legacy.lib

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit
import org.firstinspires.ftc.teamcode.legacy.opmodes.auto.Alliance
import org.firstinspires.ftc.teamcode.legacy.opmodes.auto.OurOpMode
import kotlin.math.pow
import kotlin.math.sqrt

sealed class Command
data class DriveToPositionCommand(val alliance: Alliance = Alliance.Red,
                                  val lateral: Distance = Distance(),
                                  val horizontal: Distance = Distance(),
                                  val rotation: Rotation = Rotation(),
                                  val power: Double = 0.3,
                                  val busyCode: OurOpMode.() -> Unit = {
                                      telemetry.addLine().addData("Current runtime: ", runtime.seconds())
                                      telemetry.update()
                                  }) : Command() {
    inline fun <reified newAlliance : Alliance> toAlliance() =
            when (this.alliance) {
                is newAlliance -> this
                else -> DriveToPositionCommand(newAlliance::class.objectInstance!!,
                        lateral,
                        Distance(-horizontal.value, horizontal.unit),
                        Rotation(-rotation.value, rotation.unit))
            }

    val distance
        get() = Distance(sqrt(lateral[DistanceUnit.INCH].pow(2) + horizontal[DistanceUnit.INCH].pow(2)))
}

data class Pose(val x: Distance = Distance(),
                val y: Distance = Distance(),
                val w: Rotation = Rotation())

data class Distance(val value: Double = 0.0, val unit: DistanceUnit = DistanceUnit.INCH) {
    operator fun get(other: DistanceUnit) = other.fromUnit(unit, value)
}

data class Rotation(val value: Double = 0.0, val unit: UnnormalizedAngleUnit = UnnormalizedAngleUnit.DEGREES) {
    operator fun get(other: UnnormalizedAngleUnit) = other.fromUnit(unit, value)
}